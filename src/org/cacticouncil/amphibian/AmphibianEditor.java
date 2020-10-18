package org.cacticouncil.amphibian;

import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;

import org.cacticouncil.amphibian.AmphibianComponent;
import org.cacticouncil.amphibian.AmphibianToggle;
import org.cacticouncil.amphibian.PaletteManager;
//import org.cef.browser.CefMessageRouter;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefMessageRouterHandler;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.cef.CefApp;
import org.cef.browser.CefBrowser;
import org.cef.CefClient;
import org.cef.CefSettings;


import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.*;

import com.intellij.ui.jcef.*;


/**
 * Created by exlted on 01-Mar-17.
 * Controls the actual Amphibian Editor
 */
public class  AmphibianEditor extends UserDataHolderBase implements FileEditor
{
    private String jarPalettePath = "palettes/";
    //The browser used by AmphibianEditor to show Droplet
    private JBCefBrowser browser = null;
    // Resources connected with this Editor tab
    private static VirtualFile file;
    private static Project proj;
    /**
     * If true, allows deselectNotify() to update document text
     */
    private boolean set = false;
    /**
     * Stores the settings for later usage, set ONLY during constructor
     */
    private String settings;
    /**
     * The string pulled from the document connected with file, used to update the internal code within Amphibian
     */
    private String code;
    private String mode;
    private boolean isBlocks = false;

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
        Document vFile = fManager.getDocument(file);
        mode = AmphibianComponent.getRelationMap().get(this.file.getExtension());
        settings = loadSettings(mode);

        JBCefApp.getInstance();
        JBCefClient client = JBCefApp.getInstance().createClient();
        CefLoadHandler myLoadHandler;



        browser = new JBCefBrowser(client, "file://" + AmphibianComponent.getPathname() + "plugin.html");
        while (browser.getCefBrowser().isLoading())
        {
            try { Thread.sleep(50); }
            catch (InterruptedException ignored) { }
        }


        CefMessageRouter msgRouter = CefMessageRouter.create();
        msgRouter.addHandler(new CefMessageRouterHandler() {

            //Deselct Notify JSQUERY BACK HERE
           @Override
           public boolean onQuery(CefBrowser cefBrowser, CefFrame cefFrame, long l, String s, boolean b, CefQueryCallback cefQueryCallback) {
               code = vFile.getText();
               //UpdateFile(s);
               if(s!=null)
               {
                   code = s;
               }
               Runnable r = () -> vFile.setText(code);
               WriteCommandAction.runWriteCommandAction(proj, r);

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
       }, true);

        client.getCefClient().addMessageRouter(msgRouter);

        client.addLoadHandler(myLoadHandler = new CefLoadHandler() {

            @Override
            public void onLoadingStateChange(CefBrowser cefBrowser, boolean b, boolean b1, boolean b2) {

            }

            @Override
            public void onLoadStart(CefBrowser cefBrowser, CefFrame cefFrame, CefRequest.TransitionType transitionType) {

            }

            @Override
            public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int i) {
                cefBrowser.executeJavaScript("initEditor(\"" + settings + "\", \"localuser\")",null,1);
                code = vFile.getText();
                cefBrowser.executeJavaScript("swapInEditor(\"" + (code == null ? "" : escapeJs(code)) +"\")", null, 0);
                set = true;
                isBlocks = true;
            }

            @Override
            public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {

            }
        }, browser.getCefBrowser());



        // FIXME
    /*  BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222", "--disable-web-security", "--allow-file-access-from-files");
        prefs.setLocalStorageEnabled(true);
        prefs.setApplicationCacheEnabled(true);
        browser.setPreferences(prefs);
        --browserView = new BrowserView(browser);
        --System.out.println(browser.getRemoteDebuggingURL());
       -- browser.addConsoleListener(consoleEvent -> handleConsoleEvent(consoleEvent.getMessage()));
        --browser.addLoadListener(new LoadListener()
        --{
        --    public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) { }
         --   public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) { }
        --    public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) { }
         --   public void onFailLoadingFrame(FailLoadingEvent failLoadingEvent) { }
         --   public void onDocumentLoadedInMainFrame(LoadEvent loadEvent) { }
          --  @Override
           -- public void onDocumentLoadedInFrame(FrameLoadEvent frameLoadEvent)
            {
            --    while (browser.isLoading())
                {
            --        try { Thread.sleep(50); }
             --       catch (InterruptedException ignored) { }
                }
             --   browser.executeJavaScript("initEditor(\"" + settings + "\", \"localuser\")");
             --   set = true;
           -- }
       -- });
      -- */
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
    public JComponent getComponent() { return browser.getComponent(); }//null; }//myComponent; }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() { return null; }//browserView; }

    @NotNull
    @Override
    public String getName() { return "Blocks"; }

    @Override
    public void setState(@NotNull FileEditorState state) { }

    @Override
    public boolean isModified() { return true; }

    @Override
    public boolean isValid() { return AmphibianToggle.getToggleState(); }

    // Called upon the selection of the AmphibianEditor tab; updates the settings, language, and code
    @Override
    public void selectNotify()
    {
        code = fManager.getInstance().getDocument(file).getText();
       //FIXME
        //System.out.println(code);
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

        if(set)
        {
        // FIXME
        /*  JSValue result = browser.executeJavaScriptAndReturnValue("swapOutEditor()");
            String code = FileDocumentManager.getInstance().getDocument(file).getText();
            if(!result.isNull())
                code = result.getStringValue();


            String finalCode = code;
            Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(finalCode);
            WriteCommandAction.runWriteCommandAction(proj, r);*/

            //handleConsoleEvent("CODE_UPDATE");

            //TESTING CODE//
            // Inject the query callback into JS
            //browser.executeJavaScript("cefQuery('Hello World')");
            browser.getCefBrowser().executeJavaScript("swapOutEditor()", null, 0);
        }
        set = true;
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
       // FIXME
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
