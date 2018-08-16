package com.cactiCouncil.IntelliJDroplet;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by exlted on 01-Mar-17.
 * Provides the DropletEditor when DropletToggle.toggleState is true
 */
public class DropletEditorProvider implements FileEditorProvider{
    /**
     * Runs when a new tab is opened, returns true if BOTH Droplet is toggled on
     * AND the VirtualFile to be opened is of the right file type
     * @param project
     * @param file
     * @return Whether or not a DropletEditor tab should be added to the current tab
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if(DropletComponent.relationMap.containsKey(file.getExtension())){
            return DropletToggle.toggleState;
        }
        return false;
    }

    /**
     * Calls the DropletEditor constructor
     * @param project
     * @param file
     * @return Newly created DropletEditor
     */
    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new DropletEditor(project, file);
    }

    /**
     * Called by IntelliJ at unknown time for unknown usage
     * @return
     */
    @NotNull
    @Override
    public String getEditorTypeId() {
        return "Droplet";
    }

    /**
     * Called by IntelliJ to determine what to do with the DropletEditor tab
     * @return
     */
    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
