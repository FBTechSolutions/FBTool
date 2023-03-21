package ic.unicamp.bm.graph.neo4j.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
@NodeEntity(label = "Fragment")
public class Fragment extends AbstractNode {

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
