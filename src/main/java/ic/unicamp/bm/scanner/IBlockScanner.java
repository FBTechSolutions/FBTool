package ic.unicamp.bm.scanner;

import java.nio.file.Path;
import java.util.Map;

public interface IBlockScanner {
    Map<String, String> retrieveAllValidBlocks(Path pathFile) throws Exception;
    Map<String, String> retrieveUpdatedBlocks(Path pathFile) throws Exception;
    Map<String, String> createInitialBlocks(Path pathFile);

}
