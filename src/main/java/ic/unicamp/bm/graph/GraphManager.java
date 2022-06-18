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
        } else {
          container.setUid(uid);
          daoOperation.addANode(container);
        }
        container.setGoParent(temp1);
        container.setGoChildren(temp2);
      }
      case RELATIONS -> {
        //core
        String uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isNotBlank(uid)) {
          container.setUid(uid);
          daoOperation.addNodeByJSON(container.createJson());
        }
        //relation (1)
        ContainerBlock parent = container.getGoParent();
        if (parent != null) {
          String uidParent = daoOperation.findUidFromAContainerNode(parent.getContainerId());
          if (StringUtils.isNotBlank(uidParent)) {
            parent.setUid(uidParent);
            daoOperation.addNodeByJSON(parent.createJson());
          }
        }
        //relation (2)
        List<ContainerBlock> children = container.getGoChildren();
        if (children != null) {
          for (ContainerBlock child : children) {
            String uidChild = daoOperation.findUidFromAContainerNode(child.getContainerId());
            if (StringUtils.isNotBlank(uidChild)) {
              child.setUid(uidChild);
              daoOperation.addNodeByJSON(child.createJson());
            }
          }
        }
      }
    }
  }

  @Override
  public void upsertContent(ContentBlock content, RecordState orientation) {
    switch (orientation) {
      case CONTENT -> {
        //core
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

        String contentUid = daoOperation.findUidFromAContentNode(content.getContentId());
        if (StringUtils.isBlank(contentUid)) {
          contentUid = daoOperation.addANode(content);
          if (StringUtils.isNotBlank(contentUid)) {
            content.setUid(contentUid);
          }
        } else {
          content.setUid(contentUid);
          daoOperation.addANode(content);
        }
        content.setBelongsTo(temp1);
        content.setAssociatedTo(temp2);
        content.setGoData(temp3);
        content.setGoPrevious(temp4);
        content.setGoNext(temp5);

      }
      case RELATIONS -> {
        //core
        String contentUid = daoOperation.findUidFromAContentNode(content.getContentId());
        if (StringUtils.isNotBlank(contentUid)) {
          content.setUid(contentUid);
          daoOperation.addNodeByJSON(content.createJson());
        }

        //relation (1)
        ContainerBlock container = content.getBelongsTo();
        if (container != null) {
          String containerUid = daoOperation.findUidFromAContainerNode(container.getContainerId());
          if (StringUtils.isNotBlank(containerUid)) {
            container.setUid(containerUid);
            daoOperation.addNodeByJSON(container.createJson());
          }
        }

        //relation (2)
        Data data = content.getGoData();
        if (data != null) {
          String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
          if (StringUtils.isNotBlank(dataUid)) {
            data.setUid(dataUid);
            daoOperation.addNodeByJSON(data.createJson());
          }
        }

      }
    }
  }

  @Override
  public void upsertData(Data data, RecordState orientation) {
    switch (orientation) {
      case CONTENT -> {
        ContentBlock temp1 = data.getBelongsTo();
        temp1.setBelongsTo(null);
        String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isBlank(dataUid)) {
          dataUid = daoOperation.addANode(data);
          if (StringUtils.isNotBlank(dataUid)) {
            data.setUid(dataUid);
          }
        } else {
          data.setUid(dataUid);
          daoOperation.addANode(data);
        }
        data.setBelongsTo(temp1);
      }
      case RELATIONS -> {
        //core
        String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isNotBlank(dataUid)) {
          data.setUid(dataUid);
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
