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


function getValueSync()
{
    getDataLock();
    var result = editor.getValue();
    releaseDataLock();
    return result;
}

function setValueSync(value)
{
    getDataLock();
    editor.setValue(value);
    releaseDataLock();
}

function logToServer(message)
{
    console.log("LOGGED:" + message);
}

function logEvent(message)
{
  logToServer(message + "[" + userid + "][" + Date.now() +"][" + getValueSync() + "]");
}

function showModalDialog(message)
{
    modal_content.textContent = message;
    modal.style.display = "block";
//    span.onclick = function() { modal.style.display = "none"; }
//    window.onclick = function(event) { if (event.target == modal) { modal.style.display = "none"; } }
}

function updateCode(code)
{
  console.log("CODE_UPDATE:" + getValueSync());
}

function createEditor(options)
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

function initEditor(settings, userstring)
{
    modal = document.getElementById('dialog');
    modal_content = document.getElementsByClassName("modal-content")[0];
    editor = createEditor(eval(settings));
    window.userid = userid = userstring;
    editor.setEditorState(true);
    logEvent("[init]");
}

function swapInEditor(code)
{
    modal.style.display = "none";
    editor.setEditorState(true);
    setValueSync(code);
    logEvent("[swap_to_blocks]");
    console.log("IN SWAP IN EDITOR");
    console.log("[tree] ");
    console.log(editor.session.tree);
}

function swapOutEditor()
{
    editor.setEditorState(false);
    //logEvent("[swap_to_text]");
    var returnValue = getValueSync();

    window.cefQuery({
        request: returnValue,

        //ON SUCCESS AND FAIL
    });
    //updateCode(returnValue);
    setValueSync(""); // Gross hack; I need to get the editor to reset... we have to set value to empty to do that.
    return returnValue;
}

function shutdownEditor()
{
    logEvent("[shutdown]");
}

function getViewBox(svg) {
    // Get the children elements
    var svgChildren = svg.children;
    var min = Number.MAX_SAFE_INTEGER;
    var max = -1;

    for (var i = 0; i < svgChildren.length; i++)
    {
        if (svgChildren[i].nodeName === "text")
        {
            // If it is a text element, get the y attribute to determine height
            var y = svgChildren[i].getAttribute("y");
            if (y < min)
            {
                // Set the min value
                min = y;
            }

            if (y > max)
            {
                // Set the max value
                max = y;
            }
        }
    }

    // Return the min and max heights to make viewbox from
    return [min, max];
}

function downloadSVG(node, filename, min, max) {
    // Create a new SVG element with the correct attributes and namespaces
    var svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
    svg.style.fontFamily = "Courier New, monospace";
    svg.style.fontSize = "15px";

    // Get the main canvas to get the width to scale the SVG to
    var canvas = document.getElementsByClassName("droplet-main-canvas")[0];
    var canvasWidth = canvas.style.width;

    // Remove the px that comes with style width to put into viewBox parameters
    var widthStr = canvasWidth.substring(0, canvasWidth.length - 2);

    // Add the node to the svg containing the block content
    svg.appendChild(node);

    // Set the viewbox to the appropriate parameters, give the min-y and height a buffer to not crop edges of the block too short
    svg.setAttribute("viewBox", "0 " + (min - 25) + " " + widthStr + " " + (max - min + 50));

    var serializer = new XMLSerializer();

    // Serialize the svg to download
    var source = serializer.serializeToString(svg);

    // Create a blob and then a blob URL to download the resulting image through a data URL
    var blob = new Blob([source], {type: "image/svg+xml;charset=utf-8"});
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

function downloadImageSVG(x, y)
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
        var vals = getViewBox(grandparentClone);
        // If we are in an svg group, download as an svg image
        downloadSVG(grandparentClone, 'image' + imageCount + '.svg', vals[0], vals[1]);
        imageCount++;
    }
}

function downloadAnimationSVG(x, y)
{
    // Get the element from the point
    var localElement = document.elementFromPoint(x, y);
    var parent = localElement.parentElement;
    var grandparent = parent.parentElement;

    // Clone the grandparent to avoid modifications
    var grandparentClone = grandparent.cloneNode(true);

    var vals = getViewBox(grandparentClone);

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
            downloadSVG(clone, 'animation' + animationCount + '.svg', vals[0], vals[1]);
            animationCount++;

            // Remove the child from the cloned node so that when the grandparent is downloaded, the child is not downloaded with it
            grandparentClone.removeChild(gpChildren[i]);
        }
    }

    // Download the grandparent as an individual block svg
    downloadSVG(grandparentClone, 'animation' + animationCount + '.svg', vals[0], vals[1]);
    animationCount++;
}
