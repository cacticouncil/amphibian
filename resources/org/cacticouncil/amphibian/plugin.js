var editor, options, userid, modal, modal_content, datalock = false, active = false, animationCount = 0, imageCount = 0;

sleep = function(ms)
{
    return new Promise(resolve => setTimeout(resolve, ms));
}

async function getDataLock()
{
    while (datalock)
        await sleep(20);

    datalock = true;
}

function releaseDataLock()
{
    datalock = false;
}


getValueSync = function()
{
    getDataLock();
    result = editor.getValue();
    releaseDataLock();
    return result;
}

setValueSync = function(value)
{
    getDataLock();
    editor.setValue(value);
    releaseDataLock();
}

logToServer = function(message)
{
    console.log("LOGGED:" + message);
}

logEvent = function(message)
{
  logToServer(message + "[" + userid + "][" + Date.now() +"][" + getValueSync() + "]");
}

showModalDialog = function(message)
{
    modal_content.textContent = message;
    modal.style.display = "block";
//    span.onclick = function() { modal.style.display = "none"; }
//    window.onclick = function(event) { if (event.target == modal) { modal.style.display = "none"; } }
}

updateCode = function(code)
{
  console.log("CODE_UPDATE:" + getValueSync());
}

createEditor = function(options)
{
  window.options = options;
  editor = new droplet.Editor(document.getElementById('droplet-editor'), options);

  editor.on('change', function() { updateCode(getValueSync()); });
  editor.on('pickblock', function(e) { logEvent("[pickblock][" + e + "]"); });
  editor.on('block-click', function() { logEvent("[block-click]"); });
  editor.on('selectpalette', function(e) { logEvent("[selectpalette][" + e + "]"); });
  editor.on('parseerror', function(e) { showModalDialog("You have a syntax error! Return to text mode to find it."); });
//  editor.on('parseerror', function(e) { logEvent("[parseerror][" + e + "]"); });

  return window.editor = editor;
}

initEditor = function(settings, userstring)
{
    modal = document.getElementById('dialog');
    modal_content = document.getElementsByClassName("modal-content")[0];
    editor = createEditor(eval(settings));
    window.userid = userid = userstring;
    editor.setEditorState(true);
    logEvent("[init]");
}

swapInEditor = function(code)
{
    modal.style.display = "none";
    editor.setEditorState(true);
    setValueSync(code);
    logEvent("[swap_to_blocks]");
    console.log("IN SWAP IN EDITOR");
    console.log("[tree] ");
    console.log(editor.session.tree);
}

swapOutEditor = function()
{
    editor.setEditorState(false);
    //logEvent("[swap_to_text]");
    returnValue = getValueSync();

    window.cefQuery({
        request: returnValue,

        //ON SUCCESS AND FAIL
    });
    //updateCode(returnValue);
    setValueSync(""); // Gross hack; I need to get the editor to reset... we have to set value to empty to do that.
    return returnValue;
}

shutdownEditor = function()
{
    logEvent("[shutdown]");
}

downloadSVG = function(node, filename)
{
    // Create a new SVG element with the correct attributes and namespaces
    var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
    svg.style.fontFamily = "Courier New, monospace";
    svg.style.fontSize = "15px";

    // Get the main canvas to get the height and width to scale the SVG to
    var canvas = document.getElementsByClassName("droplet-main-canvas")[0];
    var height = canvas.style.height;
    var width = canvas.style.width;

    // Remove the px that comes with style height and width to put into viewBox parameters
    var heightStr = height.substring(0, height.length - 2);
    var widthStr = width.substring(0, width.length - 2);
    svg.setAttribute("viewBox", "0 0 " + widthStr + " " + heightStr);

    // Add the node to the svg containing the block content
    svg.appendChild(node);

    var serializer = new XMLSerializer();

    // Serialize the svg to download
    var source = serializer.serializeToString(svg);

    // Create a blob and then a blob URL to download the resulting image through a data URL
    var blob = new Blob([source], {type:"image/svg+xml;charset=utf-8"});
    var blobURL = URL.createObjectURL(blob);

    // Create a temporary HTML element to download the SVG
    var download = document.createElement("a");

    // Set the download link to the blob URL
    download.href = blobURL;
    download.download = filename;

    // Add the element to the document
    document.body.appendChild(download);

    // Force click on the link to download the svg
    download.click();

    // Remove the new element from the document as it is no longer needed
    document.body.removeChild(download);
}

downloadImageSVG = function (x, y)
{
    // Get the element from the point
    var localElement = document.elementFromPoint(x, y);

    // Get the element's parent and grandparent
    var parent = localElement.parentElement;
    var grandparent = parent.parentElement;

    // Clone the grandparent so no modifications are made to the actual block svgs
    var grandparentClone = grandparent.cloneNode(true);

    if (grandparentClone.nodeName === "g")
    {
        // If we are in an svg group, download as an svg image
        downloadSVG(grandparentClone, 'image' + imageCount + '.svg');
        imageCount++;
    }
}

downloadAnimationSVG = function (x, y)
{
    // Get the element from the point
    var localElement = document.elementFromPoint(x, y);
    var parent = localElement.parentElement;
    var grandparent = parent.parentElement;

    // Clone the grandparent to avoid modifications
    var grandparentClone = grandparent.cloneNode(true);

    // Get a list of all the child nodes
    var gpChildren = grandparentClone.childNodes;

    for (var i = 0; i < gpChildren.length; i++)
    {
        if (gpChildren[i].outerHTML.startsWith("<g style=\"\">"))
        {
            // If the child is a <g style> element, it is a block nested inside
            // Clone the node
            var clone = gpChildren[i].cloneNode(true);

            // Download as an svg
            downloadSVG(clone, 'animation' + animationCount + '.svg');
            animationCount++;

            // Remove the child from the cloned node so that when the grandparent is downloaded, the child is not downloaded with it
            grandparentClone.removeChild(gpChildren[i]);
        }
    }

    // Download the grandparent as an individual block svg
    downloadSVG(grandparentClone, 'animation' + animationCount + '.svg');
    animationCount++;
}
