package ic.unicamp.fb.graph.neo4j.schema;

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

    @Property(name = "fragmentCode")
    private String fragmentCode; // e.g., 000000001

    @Property(name = "fragmentLabel")
    private String fragmentLabel;// e.g., CallButtons and DirectedCall

    @Override
    public Long getId() {
        return id;
    }
}
