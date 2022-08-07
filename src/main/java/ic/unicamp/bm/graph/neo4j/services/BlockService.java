package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import java.util.List;

public interface BlockService extends Service<Block> {

  Block getBlockByID(String productId);
  List<Block> getBlockByVCBlockState(DataState dataState);
}
