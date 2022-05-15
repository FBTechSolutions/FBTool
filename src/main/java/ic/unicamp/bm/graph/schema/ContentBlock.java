package ic.unicamp.bm.graph.schema;

import ic.unicamp.bm.scanner.BlockState;
import lombok.Getter;
import lombok.Setter;

public class ContentBlock {
  @Getter @Setter
  private String contentId;
  @Getter @Setter
  private BlockState currentState;
  @Getter @Setter
  private ContentBlock goPrevious;
  @Getter @Setter
  private ContentBlock goNext;
  @Getter @Setter
  private Data goData;
}
