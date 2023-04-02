package ic.unicamp.fb.graph.neo4j.schema;

import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;

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

@Setter
@Getter
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NodeEntity(label = "Product")
public class Product extends AbstractNode {

    @NonNull
    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "productId")
    private String productId;

    @Property(name = "productLabel")
    private String productLabel;

    @Relationship(type = "ASSOCIATED_TO", direction = OUTGOING)
    private List<ProductToFeature> associatedTo;

    @Override
    public Long getId() {
        return id;
    }
}


