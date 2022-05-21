package ic.unicamp.bm.graph.schema;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Feature {
  @Getter @Setter
  private String featureId;
  @Getter @Setter
  private String label;
  @Getter @Setter
  private Product belongsTo;
  @Getter @Setter
  private List<ContentBlock> associatedTo = new LinkedList<>();
}
