package ic.unicamp.bm.graph.schema;

public interface GraphAPI {

  void upsertContainer(ContainerBlock containerBlock);

  void upsertContent(ContentBlock contentBlock);

  void upsertData(Data data);

  void upsertFeature(Feature feature);

  void upsertProduct(Product product);

}
