package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Feature;

public interface BlockService extends Service<Block> {

  Block getBlockByID(String productId);
}
