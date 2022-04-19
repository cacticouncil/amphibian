package org.cacticouncil.amphibian;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by exlted on 01-Mar-17.
 * Updated by acomiskey Apr-22
 * Provides the AmphibianEditor when AmphibianToggle.toggleState is true
 */
public class AmphibianEditorProvider implements FileEditorProvider {
    /**
     * Runs when a new tab is opened, returns true if BOTH AmphibianToggle.toggleState = true
     * AND the VirtualFile to be opened is of the right file type (right now, just .java)
     * @param project: the IntelliJ project that's open
     * @param file: the file currently displayed by the editor
     * @return boolean: Whether or not an AmphibianEditor tab should be added to the current tab
     */
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if(AmphibianService.relationMap.containsKey(file.getExtension())){
            return AmphibianToggle.toggleState;
        }
        return false;
    }

    /**
     * Calls the AmphbianEditor constructor
     * @param project: the IntelliJ project that's open
     * @param file: the file currently displayed by the editor
     * @return FileEditor: Newly created AmphibianEditor
     */
    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
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
     * Called by IntelliJ to determine what to do with the AmphibianEditor tab
     * @return
     */
    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_BEFORE_DEFAULT_EDITOR;
    }
}
