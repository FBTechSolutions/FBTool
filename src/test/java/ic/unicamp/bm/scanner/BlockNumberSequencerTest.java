package ic.unicamp.bm.scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockNumberSequencerTest {
    @Test
    void createBlock() {
        for (int i = 0; i < 102; i++) {
            assertEquals(16, BlockNumberSequencer.getNextStringCode().length());
        }
    }
}