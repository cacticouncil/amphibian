package org.cacticouncil.amphibian;

import groovy.util.logging.Log;
import org.cef.CefSettings;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
public class AmphibianDisplayHandler implements org.cef.handler.CefDisplayHandler {

    private boolean loadingState;

    public AmphibianDisplayHandler(){
        loadingState = false;
    }

    public void startLoading(){loadingState = true;}
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
        //System.out.printf("Message: %s\nSource: %s\nLine: %d\nLevel: %s\n", message, source, line, level);

        if(loadingState && (message.equals("SWAPPED IN EDITOR") || message.equals("SWAPPED OUT EDITOR")))
        {
            loadingState = false;
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
