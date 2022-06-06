package ic.unicamp.bm.graph;

import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.doa.DAOOperation;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.List;

public class GraphManager implements GraphAPI {
  //private final DgraphClient dgraphClient;
  private final DAOOperation daoOperation;
  public GraphManager() {
    daoOperation = new DAOOperation();
/*    DgraphStub stub;
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint("https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql", "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    dgraphClient = new DgraphClient(stub);
    Version v = dgraphClient.checkVersion();
    System.out.println(v.getTag());*/
  }

  @Override
  public void upsertContainer(ContainerBlock container, RecordOrientation orientation){

    switch (orientation){
      case NONE -> {
        ContainerBlock temp1 = container.getGoParent();
        List<ContainerBlock> temp2 = container.getGoChildren();
        container.setGoParent(null);
        container.setGoChildren(null);
        String uid = daoOperation.addNode(container);
        if(!uid.isEmpty()){
          container.setUid(uid);
        }
        container.setGoParent(temp1);
        container.setGoChildren(temp2);
      }
      case RELATIONS -> {
        String uid = daoOperation.addNodeByJSON(container.createJson());
        if(!uid.isEmpty()){
          container.setUid(uid);
        }
      }
    }
  }

  @Override
  public void upsertContent(ContentBlock content) {
    daoOperation.addNode(content);
  }

  @Override
  public void upsertData(Data data) {
    //String record = String.valueOf(data.createJson());
    //createRecord(record);
  }

  @Override
  public void upsertFeature(Feature feature) {
   // String record = String.valueOf(feature.createJson());
   // createRecord(record);
  }

  @Override
  public void upsertProduct(Product product) {
   // String record = product.createJson().toString();
   // System.out.println(record);
    //createRecord(record);
  }

  private void createRecord(String record) {
  //  System.out.println(record);
  //  Transaction txn = dgraphClient.newTransaction();
  //  try {
  //    Mutation mu = Mutation.newBuilder()
   //       .setSetJson(ByteString.copyFromUtf8(record))
   //       .build();
  //    txn.mutate(mu);
  //    txn.commit();
  //  } catch (TxnConflictException ex) {
   //   System.out.println("Error here");
  //  } finally {
   //   txn.discard();
  //  }
  }
}
