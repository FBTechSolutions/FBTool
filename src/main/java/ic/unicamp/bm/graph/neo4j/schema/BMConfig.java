package ic.unicamp.bm.graph.neo4j.schema;

import java.util.concurrent.atomic.AtomicLong;

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
@NodeEntity(label = "BM")
public class BMConfig extends AbstractNode {

    @GeneratedValue
    @Id
    private Long id;

    @Property(name = "configId")
    private String configId;

    @Property(name = "lastBlockId")
    private long lastBlockId;


    @Override
    public Long getId() {
        return id;
    }
}


