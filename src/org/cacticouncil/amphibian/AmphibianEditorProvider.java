package org.cacticouncil.amphibian;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by exlted on 01-Mar-17.
 * Provides the SokoMakiEditor when SokoMakiToggle.toggleState is true
 */
public class AmphibianEditorProvider implements FileEditorProvider{
    /**
     * Runs when a new tab is opened, returns true if BOTH SokoMaki is toggled on
     * AND the VirtualFile to be opened is of the right file type
     * @param project
     * @param file
     * @return Whether or not a SokoMakiEditor tab should be added to the current tab
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if(AmphibianComponent.relationMap.containsKey(file.getExtension())){
            return AmphibianToggle.toggleState;
        }
        return false;
    }

    /**
     * Calls the SokoMakiEditor constructor
     * @param project
     * @param file
     * @return Newly created SokoMakiEditor
     */
    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        //System.out.println(file);
        return new AmphibianEditor(project, file);
    }

    /**
     * Called by IntelliJ at unknown time for unknown usage
     * @return
     */
    @NotNull
    @Override
    public String getEditorTypeId() {
        return "Amphibian";
    }

    /**
     * Called by IntelliJ to determine what to do with the SokoMakiEditor tab
     * @return
     */
    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
