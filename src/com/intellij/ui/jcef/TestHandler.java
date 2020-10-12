package com.intellij.ui.jcef;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;

public class TestHandler extends CefMessageRouterHandlerAdapter {



    private static String result;
    private static VirtualFile file;
    private static Project project;

    public TestHandler(VirtualFile file, Project project){
        this.file = file;
        this.project = project;
    }

    @Override
    public boolean onQuery(CefBrowser cefBrowser, CefFrame cefFrame, long l, String s, boolean b, CefQueryCallback cefQueryCallback) {
        System.out.println(s);
        result = s;
        UpdateFile();
        //Write a handler to change the file code
        return true;
    }

    public static boolean UpdateFile()
    {
        String code = FileDocumentManager.getInstance().getDocument(file).getText();
        if(result!=null)
        {
            code = result;
        }
        String finalCode = code;
        Runnable r = () -> FileDocumentManager.getInstance().getDocument(file).setText(finalCode);
        WriteCommandAction.runWriteCommandAction(project, r);

        //handleConsoleEvent("CODE_UPDATE");
        return true;
    }
}
