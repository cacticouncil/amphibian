var editor, options;

initEditor = function(settings, code)
{
    console.log("Settings: " + JSON.stringify(eval(settings)));
    console.log("Code: " + code);
    editor = createEditor(eval(settings));
    editor.setValue(code);
    console.log("Retrieved: " + editor.getValue());
}

createEditor = function(options)
{
  window.options = options;
//  $('#droplet-editor').html('');
  editor = new droplet.Editor(document.getElementById('droplet-editor'), options);
//  editor.setEditorState(true);
//  editor.aceEditor.getSession().setUseWrapMode(true);
//  editor.setValue('');
  editor.on('change', function()
  {
    return sessionStorage.setItem('code', editor.getValue());
  });
  return window.editor = editor;
}

initEditor( { 'mode': 'java', 'palette': [] }, "");
