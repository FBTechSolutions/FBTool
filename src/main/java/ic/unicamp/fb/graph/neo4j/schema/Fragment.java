package ic.unicamp.fb.graph.neo4j.schema;

import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
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

import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.Direction.OUTGOING;

@Setter
@Getter
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity(label = "Fragment")
public class Fragment extends AbstractNode {

    @NonNull
    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "fragmentId")
    private String fragmentId; // e.g., sF019F018

    @Property(name = "fragmentLabel")
    private String fragmentLabel;// e.g., CallButtons and DirectedCall

    @Relationship(type = "ASSOCIATED_TO", direction = OUTGOING)
    private List<FragmentToIndex> associatedTo;

    @Override
    public Long getId() {
        return id;
    }
}
