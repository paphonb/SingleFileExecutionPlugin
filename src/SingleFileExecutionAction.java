import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 *
 */
public class SingleFileExecutionAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(SingleFileExecutionAction.class.getSimpleName());

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFile sourceFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        new CMakeListWriter(project, sourceFile);
    }
}
