package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Block;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.ContainerToBlock;

import java.util.List;

public interface BlockService extends Service<Block> {

    Block getBlockByID(String blockId);
    //Block getBlockByID(String productId, int depth);

    List<Block> getBlockByVCBlockState(DataState vcBlockState);

    List<ContainerToBlock> getContainerToBlockRelations();

    List<Block> getBlocksByFile(String pathToFile);

    Block getFirstBlockByFile(String pathToFile);

    List<Block> getBlocksByFragment(String oldFeatureId);
}
