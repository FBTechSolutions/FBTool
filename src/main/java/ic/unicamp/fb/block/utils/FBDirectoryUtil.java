package ic.unicamp.fb.block.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FBDirectoryUtil {

    private static final Pattern pattern = Pattern.compile("\\.fb");//, Pattern.CASE_INSENSITIVE
    public static String BMDirectoryName = ".fb";

    public static Path getFBDirectoryAsPath() {
        String currentDirectory = System.getProperty("user.dir");
        return Paths.get(currentDirectory, BMDirectoryName);
    }

    public static File getFBDirectoryAsFile() {
        return new File(String.valueOf(FBDirectoryUtil.getFBDirectoryAsPath()));
    }

    public static boolean existsFBDirectory() {
        return FBDirectoryUtil.getFBDirectoryAsFile().exists();
    }

    public static boolean createFBDirectory() {
        File bmDirectoryAsFile = FBDirectoryUtil.getFBDirectoryAsFile();
        boolean file_was_created = bmDirectoryAsFile.mkdir();
        if (file_was_created) {
            setHiddenAttr(FBDirectoryUtil.getFBDirectoryAsPath());
            return true;
        }
        return false;
    }

    public static boolean removeFBDirectory() {
        if (FBDirectoryUtil.existsFBDirectory()) {
            try {
                FileUtils.deleteDirectory(FBDirectoryUtil.getFBDirectoryAsFile());
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

    public static boolean existNameInPath(Path filePath) {
        Matcher matcher = pattern.matcher(filePath.toString());
        return matcher.find();
    }

    public static void createFBContactFile() {
        File myFile =
                new File(getFBDirectoryAsPath().toString(), "support");
        try {
            FileUtils.writeStringToFile(myFile,
                    "Please if you have some bug contact with Junior using the jcupe.cas@gmail.com email",
                    "ISO-8859-1");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean existsFBContactFile() {
        File myFile =
                new File(getFBDirectoryAsPath().toString(), "support");
        return myFile.exists();
    }
}
