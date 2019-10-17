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
import java.beans.PropertyChangeListener;
import java.io.*;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

/**
 * Created by exlted on 01-Mar-17.
 * Controls the actual Sokomaki Editor
 */
public class DropletEditor extends UserDataHolderBase implements FileEditor
{
    private String jarPalettePath = "palettes/";
    //The browser and its view used by SokomakiEditor to show Droplet

    private Browser browser;
    private BrowserView browserView;

    // Resources connected with this Editor tab
    private VirtualFile file;
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
     * The string pulled from the document connected with file, used to update the internal code within Sokomaki
     */
    private String code;
    private String mode;
    private boolean isBlocks = false;

    private static String escapeJs(String data)
    {
        if (data == null)
            throw new NullPointerException("ERROR: Tried to escape null value.");

        data = data.replace("\\", "\\\\");
        data = data.replace("\"", "\\\"");
        data =  data.replace("\'", "\\\'");

        for (char index = 0; index < 32; index++)
            data = data.replace(index + "", "\\" + String.format("%03o", (int) index));

        return data;

    }

    private PaletteManager paletteManager = PaletteManager.getPaletteManager();

    private String loadSettings(String modeName)
    {
        StringBuilder palette = new StringBuilder();
        InputStream in;
        BufferedReader reader;

        if(modeName.startsWith("USR"))
        {
            modeName = modeName.replaceFirst("USR", "");
            try
            {
                in = new FileInputStream(paletteManager.paletteDirectory + modeName);
                reader = new BufferedReader(new InputStreamReader(in));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        else
        {
            modeName = jarPalettePath + modeName + ".json";
            in = this.getClass().getResourceAsStream(modeName);
            reader = new BufferedReader(new InputStreamReader(in));
        }
        try
        {
            while(reader.ready())
                palette.append(reader.readLine());

            reader.close();
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "(" + escapeJs(palette.toString()) + ")";
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) { return FileEditorState.INSTANCE; }

    private void handleConsoleEvent(String message)
    {
        if (message.startsWith("CODE_UPDATE"))
        {
            String target;
            String[] result = message.split(":");

            if (result == null || message.indexOf(':') == -1)
                return;

            if (result.length <= 1)
                target = "";
            else
                target = result[1];

            Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(target);
            WriteCommandAction.runWriteCommandAction(proj, r);
        }
        else if (message.startsWith("LOGGED"))
        {
            System.out.println("LOGGED: [" + message + "]");
        }
        else
        {
            System.out.println("Unrecognized console message: [" + message + "]");
        }
    }

    /**
     * Called by SokomakiEditorProvider to create a new SokomakiEditor tab
     * @param project The Project this SokomakiEditor is connected to
     * @param file The VirtualFile this SokomakiEditor is connected to
     */
    DropletEditor(Project project, VirtualFile file)
    {
        BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222", "--disable-web-security", "--allow-file-access-from-files");
        browser = new Browser();
        BrowserPreferences prefs = browser.getPreferences();
        prefs.setLocalStorageEnabled(true);
        prefs.setApplicationCacheEnabled(true);
        browser.setPreferences(prefs);
        this.proj = project;
        this.file = file;
        browserView = new BrowserView(browser);
        System.out.println(browser.getRemoteDebuggingURL());
        browser.addConsoleListener(consoleEvent -> handleConsoleEvent(consoleEvent.getMessage()));

        browser.loadURL("file://" + SokomakiComponent.pathname + "plugin.html");
        mode = SokomakiComponent.relationMap.get(this.file.getExtension());
        settings = loadSettings(mode);

        browser.addLoadListener(new LoadListener()
        {
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) { }
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) { }
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) { }
            public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) { }
            public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) { }

            @Override
            public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent)
            {
                while (browser.isLoading())
                {
                    try { Thread.sleep(50); }
                    catch (InterruptedException ignored) { }
                }

                browser.executeJavaScript("initEditor(\"" + settings + "\", \"localuser\")");
                set = true;
            }
        });

        while (browser.isLoading())
        {
            try { Thread.sleep(50); }
            catch (InterruptedException ignored) { }
        }
    }

    @NotNull
    @Override
    public JComponent getComponent() { return browserView; }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() { return browserView; }

    @NotNull
    @Override
    public String getName() { return "Blocks"; }

    @Override
    public void setState(@NotNull FileEditorState state) { }

    @Override
    public boolean isModified() { return true; }

    @Override
    public boolean isValid() { return SokomakiToggle.toggleState; }

    // Called upon the selection of the SokomakiEditor tab; updates the settings, language, and code
    @Override
    public void selectNotify()
    {
        code = FileDocumentManager.getInstance().getDocument(file).getText();
        if(!browser.isLoading())
        {
            browser.executeJavaScript("swapInEditor(\"" + (code == null ? "" : escapeJs(code)) +"\")");
            set = true;
            isBlocks = true;
        }
    }

     // Called by IntelliJ when tab loses selection
    @Override
    public void deselectNotify()
    {
        if(set)
        {
            JSValue result = browser.executeJavaScriptAndReturnValue("swapOutEditor()");
            String code = FileDocumentManager.getInstance().getDocument(file).getText();
            if(!result.isNull())
                code = result.getStringValue();

            String finalCode = code;
            Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(finalCode);
            WriteCommandAction.runWriteCommandAction(proj, r);
        }
        isBlocks = false;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener)  {

    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener)  {

    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() { return null; }

    @Override
    public void dispose()
    {
        browser.executeJavaScriptAndReturnValue("shutdownEditor()");
        browser.dispose();
    }
}
