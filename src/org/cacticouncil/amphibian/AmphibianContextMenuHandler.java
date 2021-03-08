package org.cacticouncil.amphibian;

// CEF Imports (via JetBrains SDK)
import org.cef.browser.*;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandler;

public class AmphibianContextMenuHandler implements CefContextMenuHandler
{
    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model)
    {
        if (model.getCount() > 0)
        {
            // Add a menu separator
            model.addSeparator();
        }

        // Add two new commands to the menu
        model.addItem(26501, "Export as Image");
        model.addItem(26502, "Export as Animation");
    }

    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags)
    {
        // Export as Image option
        if (commandId == 26501)
        {
            // Get the x and y of the click
            int x = params.getXCoord();
            int y = params.getYCoord();
            browser.executeJavaScript("downloadImageSVG(" + x + ", " + y + ")",null, 0);
            return true;
        }

        // Export as Animation option
        if (commandId == 26502)
        {
            // Get the x and y of the click
            int x = params.getXCoord();
            int y = params.getYCoord();
            browser.executeJavaScript("downloadAnimationSVG(" + x + ", " + y + ")",null, 0);
            return true;
        }

        return false;
    }

    public void onContextMenuDismissed(CefBrowser browser, CefFrame frame)
    {
        // Do nothing
    }
}
