package ic.unicamp.fb.block.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryUtil {

    public static Path getDirectoryAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory);
    }

    public static Path getOutDirectoryAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        Path path = Paths.get(currentDirectory);
        Path pathDirectory = path.getParent();
        return Paths.get(String.valueOf(pathDirectory), "fb_out");
    }
}
