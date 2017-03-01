import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by exlted on 01-Mar-17.
 * Controls the toggle button for the plugin
 */
public class DropletToggle extends ToggleAction {

    static boolean ToggleState;

    @Override
    public boolean isSelected(AnActionEvent e) {
        return ToggleState;
    }

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
