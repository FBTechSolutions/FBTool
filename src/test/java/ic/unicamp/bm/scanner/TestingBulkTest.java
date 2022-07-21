package ic.unicamp.bm.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import ic.unicamp.bm.graph.schema.ContainerBlock;
import io.dgraph.DgraphClient;
import io.dgraph.DgraphGrpc.DgraphStub;
import io.dgraph.DgraphProto;
import io.dgraph.DgraphProto.Response;
import io.dgraph.Transaction;
import io.dgraph.TxnConflictException;
import io.grpc.StatusRuntimeException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestingBulkTest {
  private static final ObjectMapper objectMapper = new ObjectMapper();
  @Test
  void createBulkOperation() {

    DgraphStub stub = null;
    try {
      stub = DgraphClient.clientStubFromCloudEndpoint(
              "https://blue-surf-590541.us-east-1.aws.cloud.dgraph.io/graphql",
              "YzQ2OGZmNTI5ZWI1MjBjZmQ3ZWIyYzY0NGU4M2JkYTE=");
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
    DgraphClient dgraphClient = new DgraphClient(stub);

    ContainerBlock container1 = new ContainerBlock();
    container1.setContainerId("DD1Bulk");
    ContainerBlock container2 = new ContainerBlock();
    container2.setContainerId("DD2Bulk");
    DgraphProto.Mutation mutation = null;
    try {
      List<ContainerBlock> list = new LinkedList<>();
      list.add(container1);
      list.add(container2);
      final String inputJson = objectMapper.writeValueAsString(list);//container1
      System.out.println(inputJson);
      mutation = DgraphProto.Mutation.newBuilder().setSetJson(ByteString.copyFromUtf8(inputJson))
          .build();
    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Error occurred while parsing." + ex.getMessage());
    }

    String uid = "";
    try (Transaction txn = dgraphClient.newTransaction()) {
      try {
        Response response = txn.mutate(mutation);
        txn.commit();
        for (String key : response.getUidsMap().keySet()) {
          uid = response.getUidsMap().get(key);
        }
      } catch (StatusRuntimeException | TxnConflictException dgraphException) {
        txn.discard();
        throw new RuntimeException(
            "Unable to persist the transaction." + dgraphException.getMessage());
      }
    }
  }
}
