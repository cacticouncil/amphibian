/**
 * Created by exlted on 01-Mar-17.
 * Updated by acomiskey Apr-22
 * Controls the actual Amphibian Editor
 */

package org.cacticouncil.amphibian;

// Java Language Imports
import java.beans.PropertyChangeListener;
import javax.swing.*;
import java.io.*;

// JetBrains / IntelliJ SDK Imports
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.*;

// CEF Imports (via JetBrains SDK)
/** CEF = Chromium Embedded Framework
 *  The Droplet editor tab is a Chromium tab running
 *  the regular Droplet code as if it were a web browser
 *  nested inside of IntelliJ
 */
import org.cef.browser.*;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefMessageRouterHandler;
import org.cef.network.CefRequest;
import com.intellij.ui.jcef.*;

public class  AmphibianEditor extends UserDataHolderBase implements FileEditor
{
    private static final String jarPalettePath = "palettes/";

    //The browser used by AmphibianEditor to show Droplet
    private final JBCefBrowser browser;

    // Resources connected with this Editor tab
    private static Project proj; // TODO: Should this be static???
    private final VirtualFile file;
    private final Document vFile;
    private boolean set = false; // If true, allows deselectNotify() to update document text
    private final String settings; // Stores settings for later use; set ONLY in constructor
    private String code; // String pulled from document/file; used to update code representation
    private boolean isBlocks = false;
    private final boolean isDebugMode;

    static FileDocumentManager fManager = FileDocumentManager.getInstance();

    /**
     * Called by AmphibianEditorProvider to create a new AmphibianEditor tab
     * @param project The Project this AmphibianEditor is connected to
     * @param file The VirtualFile this AmphibianEditor is connected to
     */
    public AmphibianEditor(Project project, VirtualFile file)
    {
        this.proj = project;
        this.file = file;
        this.vFile = FileDocumentManager.getInstance().getDocument(file);

        //tells the blocks tab what kind of file it has open
        String mode = AmphibianService.getRelationMap().get(this.file.getExtension());
        settings = loadSettings(mode);

        JBCefClient client = JBCefApp.getInstance().createClient();
        CefLoadHandler myLoadHandler;
        isDebugMode = java.lang.management.ManagementFactory.getRuntimeMXBean().
                                           getInputArguments().toString().contains("jdwp");

        browser = new JBCefBrowser(client, "file://" + AmphibianService.getPathname() + "plugin.html");
        while (browser.getCefBrowser().isLoading())
        {
            try { Thread.sleep(50); }
            catch (InterruptedException ignored) { }
        }

        // handler that accepts queries from plugin.js functions
        // and makes them available to the AmphibianEditor class
        CefMessageRouter msgRouter = CefMessageRouter.create();
        msgRouter.addHandler(new CefMessageRouterHandler() {

            /**
             * onQuery responds to the cefQuery in plugin.js's
             * swapOutEditor() function, which returns the code
             * as seen in the blocks tab. We only update the code
             * in the actual file if changes have been made since
             * we were last in the code tab.
             */
            @Override
            public boolean onQuery(CefBrowser cefBrowser, CefFrame cefFrame, long l, String s, boolean b, CefQueryCallback cefQueryCallback) {
                if(s!=null && !s.equals(code))
                {
                    code = s;
                    Runnable r = () -> { synchronized(vFile) { vFile.setText(code);} };
                    WriteCommandAction.runWriteCommandAction(proj, r);
                }
                //Write a handler to change the file code
                return true;
            }

            @Override
            public void onQueryCanceled(CefBrowser cefBrowser, CefFrame cefFrame, long l) {

            }

            @Override
            public void setNativeRef(String s, long l) {

            }

            @Override
            public long getNativeRef(String s) {
                return 0;
            }
        },true);

        client.getCefClient().addMessageRouter(msgRouter);

        // handler that boots up plugin.js structures
        myLoadHandler = new CefLoadHandler() {
            @Override
            public void onLoadingStateChange(CefBrowser cefBrowser, boolean b, boolean b1, boolean b2) {

            }

            @Override
            public void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType) {

            }

            /**
             * onLoadEnd initializes the plugin.js editor
             * when Amphibian is first loaded.
             */
            @Override
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i) {
                cefBrowser.executeJavaScript("initEditor(\"" + settings + "\", \"localuser\")",null,1);
                code = vFile.getText();
                cefBrowser.executeJavaScript("swapInEditor(\"" + (code == null ? "" : escapeJs(code)) +"\")", null, 0);
                set = true;
                isBlocks = true;
                if (isDebugMode)
                    browser.openDevtools();
            }

            @Override
            public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {

            }
        };
        client.addLoadHandler(myLoadHandler, browser.getCefBrowser());
    }

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel level) { return FileEditorState.INSTANCE; }

    /**
     * this logging system is supposed to accept
     * console.log() messages from plugin.js and operate
     * on them, but it doesn't work.
     */
    private void handleConsoleEvent(String message)
    {
        if (message.startsWith("CODE_UPDATE"))
        {
            String target;
            String[] result = message.split(":");
            System.out.println("Inside CODE UPDATE");
            for(int i = 0; i < result.length; i++) {
                System.out.println(result[i]);
            }

            if (result == null || message.indexOf(':') == -1)
                return;

            if (result.length <= 1)
                target = "";
            else{
                target = result[1];
                System.out.println("Printing target");
                System.out.println(target);
            }


            Runnable r = () -> fManager.getDocument(file).setText(target);
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

    @NotNull
    @Override
    public JComponent getComponent() { return browser.getComponent(); }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() { return null; }

    @NotNull
    @Override
    public String getName() { return "Blocks"; }

    @Override
    public void setState(@NotNull FileEditorState state) { }

    @Override
    public boolean isModified() { return true; }

    @Override
    public boolean isValid() { return AmphibianToggle.getToggleState(); }

    /**
     * selectNotify sends the code in the code tab
     * into the blocks tab. Currently, we send the
     * code every time we switch, because the blocks
     * tab resets itself to empty every time it goes
     * out of focus.
     */
    @Override
    public void selectNotify()
    {
        code = FileDocumentManager.getInstance().getDocument(file).getText();
        if(!browser.getCefBrowser().isLoading())

        {
            browser.getCefBrowser().executeJavaScript("swapInEditor(\"" + (code == null ? "" : escapeJs(code)) +"\")", null, 0);
            set = true;
            isBlocks = true;
        }
    }

    // Called by IntelliJ when tab loses selection
    @Override
    public void deselectNotify()
    {
        if (set)
        {
            /**
             * below line is commented out because it is unclear
             * if it has any real effect on the plugin. code variable
             * is also updated in onQuery, maybe this is deprecated?
             */
            //code = FileDocumentManager.getInstance().getDocument(file).getText();
            browser.getCefBrowser().executeJavaScript("swapOutEditor()", null, 0);
            set = true;
            isBlocks = false;
        }
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener)  { }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener)  { }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() { return null; }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() { return null; }

    @NotNull
    @Override
    public VirtualFile getFile() { return file; }

    @Override
    public void dispose()
    {
        browser.getCefBrowser().executeJavaScript("shutdownEditor()", null, 0);
        browser.dispose();
    }

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
}
