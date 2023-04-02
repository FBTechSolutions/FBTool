package ic.unicamp.fb.block.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class TempBMDirectoryUtil {

    public static String BMTemporalDirName = "temporal";

    public static Path getBMDirectoryAsPath() {
        String currentDirectory = BMDirectoryUtil.getBMDirectoryAsPath().toString();
        return Paths.get(currentDirectory, BMTemporalDirName);
    }

    public static File getBMDirectoryAsFile() {
        return new File(String.valueOf(TempBMDirectoryUtil.getBMDirectoryAsPath()));
    }

    public static boolean existsBmTemporalDirectory() {
        return TempBMDirectoryUtil.getBMDirectoryAsFile().exists();
    }

    public static boolean createBMTemporalDirectory() {
        File bmDirectoryAsFile = TempBMDirectoryUtil.getBMDirectoryAsFile();
        return bmDirectoryAsFile.mkdir();
    }

    public static boolean removeBMDirectory() {
        if (TempBMDirectoryUtil.existsBmTemporalDirectory()) {
            try {
                FileUtils.deleteDirectory(TempBMDirectoryUtil.getBMDirectoryAsFile());
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
