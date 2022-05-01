package ic.unicamp.bm.block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

public class BMDirectory {

    private static final Pattern pattern = Pattern.compile( "\\.bm");//, Pattern.CASE_INSENSITIVE
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
        File bmDirectoryAsFile = BMDirectory.getBMDirectoryAsFile();
        boolean file_was_created = bmDirectoryAsFile.mkdir();
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
    public static boolean existNameInPath(Path filePath){
        Matcher matcher = pattern.matcher(filePath.toString());
        return matcher.find();
    }
}
