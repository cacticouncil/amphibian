package com.cactiCouncil.IntelliJDroplet;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by exlted on 01-Mar-17.
 * Controls the toggle button for the plugin
 */
public class DropletToggle extends ToggleAction {

    /**
     * Stores whether or not Droplet is enabled
     */
    static boolean ToggleState;

    /**
     * Called by IntelliJ to know whether the toggle is on or not
     * @param e
     * @return
     */
    @Override
    public boolean isSelected(AnActionEvent e) {
        return ToggleState;
    }

    /**
     * Called by IntelliJ to update the current state of the toggle
     * @param e
     * @param state
     */
    @Override
    public void setSelected(AnActionEvent e, boolean state) {
        ToggleState = state;
        //Refreshes all opened editor tabs to open or close Droplet Editors from those tabs
        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(e.getProject());
        VirtualFile[] files = manager.getOpenFiles();
        manager.closeAllFiles();
        for (VirtualFile file : files) {
            manager.openFile(file, true);
        }
        if(ToggleState){
            //Do Stuff?
        }
        else{
            //Do Stuff?
        }
    }
}
