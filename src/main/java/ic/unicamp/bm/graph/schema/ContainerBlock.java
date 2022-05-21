package ic.unicamp.bm.graph.schema;

import ic.unicamp.bm.graph.schema.enums.ContainerType;
import ic.unicamp.bm.scanner.BlockState;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ContainerBlock {
  @Getter @Setter
  private String containerId;
  @Getter @Setter
  private ContainerType type;  //added

  @Getter @Setter
  private ContainerBlock goParent;
  @Getter @Setter
  private List<ContainerBlock> goChildren = new LinkedList<>();
  @Getter @Setter
  private ContentBlock goContent;
}
