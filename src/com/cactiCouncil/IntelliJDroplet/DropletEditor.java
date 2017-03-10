package com.cactiCouncil.IntelliJDroplet;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import com.cactiCouncil.IntelliJDroplet.*;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
/**
 * Created by exlted on 01-Mar-17.
 * Controls the actual Editor
 */
public class DropletEditor extends UserDataHolderBase implements FileEditor{
    private Browser browser;
    private BrowserView browserView;
    private VirtualFile file;
    private Project proj;

    DropletEditor(Project Proj, VirtualFile File){
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222", "--disable-web-security", "--allow-file-access-from-files");
        browser = new Browser();
        BrowserPreferences prefs = browser.getPreferences();
        prefs.setLocalStorageEnabled(true);
        prefs.setApplicationCacheEnabled(true);
        browser.setPreferences(prefs);
        proj = Proj;
        file = File;
        browserView = new BrowserView(browser);
        System.out.println(browser.getRemoteDebuggingURL());
        browser.addConsoleListener(consoleEvent -> System.out.println("Message: " + consoleEvent.getMessage()));
        browser.loadURL(DropletAppComp.filePath + "example.html");
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return browserView;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return browserView;
    }

    @NotNull
    @Override
    public String getName() {
        return "Droplet";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return DropletToggle.ToggleState;
    }

    @Override
    public void selectNotify() {
        String code = FileDocumentManager.getInstance().getDocument(file).getText();
        browser.executeJavaScript("this.editor.setValue(`" + code + "`)");
    }

    @Override
    public void deselectNotify() {
        com.teamdev.jxbrowser.chromium.JSValue blah = browser.executeJavaScriptAndReturnValue("(function(){return this.editor.getValue()})();");
        String code = FileDocumentManager.getInstance().getDocument(file).getText();
        if(!blah.isNull()){
            code = blah.getStringValue();
        }

        String finalCode = code;
        Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(finalCode);
        WriteCommandAction.runWriteCommandAction(proj, r);
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
        browser.dispose();
    }
}
