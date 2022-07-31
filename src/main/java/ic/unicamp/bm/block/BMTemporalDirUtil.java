package ic.unicamp.bm.block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;

public class BMTemporalDirUtil {

  public static String BMTemporalDirName = "temporal";

  public static Path getBMDirectoryAsPath() {
    String currentDirectory = BMDirectoryUtil.getBMDirectoryAsPath().toString();
    return Paths.get(currentDirectory, BMTemporalDirName);
  }

  public static File getBMDirectoryAsFile() {
    return new File(String.valueOf(BMTemporalDirUtil.getBMDirectoryAsPath()));
  }

  public static boolean existsBmTemporalDirectory() {
    return BMTemporalDirUtil.getBMDirectoryAsFile().exists();
  }

  public static boolean createBMTemporalDirectory() {
    File bmDirectoryAsFile = BMTemporalDirUtil.getBMDirectoryAsFile();
    return bmDirectoryAsFile.mkdir();
  }

  public static boolean removeBMDirectory() {
    if (BMTemporalDirUtil.existsBmTemporalDirectory()) {
      try {
        FileUtils.deleteDirectory(BMTemporalDirUtil.getBMDirectoryAsFile());
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
