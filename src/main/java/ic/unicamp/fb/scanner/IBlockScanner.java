package ic.unicamp.fb.scanner;

import java.nio.file.Path;
import java.util.Map;

public interface IBlockScanner {

    //Vector
    Map<String, String> retrieveAllValidBlocks(Path pathFile) throws Exception;

    Map<String, String> retrieveUpdatedBlocks(Path pathFile) throws Exception;

    Map<String, String> createInitialBlocks(Path pathFile);

    String cleanTagMarks(String content);
    // second param is the wrapper
}
