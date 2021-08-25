package org.cacticouncil.amphibian;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.ReadonlyStatusHandler;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import groovy.util.logging.Log;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;

import java.util.concurrent.Callable;

public class AmphibianDisplayHandler implements org.cef.handler.CefDisplayHandler {

    private boolean loadingState;
    private String direction;
    private final Document doc;
    private final VirtualFile vFile;
    private final CefBrowser cefBrowser;
    private static Project proj;

    //THE NAMES vFILE AND doc ARE SWITCHED IN AMPHIBIANEDITOR
    public AmphibianDisplayHandler(Document doc, CefBrowser cefBrowser, Project proj_, VirtualFile vFile){
        this.vFile = vFile;
        this.doc = doc;
        this.cefBrowser = cefBrowser;
        proj = proj_;
        loadingState = false;
    }

    public void startLoading(String direction){
        this.direction = direction;
        loadingState = true;
    }
    public boolean isLoading(){return loadingState;}

    /**
     * Handle a console message
     * @param browser The browser generating the event.
     * @param level Message log level
     * @param message Message text
     * @param source Message source file
     * @param line Source line number
     * @return true to stop the message from being output to the console.
     */
    public boolean onConsoleMessage(CefBrowser browser, CefSettings.LogSeverity level,
                                    String message, String source, int line){
        System.out.printf("Message: %s\nSource: %s\nLine: %d\nLevel: %s\n", message, source, line, level);

        //Handles waiting for return from JS, unclear whether this actually waits long enough due to threading concerns
        if(loadingState && message.equals("SWAPPED "+ direction + " EDITOR") )
        {
            loadingState = false;
        }

        //See also onQuery from AmphibianEditor, this is its replacement
        //We pass the code in a console message now (since it's getting logged there anyway)
        if(message.startsWith("[swap_to_code]")){
            String code = message.substring(14);
            System.out.println("CODE RECEIVED - using alternate path");

            Runnable r = () -> { synchronized(doc) { doc.setText(code); } };
            //System.out.println("Runnable assigned");
            
            // TODO This line causes a crash when switching modes rapidly, caused by an intelliJ crash. fix me please :)
            WriteCommandAction.runWriteCommandAction(proj, r);
            System.out.println("new code written");

        }
        return false;
    }


    /* EMPTY TEMPLATE FUNCTIONS */
    /**
     * Browser address changed.
     * @param browser The browser generating the event.
     * @param frame The frame generating the event.
     * @param url The new address.
     */
    public void onAddressChange(CefBrowser browser, CefFrame frame, String url){}

    /**
     * Browser title changed.
     * @param browser The browser generating the event.
     * @param title The new title.
     */
    public void onTitleChange(CefBrowser browser, String title){}

    /**
     * About to display a tooltip.
     * @param browser The browser generating the event.
     * @param text Contains the text that will be displayed in the tooltip.
     * @return true to handle the tooltip display yourself.
     */
    public boolean onTooltip(CefBrowser browser, String text){
        return false;
    }

    /**
     * Received a status message.
     * @param browser The browser generating the event.
     * @param value Contains the text that will be displayed in the status message.
     */
    public void onStatusMessage(CefBrowser browser, String value){}

    /**
     * Handle cursor changes.
     * @param browser The browser generating the event.
     * @param cursorType The new cursor type.
     * @return true if the cursor change was handled.
     */
    public boolean onCursorChange(CefBrowser browser, int cursorType){
        return false;
    }
}
