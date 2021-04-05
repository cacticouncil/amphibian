package org.cacticouncil.amphibian;

import com.intellij.openapi.project.Project;
import org.apache.commons.io.FilenameUtils;
import org.cef.browser.CefBrowser;
import org.cef.callback.CefBeforeDownloadCallback;
import org.cef.callback.CefDownloadItem;
import org.cef.callback.CefDownloadItemCallback;
import org.cef.handler.CefDownloadHandler;

public class AmphibianDownloadHandler implements CefDownloadHandler
{
    private final Project project;

    public AmphibianDownloadHandler(Project p)
    {
        project = p;
    }

    @Override
    public void onBeforeDownload(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, String s, CefBeforeDownloadCallback cefBeforeDownloadCallback)
    {
        // Get the base path of the project and add the file name
        String path = project.getBasePath() + "/" + s;

        // Ensure that the file separators are correct for the OS
        String correctPath = FilenameUtils.separatorsToSystem(path);

        // Continue with the download, but do not allow file dialog to pop up
        cefBeforeDownloadCallback.Continue(correctPath, false);
    }

    @Override
    public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback)
    {
        // Do nothing
    }
}
