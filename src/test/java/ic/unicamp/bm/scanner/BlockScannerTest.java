package ic.unicamp.bm.scanner;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockScannerTest {
    @Test
    void detectValidBlocks() {
        BlockScanner blockScanner = new BlockScanner();
        Path resourceDirectory1 = Paths.get("src","test","resources").resolve("sourcecodeblocks1.txt");
        Path resourceDirectory2 = Paths.get("src","test","resources").resolve("sourcecodeblocks2.txt");
        Path resourceDirectory3 = Paths.get("src","test","resources").resolve("sourcecodeblocks3.txt");
        try {
            Map<String, String> updatedBlocks = blockScanner.retrieveAllValidBlocks(resourceDirectory1);
            assertEquals(3, updatedBlocks.size());
            System.out.println("----- File 1");
            System.out.println(updatedBlocks.get("0000000000000000"));
            System.out.println(updatedBlocks.get("0000000000000001"));
            System.out.println(updatedBlocks.get("0000000000000002"));

            updatedBlocks = blockScanner.retrieveAllValidBlocks(resourceDirectory2);
            assertEquals(3, updatedBlocks.size());
            System.out.println("----- File 2");
            System.out.println(updatedBlocks.get("0000000000000000"));
            System.out.println(updatedBlocks.get("0000000000000001"));
            System.out.println(updatedBlocks.get("0000000000000002"));

            updatedBlocks = blockScanner.retrieveAllValidBlocks(resourceDirectory3);
            assertEquals(3, updatedBlocks.size());
            System.out.println("----- File 3");
            System.out.println(updatedBlocks.get("0000000000000000"));
            System.out.println(updatedBlocks.get("0000000000000001"));
            System.out.println(updatedBlocks.get("0000000000000002"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void detectAllBlocks() {
        BlockScanner blockScanner = new BlockScanner();
        Path resourceDirectory1 = Paths.get("src","test","resources").resolve("sourcecodeblocks4.txt");
        try {
            Map<String, String> updatedBlocks = blockScanner.retrieveAllBlocks(resourceDirectory1);
            assertEquals(6, updatedBlocks.size());
            for (String key: updatedBlocks.keySet()){
                System.out.println(updatedBlocks.get(key));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    void createInitialBlock() {
        BlockScanner.BLOCK_CONTENT_MAX_SIZE = 11;
        BlockScanner blockScanner = new BlockScanner();
        Path resourceDirectory1 = Paths.get("src","test","resources").resolve("sourcecodefile1.txt");
        Map<String, String> blocks = blockScanner.createInitialBlocks(resourceDirectory1);
        assertEquals(10, blocks.size());

        Path resourceDirectory2 = Paths.get("src","test","resources").resolve("sourcecodefile2.txt");
        blocks = blockScanner.createInitialBlocks(resourceDirectory2);
        assertEquals(13, blocks.size());
    }
}