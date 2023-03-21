package ic.unicamp.bm.graph.neo4j.schema;

import ic.unicamp.bm.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.bm.graph.neo4j.schema.relations.BlockToFeature;
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
@NodeEntity(label = "Block")
public class Block extends AbstractNode {

    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "blockId")
    private String blockId;

    @Property(name = "blockState")
    private BlockState blockState;

    @Property(name = "blockSha")
    private String blockSha;

    // version control
    @Property(name = "vcBlockState")
    private DataState vcBlockState;

    @Relationship(type = "GO_NEXT_BLOCK", direction = OUTGOING)
    private BlockToBlock goNextBlock;

    @Relationship(type = "ASSOCIATED_TO", direction = OUTGOING)
    private BlockToFeature associatedTo;

/*
  for now it is not necesary
  @Relationship(type = "ASSOCIATED_TO_DEFAULT_FEATURE", direction = Relationship.OUTGOING)
  private BlockToDefaultFeature associatedToDefaultFeature;*/

    @Override
    public Long getId() {
        return id;
    }
}
