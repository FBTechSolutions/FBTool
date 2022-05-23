package ic.unicamp.bm.graph;

import com.google.protobuf.ByteString;
import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Version;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import java.net.MalformedURLException;

public class GraphManager implements GraphAPI {
  private final DgraphClient dgraphClient;
  public GraphManager() {
    DgraphStub stub;
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint("https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql", "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    dgraphClient = new DgraphClient(stub);
    Version v = dgraphClient.checkVersion();
    System.out.println(v.getTag());
  }

  @Override
  public void upsertContainer(ContainerBlock container, int depth, RecordOrientation orientation){
    String record = container.createJson(depth,orientation).toString();
    createRecord(record);
  }

  @Override
  public void upsertContent(ContentBlock content) {
    String record = String.valueOf(content.createJson());
    createRecord(record);
  }

  @Override
  public void upsertData(Data data) {
    String record = String.valueOf(data.createJson());
    createRecord(record);
  }

  @Override
  public void upsertFeature(Feature feature) {
    String record = String.valueOf(feature.createJson());
    createRecord(record);
  }

  @Override
  public void upsertProduct(Product product) {
    String record = product.createJson().toString();
    System.out.println(record);
    createRecord(record);
  }

  private void createRecord(String record) {
    System.out.println(record);
    Transaction txn = dgraphClient.newTransaction();
    try {
      Mutation mu = Mutation.newBuilder()
          //.setCommitNow(true)
          .setSetJson(ByteString.copyFromUtf8(record))
          .build();
      txn.mutate(mu);
      txn.commit();
    } catch (TxnConflictException ex) {
      System.out.println("Error here");
    } finally {
      txn.discard();
    }
  }
}
