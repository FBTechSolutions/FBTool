package ic.unicamp.bm.graph;

import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.doa.DAOOperation;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

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
  public void upsertContainer(ContainerBlock container, RecordState orientation) {

    switch (orientation) {
      case CONTENT -> {
        ContainerBlock temp1 = container.getGoParent();
        List<ContainerBlock> temp2 = container.getGoChildren();
        container.setGoParent(null);
        container.setGoChildren(null);
        String uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isBlank(uid)) {
          uid = daoOperation.addANode(container);
          if (StringUtils.isNotBlank(uid)) {
            container.setUid(uid);
          }
        }
        container.setGoParent(temp1);
        container.setGoChildren(temp2);
      }
      case RELATIONS -> {
        String uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isNotBlank(uid)) {
          container.setUid(uid);
          daoOperation.addNodeByJSON(container.createJson());
        }
      }
    }
  }

  @Override
  public void upsertContent(ContentBlock content, RecordState orientation) {
    switch (orientation) {
      case CONTENT -> {
        //content
        ContainerBlock temp1 = content.getBelongsTo();
        Feature temp2 = content.getAssociatedTo();
        Data temp3 = content.getGoData();
        ContentBlock temp4 = content.getGoPrevious();
        ContentBlock temp5 = content.getGoNext();

        content.setBelongsTo(null);
        content.setAssociatedTo(null);
        content.setGoData(null);
        content.setGoPrevious(null);
        content.setGoNext(null);

        String uid = daoOperation.findUidFromAContentNode(content.getContentId());
        if (StringUtils.isBlank(uid)) {
          uid = daoOperation.addANode(content);
          if (StringUtils.isNotBlank(uid)) {
            content.setUid(uid);
          }
        }
        content.setBelongsTo(temp1);
        content.setAssociatedTo(temp2);
        content.setGoData(temp3);
        content.setGoPrevious(temp4);
        content.setGoNext(temp5);

        //data
        Data data = content.getGoData();
        ContentBlock temp6 = data.getBelongsTo();
        data.setBelongsTo(null);

        uid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isBlank(uid)) {
          uid = daoOperation.addANode(data);
          if (StringUtils.isNotBlank(uid)) {
            data.setUid(uid);
          }
        }
        data.setBelongsTo(temp6);
      }
      case RELATIONS -> {
        String uid = daoOperation.findUidFromAContentNode(content.getContentId());
        if (StringUtils.isNotBlank(uid)) {
          content.setUid(uid);
          daoOperation.addNodeByJSON(content.createJson());
        }
        ContainerBlock container = content.getBelongsTo();
        uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isNotBlank(uid)) {
          container.setUid(uid);
          daoOperation.addNodeByJSON(container.createJson());
        }

        Data data = content.getGoData();
        uid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isNotBlank(uid)) {
          content.setUid(uid);
          daoOperation.addNodeByJSON(data.createJson());
        }
      }
    }
  }

  @Override
  public void upsertData(Data data, RecordState orientation) {
    switch (orientation) {
      case CONTENT -> {
        //temp
        ContentBlock temp1 = data.getBelongsTo();
        temp1.setBelongsTo(null);
        String uid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isBlank(uid)) {
          uid = daoOperation.addANode(data);
          if (StringUtils.isNotBlank(uid)) {
            data.setUid(uid);
          }
        } else {
          data.setUid(uid);
          daoOperation.addANode(data);
        }
        data.setBelongsTo(temp1);
      }
      case RELATIONS -> {
        //core
        String uid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isNotBlank(uid)) {
          data.setUid(uid);
          daoOperation.addNodeByJSON(data.createJson());
        }
        //relations
        ContentBlock contentBlock = data.getBelongsTo();
        if (contentBlock != null) {
          String contentUid = daoOperation.findUidFromAContentNode(contentBlock.getContentId());
          if (StringUtils.isNotBlank(contentUid)) {
            contentBlock.setUid(contentUid);
            daoOperation.addNodeByJSON(contentBlock.createJson());
          }
        }
      }
    }
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
