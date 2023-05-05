package ic.unicamp.fb.graph.neo4j.schema.relations;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
@RelationshipEntity(type = "HAS_A")
public class FeatureToBitOrder {

    @Id
    @GeneratedValue
    private Long relationshipId;

    @StartNode
    private Feature startFeature;

    @EndNode
    private BitOrder endBitOrder;
}
