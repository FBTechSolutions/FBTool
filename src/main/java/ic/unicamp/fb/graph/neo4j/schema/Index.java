package ic.unicamp.fb.graph.neo4j.schema;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Setter
@Getter
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity(label = "Index")
@ToString
public class Index extends AbstractNode {

    @NonNull
    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "indexId")
    private int indexId;

    @Override
    public Long getId() {
        return id;
    }

}
