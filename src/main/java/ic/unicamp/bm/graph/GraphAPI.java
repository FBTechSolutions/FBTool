package ic.unicamp.bm.graph;

import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;

public interface GraphAPI {

  void upsertContainer(ContainerBlock containerBlock, int depth, RecordOrientation orientation);

  void upsertContent(ContentBlock contentBlock);

  void upsertData(Data data);

  void upsertFeature(Feature feature);

  void upsertProduct(Product product);

}
