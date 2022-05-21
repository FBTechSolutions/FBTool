package ic.unicamp.bm.graph.schema;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Product {
  @Getter @Setter
  private String productId;
  @Getter @Setter
  private String label;
  @Getter @Setter
  private List<Feature> associatedTo = new LinkedList<>();
}
