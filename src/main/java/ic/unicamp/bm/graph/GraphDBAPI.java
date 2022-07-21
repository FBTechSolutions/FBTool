package ic.unicamp.bm.graph;

import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.enums.DataState;
import java.util.LinkedList;
import java.util.List;

public interface GraphDBAPI {

  void upsertContainer(ContainerBlock containerBlock, NodePart element);

  void updateContainerInBulk(List<ContainerBlock> containerBlockList, NodePart element)
      throws Exception;
  void createContainersInBulk(List<ContainerBlock> containerBlockList, NodePart element)
      throws Exception;

  void upsertContent(ContentBlock contentBlock, NodePart element);

  void updateContentInBulk(ContentBlock contentBlock, NodePart element);
  void createContentInBulk(ContentBlock contentBlock, NodePart element);

  void upsertData(Data data, NodePart element);

  void updateDataInBulk(Data data, NodePart element);
  void createDataInBulk(Data data, NodePart element);

  void upsertFeature(Feature feature, NodePart element);

  void updateFeatureInBulk(Feature feature, NodePart element);
  void createFeatureInBulk(Feature feature, NodePart element);

  void upsertProduct(Product product, NodePart element);

  void updateProductInBulk(Product product, NodePart element);
  void createProductInBulk(Product product, NodePart element);


  List<Data> retrieveDataByState(DataState temporal);

  Product retrieveProductWithFeatures(String productId);

  List<String> insertObjects(LinkedList<Object> objects,NodePart none);
}
