package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;

public interface ContainerService extends Service<Container> {

    Container getContainerByID(String productId);

    Container getContainerByType(ContainerType type);
}
