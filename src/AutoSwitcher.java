import com.intellij.execution.RunManager;
import com.intellij.execution.RunManagerListener;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunManagerImpl;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.jetbrains.cidr.cpp.cmake.model.CMakeTarget;
import com.jetbrains.cidr.cpp.execution.CMakeAppRunConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.rmi.CORBA.Util;

public class AutoSwitcher implements ProjectComponent {
    private final Project project;
    private boolean busy = false;

    public AutoSwitcher(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull
    String getComponentName() {
        return "AutoSwitcher";
    }

    @Override
    public void projectOpened() {
        addRunConfigurationListener();
//        addConfigurationAddedListener();
        addFileEditorListeners();
    }

    private void addFileEditorListeners() {
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                selectTask(file);
            }

            private void selectTask(VirtualFile file) {
                Runnable selectTaskRunnable = () -> {
                    if (busy || file == null) {
                        return;
                    }
                    RunManagerImpl runManager = RunManagerImpl.getInstanceImpl(project);
                    RunnerAndConfigurationSettings racs = runManager.findConfigurationByName(Utilities.getConfigName(project, file));
                    if (racs == null) {
                        addToCMakeList(project, file);
                        return;
                    }
                    RunConfiguration configuration = racs.getConfiguration();
                    busy = true;
                    RunManager.getInstance(project).setSelectedConfiguration(
                            new RunnerAndConfigurationSettingsImpl(
                                    runManager,
                                    configuration,
                                    false
                            )
                    );
                    busy = false;

                };

                DumbService.getInstance(project).smartInvokeLater(selectTaskRunnable);
            }

            private void addToCMakeList(Project project, VirtualFile file) {
                String extension = file.getExtension();
                if (extension == null || !extension.startsWith("c")) return;
                if (Utilities.isAutoEnabled(project)) {
                    new CMakeListWriter(project, file);
                }
            }

            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                selectTask(event.getNewFile());
            }
        });
    }

    private void addRunConfigurationListener() {
        RunManagerImpl.getInstanceImpl(project).addRunManagerListener(new RunManagerListener() {
            @Override
            public void runConfigurationAdded(@NotNull RunnerAndConfigurationSettings settings) {
                RunConfiguration configuration = settings.getConfiguration();
                if (configuration instanceof CMakeAppRunConfiguration) {
                    FileEditor editor = FileEditorManager.getInstance(project).getSelectedEditor();
                    if (editor == null) return;
                    VirtualFile file = editor.getFile();
                    if (file == null) return;
                    CMakeTarget target = ((CMakeAppRunConfiguration) configuration).getCMakeTarget();
                    if (target == null) return;
                    String name = Utilities.getConfigName(project, file);
                    if (target.getName().equals(name)) {
                        RunManager.getInstance(project).setSelectedConfiguration(settings);
                    }
                }
            }

            @Override
            public void runConfigurationSelected() {
//                RunnerAndConfigurationSettings selectedConfiguration =
//                        RunManagerImpl.getInstanceImpl(project).getSelectedConfiguration();
//                if (selectedConfiguration == null) {
//                    return;
//                }
//                RunConfiguration configuration = selectedConfiguration.getConfiguration();
//                System.out.println("type: " + configuration.getClass().getName());
//                String pathToClassFile = ((CMakeAppRunConfiguration) configuration).getCMakeTarget().getBuildConfigurations().get(0).getSources().;
//                System.out.println("name: " + pathToClassFile);
//                if (busy || !(configuration instanceof TaskConfiguration)) {
//                    return;
//                }
//                busy = true;
//                String pathToClassFile = ((TaskConfiguration) configuration).getCppPath();
//                VirtualFile toOpen = project.getBaseDir().findFileByRelativePath(pathToClassFile);
//                if (toOpen != null) {
//                    TransactionGuard.getInstance().submitTransactionAndWait(() -> FileEditorManager.getInstance(project).openFile(
//                            toOpen,
//                            true
//                    ));
//                }
//                busy = false;
            }
        });
    }

}
