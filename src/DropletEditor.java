import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import javax.swing.text.DefaultStyledDocument;
import java.beans.PropertyChangeListener;
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
        proj = Proj;
        file = File;
        browser = new Browser();
        browserView = new BrowserView(browser);
        browser.loadHTML("<html><body><h1>Hello World!</h1></body></html>");
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

    }

    @Override
    public void deselectNotify() {

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

    }
}
