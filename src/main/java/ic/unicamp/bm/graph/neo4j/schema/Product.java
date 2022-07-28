package ic.unicamp.bm.graph.neo4j.schema;

import ic.unicamp.bm.graph.neo4j.schema.relations.ProductToFeature;
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

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
@NodeEntity(label = "Product")
public class Product extends AbstractNode{
  @GeneratedValue
  @Id
  private Long id;

  @Property(name = "productId")
  private String productId;

  @Property(name = "productLabel")
  private String productLabel;

  @Relationship(type = "ASSOCIATED_TO", direction = Relationship.OUTGOING)
  private List<ProductToFeature> associatedTo;
  @Override
  public Long getId() {
    return id;
  }
}


