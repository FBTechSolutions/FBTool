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
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.Raw;
import ic.unicamp.bm.graph.schema.enums.DataState;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto;
import io.dgraph.DgraphProto.Mutation;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import io.grpc.StatusRuntimeException;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DAOOperation {

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static Config config = ConfigFactory.load("application.conf");
  private static final DgraphStub stub;
  private static final DgraphClient dgraphClient;

  static {
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint(
          "https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql",
          "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
      dgraphClient = new DgraphClient(stub);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> String commitANode(final T element) {
    DgraphProto.Mutation mutation = getMutation(element);
    return applyMutation(mutation).get(0);
  }

  public <T> List<String> commitNodes(final List<T> elements) {
    DgraphProto.Mutation mutation = getMutation(elements);
    return applyMutation(mutation);
  }

  public String commitNodeByJSON(JsonObject json) {
    String payload = new Gson().toJson(json);
    DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
        .setSetJson(ByteString.copyFromUtf8(payload)).build();
    return applyMutation(mutation).get(0);
  }
  public List<String> commitNodesByJSON(List<JsonObject> jsons) {
    String payload = new Gson().toJson(jsons);
    DgraphProto.Mutation mutation = DgraphProto.Mutation.newBuilder()
        .setSetJson(ByteString.copyFromUtf8(payload)).build();
    return applyMutation(mutation);
  }

  private List<String> applyMutation(Mutation mutation) {
    List<String> uids = new LinkedList<>();
    try (Transaction txn = dgraphClient.newTransaction()) {
      try {
        Response response = txn.mutate(mutation);
        txn.commit();
        for (String key : response.getUidsMap().keySet()) {
          uids.add(response.getUidsMap().get(key));
        }
      } catch (StatusRuntimeException | TxnConflictException dgraphException) {
        txn.discard();
        throw new RuntimeException(
            "Unable to persist the transaction." + dgraphException.getMessage());
      }
      return uids;
    }
  }

  public <T> DgraphProto.Mutation getMutation(final T element) {
    try {
      final String inputJson = objectMapper.writeValueAsString(element);

      return DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(inputJson))
          .build();
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Error occurred while parsing." + ex.getMessage());
    }
  }

  public <T> DgraphProto.Mutation getMutation(final List<T> element) {
    try {
      final String inputJson = objectMapper.writeValueAsString(element);

      return DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(inputJson))
          .build();
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

      Raw<ContainerBlock> containerBlocks = objectMapper.readValue(response.getJson().toByteArray(),
          new TypeReference<Raw<ContainerBlock>>() {
          });
      if (containerBlocks.getRaw().isEmpty()) {
        return null;
      }
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
      ContainerBlock containerBlock = objectMapper.readValue(response.getJson().toByteArray(),
          ContainerBlock.class);
      return containerBlock;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }


  public ContentBlock getAContentBlockById(String id) {
    // Query
    String query = config.getString("queries.getAContentBlockById");
    Map<String, String> vars = Collections.singletonMap("$contentId", id);
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      // Deserialize

      Raw<ContentBlock> contentBlocks = objectMapper.readValue(response.getJson().toByteArray(),
          new TypeReference<Raw<ContentBlock>>() {
          });
      if (contentBlocks.getRaw().isEmpty()) {
        return null;
      }

      return contentBlocks.getRaw().get(0);
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }

  public Data getADataById(String id) {
    // Query
    String query = config.getString("queries.getADataById");
    Map<String, String> vars = Collections.singletonMap("$dataId", id);
    try {

      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      Raw<Data> dataRaw = objectMapper.readValue(response.getJson().toByteArray(),
          new TypeReference<Raw<Data>>() {
          });
      if (dataRaw.getRaw().isEmpty()) {
        return null;
      }

      return dataRaw.getRaw().get(0);
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }
  public List<Data> getDataByState(DataState state) {
    // Query
    String query = config.getString("queries.getDataByState");
    Map<String, String> vars = Collections.singletonMap("$state", state.toString());
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, vars);
      Raw<Data> records = objectMapper.readValue(response.getJson().toByteArray(),
          new TypeReference<>() {
          });
      return records.getRaw();
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }

  public List<Product> getAllProducts(String uid) {
    // Query
    String query = config.getString("queries.getAllProducts");
    try {
      DgraphProto.Response response = dgraphClient.newTransaction().queryWithVars(query, null);
      List<Product> products = objectMapper.readValue(response.getJson().toByteArray(),
          new TypeReference<List<Product>>() {
          });
      return products;
    } catch (Exception ex) {
      throw new RuntimeException("Result can not be cast into object." + ex);
    }
  }

  public <T> boolean deleteNode(final T element) {
    try (Transaction txn = dgraphClient.newTransaction()) {
      try {
        final DgraphProto.Mutation mutation = getDeleteMutation(element);
        txn.mutate(mutation);
        txn.commit();
        return true;

      } catch (StatusRuntimeException | TxnConflictException |
               JsonProcessingException dgraphException) {
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

  public String findUidFromAContainerNode(String containerId) {
    ContainerBlock containerBlock = getAContainerBlockById(containerId);
    if (containerBlock != null) {
      return containerBlock.getUid();
    }
    return null;
  }

  public String findUidFromAContentNode(String containerId) {
    ContentBlock contentBlock = getAContentBlockById(containerId);
    if (contentBlock != null) {
      return contentBlock.getUid();
    }
    return null;
  }

  public String findUidFromADataNode(String dataId) {
    Data data = getADataById(dataId);
    if (data != null) {
      return data.getUid();
    }
    return null;
  }
}
