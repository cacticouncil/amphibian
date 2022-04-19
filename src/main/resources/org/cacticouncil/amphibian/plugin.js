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

//these logs don't show up anywhere
//for the end user, because the log operator
//in AmphibianEditor doesn't work
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
}

//this function doesn't do anything.
//it is supposed to pass the blocks code through
//the console into the AmphibianEditor, but instead
//we use a Query
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
    logEvent("[swap_to_text]");
    returnValue = getValueSync();

    window.cefQuery({
        request: returnValue,
    });

    setValueSync(""); // Gross hack; I need to get the editor to reset... we have to set value to empty to do that.
    return returnValue;
}

shutdownEditor = function()
{
    logEvent("[shutdown]");
}
