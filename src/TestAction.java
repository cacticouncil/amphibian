import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
/**
 * Created by Jeremiah Blanchard on 8/31/2016.
 */
public class TestAction extends AnAction
{
    public void actionPerformed(AnActionEvent event) {
        String name = event.getData(PlatformDataKeys.VIRTUAL_FILE).getName();
        String fileText = event.getData(PlatformDataKeys.FILE_TEXT);
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Messages.showMessageDialog(project, "Hello! You are at " + name + "!\nThe text of this file is:\n\n" + fileText, "Information", Messages.getInformationIcon());
        DropletEditor dialog = new DropletEditor();
        dialog.pack();
        dialog.setVisible(true);
//        String txt= Messages.showInputDialog(project, "What is your name?", "Input your name", Messages.getQuestionIcon());
//        Messages.showMessageDialog(project, "Hello, " + txt + "!\n I am glad to see you.", "Information", Messages.getInformationIcon());
    }
}
