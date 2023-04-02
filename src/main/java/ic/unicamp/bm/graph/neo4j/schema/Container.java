package ic.unicamp.bm.graph.neo4j.schema;

import ic.unicamp.bm.graph.neo4j.schema.enums.ContainerType;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToContainer;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import static org.neo4j.ogm.annotation.Relationship.Direction.OUTGOING;

/**
 * A container that holds other containers or a block.
 */
@Setter
@Getter
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity(label = "Container")
public class Container extends AbstractNode {

    @NonNull
    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "containerId")
    private String containerId;

    @Property(name = "containerType")
    private ContainerType containerType;

    @Relationship(type = "PARENT_FROM", direction = OUTGOING)
    private List<ContainerToContainer> parentFrom;

    @Relationship(type = "POINT_TO", direction = OUTGOING)
    private ContainerToBlock block;

    @Override
    public Long getId() {
        return id;
    }
}
