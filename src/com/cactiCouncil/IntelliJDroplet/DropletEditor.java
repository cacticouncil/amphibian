package com.cactiCouncil.IntelliJDroplet;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.HashMap;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
/**
 * Created by exlted on 01-Mar-17.
 * Controls the actual Droplet Editor
 */
public class DropletEditor extends UserDataHolderBase implements FileEditor
{
    private String jarPalettePath = "palettes/";
    /**
     * The browser used by DropletEditor to show Droplet
     */
    private Browser browser;
    /**
     * The component connected to browser to render Droplet into the tab
     */
    private BrowserView browserView;

    private JPanel browserPanel;
    private JButton b1;
    /**
     * The file connected with this Editor tab
     */
    private VirtualFile file;
    /**
     * The project connected with this Editor tab
     */
    private Project proj;
    /**
     * If true, allows deselectNotify() to update document text
     */
    private boolean set = false;
    /**
     * Stores the settings for later usage, set ONLY during constructor
     */
    private String settings;
    /**
     * If false, allows settings to be set for future use, if true settings has already been set
     */
    private boolean setPalette = false;
    /**
     * The string pulled from the document connected with file, used to update the internal code within Droplet
     */
    private String code;
    private String mode;

    private static String escapeJs(String data)
    {
        data = data.replace("\\", "\\\\");
        data = data.replace("\"", "\\\"");
        data =  data.replace("\'", "\\\'");
        for (char index = 0; index < 32; index++)
            data = data.replace(index + "", "\\" + String.format("%03o", (int) index));

        return data;
    }

    private PaletteManager paletteManager = PaletteManager.getPaletteManager();

    String loadSettings(String modeName)
    {
        StringBuilder palette = new StringBuilder();
        InputStream in;
        BufferedReader reader;
        if(modeName.startsWith("USR")){
            modeName = modeName.replaceFirst("USR", "");
            try {
                in = new FileInputStream(paletteManager.paletteDirectory + modeName);
                reader = new BufferedReader(new InputStreamReader(in));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            modeName = jarPalettePath + modeName + ".json";
            in = this.getClass().getResourceAsStream(modeName);
            reader = new BufferedReader(new InputStreamReader(in));
        }
        try {
            while(reader.ready()){
                palette.append(reader.readLine());
            }
            reader.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "(" + escapeJs(palette.toString()) + ")";
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level)
    {
        return null;
    }

    void handleConsoleEvent(String message){
        System.out.println("Console: " + message);
    }

    /**
     * Called by DropletEditorProvider to create a new DropletEditor tab
     * @param proj The Project this DropletEditor is connected to
     * @param file The VirtualFile this DropletEditor is connected to
     */
    DropletEditor(Project proj, VirtualFile file){
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222", "--disable-web-security", "--allow-file-access-from-files");
        browser = new Browser();
        BrowserPreferences prefs = browser.getPreferences();
        prefs.setLocalStorageEnabled(true);
        prefs.setApplicationCacheEnabled(true);
        browser.setPreferences(prefs);
        this.proj = proj;
        this.file = file;
        browserView = new BrowserView(browser);
        System.out.println(browser.getRemoteDebuggingURL());
        browser.addConsoleListener(consoleEvent -> handleConsoleEvent(consoleEvent.getMessage()));
        browser.loadURL("file://" + DropletComponent.pathname + "plugin.html");
        mode = DropletComponent.relationMap.get(this.file.getExtension());
        settings = loadSettings(mode);
        browser.addLoadListener(new LoadListener() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {

            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {

            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {

            }

            @Override
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) {

            }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent) {
                while (browser.isLoading())
                    try { Thread.sleep(50); }
                    catch (InterruptedException ignored) { }

                browser.executeJavaScript("initEditor(\"" + settings + "\", \"" + (code == null ? "" : escapeJs(code)) + "\")");
                set = true;
            }

            @Override
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) {

            }
        });
    }

    /**
     * Called upon creation of tab (possibly other times)
     * Gives IntelliJ the component(s) that should show up in the tab
     * @return The components of the tab
     */
    @NotNull
    @Override
    public JComponent getComponent()
    {
        return browserView;
    }

    /**
     * Called upon creation of tab (possibly other times)
     * Gives IntelliJ the component that should initially be focused
     * @return The initially focused component
     */
    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return browserView;
    }

    /**
     * Called upon creation of tab to name the new tab
     * @return the name of the Tab for this editor
     */
    @NotNull
    @Override
    public String getName() {
        return "Droplet";
    }

    /**
     * Currently not used or implemented
     * @param state
     */
    @Override
    public void setState(@NotNull FileEditorState state) {

    }

    /**
     * Currently not used or implemented
     * @return
     */
    @Override
    public boolean isModified() {
        return false;
    }

    /**
     * Is called upon creation of the tab (possibly at other times)
     * Will cause IntelliJ to throw a warning if returns false
     * @return Whether the editor tab is valid or not
     */
    @Override
    public boolean isValid() {
        return DropletToggle.toggleState;
    }

    /**
     * Is called upon the selection of the DropletEditor tab.
     * Updates the tab's settings to the proper language and the code to the most up to date code
     * Stores the text of the related Document within "code" to allow for the tab to update upon the page finishing loading
     */
    @Override
    public void selectNotify()
    {
        code = FileDocumentManager.getInstance().getDocument(file).getText();
        if(!browser.isLoading())
        {
            browser.executeJavaScript("initEditor(\"" + settings + "\", \"" + (code == null ? "" : escapeJs(code)) + "\")");
            set = true;
        }
    }

    /**
     * Called by IntelliJ when tab loses selection
     */
    @Override
    public void deselectNotify() {
        if(set){
            JSValue result = browser.executeJavaScriptAndReturnValue("(function(){return this.editor.getValue()})();");
            String code = FileDocumentManager.getInstance().getDocument(file).getText();
            if(!result.isNull())
                code = result.getStringValue();

            String finalCode = code;
            Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(finalCode);
            WriteCommandAction.runWriteCommandAction(proj, r);
        }
    }

    /**
     * Currently neither implemented nor used
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    /**
     * Currently neither implemented nor used
     */
    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {

    }

    /**
     * Currently neither implemented nor used
     * @return
     */
    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    /**
     * Currently neither implemented nor used
     * @return
     */
    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    /**
     * Called by IntelliJ upon closing of editor tab
     * Disposes of browser
     */
    @Override
    public void dispose() {
        browser.dispose();
    }
}
