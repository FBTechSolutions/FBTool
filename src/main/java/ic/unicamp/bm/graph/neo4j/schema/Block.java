package ic.unicamp.bm.graph.neo4j.schema;

import ic.unicamp.bm.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFeature;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToRawData;
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
@NodeEntity(label = "Block")
public class Block extends AbstractNode{
  @GeneratedValue
  @Id
  private Long id;

  @Property(name = "blockId")
  private String blockId;

  @Property(name = "currentState")
  private BlockState currentState;

  @Relationship(type = "GO_NEXT_BLOCK", direction = Relationship.OUTGOING)
  private BlockToBlock goNextBlock;

  @Relationship(type = "GET_RAW_DATA", direction = Relationship.OUTGOING)
  private BlockToRawData getRawData;

  @Relationship(type = "ASSOCIATED_TO", direction = Relationship.OUTGOING)
  private BlockToFeature associatedTo;

  @Override
  public Long getId() {
    return id;
  }
}
