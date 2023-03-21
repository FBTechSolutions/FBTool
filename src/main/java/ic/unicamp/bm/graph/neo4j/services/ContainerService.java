package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.enums.ContainerType;

public interface ContainerService extends Service<Container> {

    Container getContainerByID(String productId);

    Container getContainerByType(ContainerType type);
}
