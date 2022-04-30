package ic.unicamp.bm.block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class BMDirectory {

    public static String BMDirectoryName = ".bm";

    public static Path getBMDirectoryAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, BMDirectoryName);
    }

    public static File getBMDirectoryAsFile() {
        return new File(String.valueOf(BMDirectory.getBMDirectoryAsPath()));
    }

    public static boolean existsBmDirectory() {
        return BMDirectory.getBMDirectoryAsFile().exists();
    }

    public static boolean createBMDirectory() {
        File xgit_dir_as_file = BMDirectory.getBMDirectoryAsFile();
        boolean file_was_created = xgit_dir_as_file.mkdir();
        if (file_was_created) {
            setHiddenAttr(BMDirectory.getBMDirectoryAsPath());
            return true;
        }
        return false;
    }

    public static boolean removeBMDirectory() {
        if (BMDirectory.existsBmDirectory()) {
            try {
                FileUtils.deleteDirectory(BMDirectory.getBMDirectoryAsFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    public static void setHiddenAttr(Path filePath) {
        try {
            Files.setAttribute(filePath, "dos:hidden", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
