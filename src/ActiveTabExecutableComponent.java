import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

public class ActiveTabExecutableComponent implements ApplicationComponent {

    private static final Logger logger = Logger.getInstance(ActiveTabExecutableComponent.class);

    private MessageBusConnection connection;

    @Override
    public void initComponent() {
        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        connection = bus.connect();
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new Listener());
    }

    @Override
    public void disposeComponent() {
        connection.disconnect();
    }

    class Listener implements FileEditorManagerListener {

        @Override
        public void selectionChanged(@NotNull FileEditorManagerEvent event) {
            ActionManager am = ActionManager.getInstance();
            am.getAction("AutoSingleFileExecutionAction").actionPerformed(new AnActionEvent(null, DataManager.getInstance().getDataContext(),
                    ActionPlaces.UNKNOWN, new Presentation(),
                    ActionManager.getInstance(), 0));
        }
    }
}
