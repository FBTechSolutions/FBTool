package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.RawData;

public interface RawDataService extends Service<RawData> {

  RawData getRawDataByID(String productId);
}
