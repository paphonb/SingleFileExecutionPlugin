import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtilRt;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class Utilities {

    public static String getConfigName(Project project, VirtualFile file) {
        String path = new File(project.getBasePath()).toURI().relativize(new File(file.getPath()).toURI()).getPath();
        int idx = StringUtilRt.lastIndexOf(path, '.', 0, path.length());
        String name = idx < 0 ? path : path.substring(0, idx);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            builder.append(filter(name.charAt(i)));
        }
        return builder.toString();
    }

    private static char filter(char ch) {
        if (ch >= 'A' && ch <= 'Z') return ch;
        if (ch >= 'a' && ch <= 'z') return ch;
        if (ch >= '0' && ch <= '9') return ch;
        return '_';
    }
}
