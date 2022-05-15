import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Request;
import io.dgraph.DgraphProto.Response;
import io.dgraph.DgraphProto.Version;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import java.net.MalformedURLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HelloDgraph {

  public static void main(String[] args) {
    DgraphStub stub = null;
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint("https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql", "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    DgraphClient dgraphClient = new DgraphClient(stub);
    Version v = dgraphClient.checkVersion();
    System.out.println(v.getTag());

    Transaction bestEffortTxn = dgraphClient.newReadOnlyTransaction();
    bestEffortTxn.setBestEffort(true);
    String query =
        "schema {\n"
            + "\n"
            + "}";
    Map<String, String> vars = Collections.singletonMap("$a", "P1");
    Response response = bestEffortTxn.queryWithVars(query, vars);
    System.out.println(response);

    //createData(dgraphClient);
  }

  private static void query(DgraphClient dgraphClient) {
    // Query
    String query =
        "query MyQuery {\n"
            + "  getProduct(productId: \"P1\") {\n"
            + "    productId\n"
            + "    label\n"
            + "    associatedTo {\n"
            + "      label\n"
            + "      featureId\n"
            + "    }\n"
            + "  }\n"
            + "}\n";
    Transaction transaction = dgraphClient.newTransaction();
    Request request = Request.newBuilder()
        .setQuery(query)
        .build();
    Response response = transaction.doRequest(request);
    //Map<String, String> vars = Collections.singletonMap("$a", "P1");
    //Transaction transaction = dgraphClient.newReadOnlyTransaction();
    //Response response = transaction.queryWithVars(query, vars);

// Deserialize
    Gson gson = new Gson();
    Product product = gson.fromJson(response.getJson().toStringUtf8(), Product.class);

// Print results
    System.out.printf("people found: %d\n", product.getAssociatedTo().size());
    product.getAssociatedTo().forEach(feature -> System.out.println(feature.getLabel()));
  }

  private static void createData(DgraphClient dgraphClient) {
    // Create data
    List<Feature> featureList = new LinkedList<>();
    Feature feature1 = new Feature();
    feature1.setFeatureId("F10");
    feature1.setLabel("Feature 10");
    featureList.add(feature1);
    Product product = new Product();
    product.setProductId("P10");
    product.setLabel("Product 10");
    product.setAssociatedTo(featureList);

    // Serialize it
    Gson gson = new Gson();
    String json = gson.toJson(product);

    Transaction txn = dgraphClient.newTransaction();
    try {
      // Run mutation
      Mutation mu = Mutation.newBuilder()
          .setSetJson(ByteString.copyFromUtf8(json))
          .build();
      txn.mutate(mu);
      txn.commit();
      System.out.println("End");
    } catch (TxnConflictException ex) {
      // Retry or handle exception.
      System.out.println("Error");
    } finally {
      // Clean up. Calling this after txn.commit() is a no-op
      // and hence safe.
      System.out.println("In the end do this");
      txn.discard();
    }
  }
}
