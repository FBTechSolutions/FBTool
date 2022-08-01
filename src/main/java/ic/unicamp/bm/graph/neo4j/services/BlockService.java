package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import java.util.List;

public interface BlockService extends Service<Block> {

  Block getBlockByID(String productId);
  List<Block> getBlockByRawState(DataState dataState);
}
