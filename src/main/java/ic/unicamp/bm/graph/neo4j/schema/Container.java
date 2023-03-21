package ic.unicamp.bm.graph.neo4j.schema;

import ic.unicamp.bm.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToContainer;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import static org.neo4j.ogm.annotation.Relationship.Direction.OUTGOING;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
@NodeEntity(label = "Container")
public class Container extends AbstractNode {

    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "containerId")
    private String containerId;

    @Property(name = "containerType")
    private ContainerType containerType;

    @Relationship(type = "GET_CONTAINERS", direction = OUTGOING)
    private List<ContainerToContainer> getContainers;

    @Relationship(type = "GET_FIRST_BLOCK", direction = OUTGOING)
    private ContainerToBlock getFirstBlock;

    @Override
    public Long getId() {
        return id;
    }
}
