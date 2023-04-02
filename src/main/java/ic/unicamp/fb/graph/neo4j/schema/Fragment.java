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
    private String fragmentId;

    @Property(name = "fragmentLabel")
    private String fragmentLabel;

    @Override
    public Long getId() {
        return id;
    }
}
