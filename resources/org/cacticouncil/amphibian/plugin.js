var editor, options, userid, modal, modal_content, datalock = false,  active = false;

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
    console.log("SWAPPED IN EDITOR");
}

swapOutEditor = function()
{
    editor.setEditorState(false);
    //logEvent("[swap_to_text]");
    returnValue = getValueSync();
    console.log( "[swap_to_code]"+returnValue)

    //this technique is deprecated, pass code by console message instead
    /*window.cefQuery({
        request: returnValue,

        //this call will probably fail with
        //          Unexpected call to CefQueryCallback_N::finalize()
        //ON SUCCESS AND FAIL
        onSuccess: function(response) {
            console.log("SUCCESS");
            console.log("SWAPPED OUT EDITOR");
        },
        onFailure: function(error_code, error_message) {
            console.log("FAILURE");
            console.log(error_code);
            console.log(error_message);
            console.log("SWAPPED OUT EDITOR");
        }
    });*/

    //updateCode(returnValue);
    setValueSync(""); // Gross hack; I need to get the editor to reset... we have to set value to empty to do that.
    //console.log("SWAPPED OUT EDITOR");
    return returnValue;
}

shutdownEditor = function()
{
    logEvent("[shutdown]");
}
