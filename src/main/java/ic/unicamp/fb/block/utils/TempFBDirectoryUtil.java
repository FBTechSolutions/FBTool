package ic.unicamp.fb.block.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TempFBDirectoryUtil {

    public static String FBTemporalDirName = "temporal";

    public static Path getFBDirectoryAsPath() {
        String currentDirectory = FBDirectoryUtil.getFBDirectoryAsPath().toString();
        return Paths.get(currentDirectory, FBTemporalDirName);
    }

    public static File getFBDirectoryAsFile() {
        return new File(String.valueOf(TempFBDirectoryUtil.getFBDirectoryAsPath()));
    }

    public static boolean existsFBTemporalDirectory() {
        return TempFBDirectoryUtil.getFBDirectoryAsFile().exists();
    }

    public static boolean createFBTemporalDirectory() {
        File fbDirectoryAsFile = TempFBDirectoryUtil.getFBDirectoryAsFile();
        return fbDirectoryAsFile.mkdir();
    }

    public static boolean removeFBDirectory() {
        if (TempFBDirectoryUtil.existsFBTemporalDirectory()) {
            try {
                FileUtils.deleteDirectory(TempFBDirectoryUtil.getFBDirectoryAsFile());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }
}
