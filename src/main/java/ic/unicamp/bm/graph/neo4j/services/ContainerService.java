package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.Feature;

public interface ContainerService extends Service<Container> {

  Container getContainerByID(String productId);
}
