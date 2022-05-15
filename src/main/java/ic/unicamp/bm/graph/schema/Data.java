package ic.unicamp.bm.graph.schema;

import ic.unicamp.bm.graph.schema.enums.DataState;
import lombok.Getter;
import lombok.Setter;

public class Data {
  @Getter @Setter
  private DataState currentState;
  @Getter @Setter
  private String sha;
}
