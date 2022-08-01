package ic.unicamp.bm.block.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryUtil {

  public static Path getDirectoryAsPath() {
    String currentDirectory = System.getProperty("user.dir");
    return Paths.get(currentDirectory);
  }
}
