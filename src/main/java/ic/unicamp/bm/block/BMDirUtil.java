package ic.unicamp.bm.block;

import ic.unicamp.bm.cli.util.logger.SplMgrLogger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

public class BMDirUtil {

    private static final Pattern pattern = Pattern.compile( "\\.bm");//, Pattern.CASE_INSENSITIVE
    public static String BMDirectoryName = ".bm";

    public static Path getBMDirectoryAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, BMDirectoryName);
    }

    public static File getBMDirectoryAsFile() {
        return new File(String.valueOf(BMDirUtil.getBMDirectoryAsPath()));
    }

    public static boolean existsBmDirectory() {
        return BMDirUtil.getBMDirectoryAsFile().exists();
    }

    public static boolean createBMDirectory() {
        File bmDirectoryAsFile = BMDirUtil.getBMDirectoryAsFile();
        boolean file_was_created = bmDirectoryAsFile.mkdir();
        if (file_was_created) {
            setHiddenAttr(BMDirUtil.getBMDirectoryAsPath());
            return true;
        }
        return false;
    }

    public static boolean removeBMDirectory() {
        if (BMDirUtil.existsBmDirectory()) {
            try {
                FileUtils.deleteDirectory(BMDirUtil.getBMDirectoryAsFile());
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

    public static void createBMContactFile() {
        File myFile =
            new File(getBMDirectoryAsPath().toString(), "support");
        try {
                FileUtils.writeStringToFile(myFile, "Please if you have some bug contact with Junior using the jcupe.cas@gmail.com email", "ISO-8859-1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean existsBMContactFile() {
        File myFile =
            new File(getBMDirectoryAsPath().toString(), "support");
        return myFile.exists();
    }
}
