package com.cactiCouncil.IntelliJDroplet;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.*;

import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.*;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
/**
 * Created by exlted on 01-Mar-17.
 * Controls the actual Droplet Editor
 */
public class DropletEditor extends UserDataHolderBase implements FileEditor
{
    private String jarPalettePath = "palettes/";
    //The browser and its view used by DropletEditor to show Droplet

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
     * The string pulled from the document connected with file, used to update the internal code within Droplet
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
//        System.out.println("Console: " + message);

        if(message.startsWith("LOGFAILED"))
        {
            String logdata = message.split(":")[1];
            DropletPluginState.getInstance().logList.add(logdata);
        }
        else if (message.startsWith("CODE_UPDATE"))
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
        else
        {
            System.out.println("Unrecognized console message: [" + message + "]");
        }
    }

    private void checkSettings()
    {
        DropletPluginState pluginState = DropletPluginState.getInstance();
        boolean shouldShowDialog = ((pluginState.ufid == null || pluginState.gatorlink == null) && pluginState.canLog);

        // Replay logs that failed to send if necessary / appropriate
        if (pluginState.canLog)
        {
            while (!pluginState.logList.isEmpty())
                browser.executeJavaScript("logToServer(\"" + escapeJs(pluginState.logList.pop()) + "\")");
        }

        while (shouldShowDialog)
        {
            DropletSettingsDialog dialog = new DropletSettingsDialog();
            dialog.show();
            if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE)
            {
                if ((pluginState.ufid == null || pluginState.gatorlink == null) && DropletPluginState.getInstance().canLog)
                {
                    int result = JOptionPane.showConfirmDialog(null, "It looks like you still haven't entered all of your information. Would you like to go back?", "Warning!", JOptionPane.YES_NO_OPTION);
                    shouldShowDialog = (result == JOptionPane.YES_OPTION);
                }
                else
                {
                    shouldShowDialog = false;
                    browser.executeJavaScript("logToServer(\"[REGISTRATION][" + escapeJs(pluginState.randomized) + "][" + escapeJs(pluginState.ufid) + "][" + escapeJs(pluginState.gatorlink) + "]\")");
                }
            }
            else
            {
                int result = JOptionPane.showConfirmDialog(null, "If you don't elect an option, this dialog will appear again. Would you like to go back?", "Warning!", JOptionPane.YES_NO_OPTION);
                shouldShowDialog = (result == JOptionPane.YES_OPTION);
            }
        }
    }

    /**
     * Called by DropletEditorProvider to create a new DropletEditor tab
     * @param project The Project this DropletEditor is connected to
     * @param file The VirtualFile this DropletEditor is connected to
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

        browser.loadURL("file://" + DropletComponent.pathname + "plugin.html");
        mode = DropletComponent.relationMap.get(this.file.getExtension());
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

                browser.executeJavaScript("initEditor(\"" + settings + "\", \"" + escapeJs(DropletPluginState.getInstance().randomized) +"\")");
                set = true;
            }
        });

        while (browser.isLoading())
        {
            try { Thread.sleep(50); }
            catch (InterruptedException ignored) { }
        }
        checkSettings();
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
    public boolean isValid() { return DropletToggle.toggleState; }

    // Called upon the selection of the DropletEditor tab; updates the settings, language, and code
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
