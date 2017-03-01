import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * Created by exlted on 01-Mar-17.
 * Provides the DropletEditor when DropletToggle.ToggleState is true
 */
public class DropletEditorProvider implements FileEditorProvider{
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        return DropletToggle.ToggleState;
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        return new DropletEditor(project, file);
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return "Droplet";
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
