package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;
import java.util.List;

public interface BlockService extends Service<Block> {

  Block getBlockByID(String productId);
  //Block getBlockByID(String productId, int depth);

  List<Block> getBlockByVCBlockState(DataState dataState);

  List<ContainerToBlock> getContainerToBlockRelations();

  List<Block> getBlocksByFile(String pathFile);

  Block getFirstBlockByFile(String pathFile);
}
