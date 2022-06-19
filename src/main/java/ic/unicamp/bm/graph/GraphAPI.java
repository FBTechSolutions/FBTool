package ic.unicamp.bm.graph;

import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.enums.DataState;
import java.util.List;

public interface GraphAPI {

  void upsertContainer(ContainerBlock containerBlock, RecordState none);

  void upsertContent(ContentBlock contentBlock, RecordState none);

  void upsertData(Data data, RecordState none);

  void upsertFeature(Feature feature);

  void upsertProduct(Product product);

  List<Data> retrieveDataByState(DataState temporal);
}
