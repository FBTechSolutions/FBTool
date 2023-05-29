package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.services.BlockService;

public class BlockUtil {
    public static Block retrieveOrCreateAStandardBlockBean(BlockService blockService, String blockId, DataState dataState, BlockState blockState, String blockSha) {
        Block fullBlock = blockService.getBlockByID(blockId);
        if (fullBlock == null) {
            fullBlock = new Block();
            fullBlock.setVcBlockState(dataState);
            fullBlock.setBlockState(blockState);
            fullBlock.setBlockSha(blockSha);
        }
        return fullBlock;
    }
    public static Block retrieveOrCreateABlockBeanByState(BlockService blockService, String blockId, DataState dataState, BlockState blockState) {
        Block fullBlock = blockService.getBlockByID(blockId);
        if (fullBlock == null) {
            fullBlock = new Block();
            fullBlock.setVcBlockState(dataState);
            fullBlock.setBlockState(blockState);
        }
        return fullBlock;
    }
}
