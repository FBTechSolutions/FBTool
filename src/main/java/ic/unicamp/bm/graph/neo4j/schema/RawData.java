package ic.unicamp.bm.graph.neo4j.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import ic.unicamp.bm.graph.schema.enums.DataState;
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
@NodeEntity(label = "RawData")
public class RawData extends AbstractNode{
  @GeneratedValue
  @Id
  private Long id;

  @Property(name = "dataId")
  private String dataId;

  @Property(name = "rawDataSha")
  private String rawDataSha;

  @Property(name = "currentState")
  private DataState currentState;

  @Override
  public Long getId() {
    return id;
  }

}
