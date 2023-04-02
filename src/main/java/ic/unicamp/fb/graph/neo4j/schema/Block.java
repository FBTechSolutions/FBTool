package ic.unicamp.fb.graph.neo4j.schema;

import ic.unicamp.fb.graph.neo4j.schema.enums.BlockState;
import ic.unicamp.fb.graph.neo4j.schema.enums.DataState;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToBlock;
import ic.unicamp.fb.graph.neo4j.schema.relations.BlockToFragment;
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
@NodeEntity(label = "Block")
public class Block extends AbstractNode {

    @NonNull
    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "blockId")
    private String blockId;

    @Property(name = "blockState")
    private BlockState blockState;

    @Property(name = "blockSha")
    private String blockSha;

    @Property(name = "vcBlockState")
    private DataState vcBlockState;

    @Relationship(type = "GO_NEXT_BLOCK", direction = OUTGOING)
    private BlockToBlock goNextBlock;

    @Relationship(type = "ASSOCIATED_TO", direction = OUTGOING)
    private BlockToFragment associatedTo;

    @Override
    public Long getId() {
        return id;
    }
}
