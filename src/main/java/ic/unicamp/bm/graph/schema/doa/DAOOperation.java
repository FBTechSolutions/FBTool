package ic.unicamp.bm.graph.schema.doa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.protobuf.ByteString;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.Raw;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class DAOOperation {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static Config config = ConfigFactory.load("application.conf");
  private static final DgraphStub stub;
  private static final DgraphClient dgraphClient;

  static {
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint("https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql", "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
      dgraphClient = new DgraphClient(stub);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> String addNode(final T element) {
    DgraphProto.Mutation mutation = getMutation(element);
    return applyMutation(mutation);
  }
  public String addNodeByJSON(JsonObject json) {
    String payload = new Gson().toJson(json);
    System.out.println("amigo3="+payload);
    DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(payload)).build();
    return applyMutation(mutation);
  }

  private String applyMutation(Mutation mutation) {
    String uid = "";
    try (Transaction txn = dgraphClient.newTransaction()) {
      try {
        Response response = txn.mutate(mutation);
        txn.commit();
        for(String key: response.getUidsMap().keySet()){
          uid = response.getUidsMap().get(key);
        }
      } catch (StatusRuntimeException | TxnConflictException dgraphException) {
        txn.discard();
        throw new RuntimeException("Unable to persist the transaction." + dgraphException.getMessage());
      }
      return uid;
    }
  }

  public <T> DgraphProto.Mutation getMutation(final T element) {
    try {
      final String inputJson = objectMapper.writeValueAsString(element);
      System.out.println(inputJson);
      return DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(inputJson)).build();
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Error occurred while parsing." + ex.getMessage());
    }
  }

  public Product getAProductById(String id) {
    // Query
    String query = config.getString("queries.getAProductById");
    Map<String, String> vars = Collections.singletonMap("$productId", id);
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      // Deserialize
      Product product = objectMapper.readValue(response.getJson().toByteArray(), Product.class);
      return product;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }

  }

  public Product getAProductByUid(String uid) {
    // Query
    String query = config.getString("queries.getAProductByUid");
    Map<String, String> vars = Collections.singletonMap("$personUid", uid);
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      // Deserialize
      Product product = objectMapper.readValue(response.getJson().toByteArray(), Product.class);
      return product;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }


  }

  public ContainerBlock getAContainerBlockById(String id) {
    // Query
    String query = config.getString("queries.getAContainerBlockById");
    Map<String, String> vars = Collections.singletonMap("$containerId", id);
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      // Deserialize

      Raw<ContainerBlock> containerBlocks = objectMapper.readValue(response.getJson().toByteArray(),new TypeReference<Raw<ContainerBlock>>() {});
      System.out.println(containerBlocks.getRaw().get(0).toString());
      return containerBlocks.getRaw().get(0);
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }

  }

  public ContainerBlock getAContainerBlockByUid(String uid) {
    // Query
    String query = config.getString("queries.getAContainerBlockById");
    Map<String, String> vars = Collections.singletonMap("$uid", uid);
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      // Deserialize
      ContainerBlock containerBlock = objectMapper.readValue(response.getJson().toByteArray(), ContainerBlock.class);
      return containerBlock;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }

  public List<Product> getAllProducts(String uid) {
    // Query
    String query = config.getString("queries.getAllProducts");
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, null);
      // Deserialize
      List<Product> products = objectMapper.readValue(response.getJson().toByteArray(),new TypeReference<List<Product>>(){});
      return products;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }

  public <T> boolean deleteNode(final T element)  {
    try (Transaction txn = dgraphClient.newTransaction()) {
      try {
        final DgraphProto.Mutation mutation = getDeleteMutation(element);
        txn.mutate(mutation);
        txn.commit();
        return true;

      } catch (StatusRuntimeException | TxnConflictException | JsonProcessingException dgraphException) {
        txn.discard();
        throw new RuntimeException("Unable to persist the transaction ", dgraphException);
      }
    }
  }

  private <T> DgraphProto.Mutation getDeleteMutation(final T element)
      throws JsonProcessingException {
    final String inputJson = objectMapper.writeValueAsString(element);
    return DgraphProto.Mutation.newBuilder()
        .setDeleteJson(ByteString.copyFromUtf8(inputJson))
        .build();
  }
}
