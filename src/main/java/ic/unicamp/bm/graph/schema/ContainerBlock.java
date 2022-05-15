package ic.unicamp.bm.graph.schema;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ContainerBlock {
  @Getter @Setter
  private String containerId;
  @Getter @Setter
  private ContainerBlock goParent;
  @Getter @Setter
  private List<ContainerBlock> goChildren;
  @Getter @Setter
  private ContentBlock goContent;
}
