import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;

public class AutoSingleFileExecutionAction extends SingleFileExecutionAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            Project project = e.getRequiredData(CommonDataKeys.PROJECT);
            SingleFileExecutionConfig config = SingleFileExecutionConfig.getInstance(project);
            if (config.automaticallySwitch)
                super.actionPerformed(e);
        } catch (AssertionError ignored) {

        }
    }
}
