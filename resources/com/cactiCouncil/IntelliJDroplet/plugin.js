var editor, options, userid;

logToServer = function(message)
{
    http = new XMLHttpRequest();
    url = 'https://dropletplugin.eastus.cloudapp.azure.com:4443/log_droplet?' + encodeURIComponent(message);
    http.open("GET", url);
    http.send();

    http.onreadystatechange = function()
    {
        if (http.readyState === 4 && http.status !== 200)
            console.log("LOGFAILED:" + message);
    }
}

logEvent = function(message)
{
  logToServer(message + "[" + userid + "][" + Date.now() +"][" + editor.getValue() + "]");
}

updateCode = function(code)
{
  console.log("CODE_UPDATE:" + editor.getValue());
}

createEditor = function(options)
{
  window.options = options;
  editor = new droplet.Editor(document.getElementById('droplet-editor'), options);

  editor.on('change', function() { updateCode(editor.getValue()); });
  editor.on('pickblock', function(e) { logEvent("[pickblock][" + e + "]"); });
  editor.on('block-click', function() { logEvent("[block-click]"); });
  editor.on('selectpalette', function(e) { logEvent("[selectpalette][" + e + "]"); });
  editor.on('parseerror', function(e) { logEvent("[parseerror][" + e + "]"); });

  return window.editor = editor;
}

initEditor = function(settings, userstring)
{
    editor = createEditor(eval(settings));
    window.userid = userid = userstring;
    logEvent("[init]");
}

swapInEditor = function(code)
{
    editor.setValue(code);
    editor.setEditorState(true);
    logEvent("[swap_to_blocks]");
}

swapOutEditor = function()
{
    editor.setEditorState(false);
    logEvent("[swap_to_text]");
    return editor.getValue();
}

shutdownEditor = function()
{
    logEvent("[shutdown]");
}
