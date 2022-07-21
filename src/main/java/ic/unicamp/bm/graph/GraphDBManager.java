package ic.unicamp.bm.graph;

import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.schema.ContainerBlock;
import ic.unicamp.bm.graph.schema.ContentBlock;
import ic.unicamp.bm.graph.schema.Data;
import ic.unicamp.bm.graph.schema.Feature;
import ic.unicamp.bm.graph.schema.Product;
import ic.unicamp.bm.graph.schema.doa.DAOOperation;
import ic.unicamp.bm.graph.schema.enums.DataState;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GraphDBManager implements GraphDBAPI {

  private final DAOOperation daoOperation;

  public GraphDBManager() {
    daoOperation = new DAOOperation();
  }

  @Override
  public void upsertContainer(ContainerBlock container, NodePart element) {
    switch (element) {
      case VERTEX -> {
        ContainerBlock temp1 = container.getGoParent();
        List<ContainerBlock> temp2 = container.getGoChildren();
        container.setGoParent(null);
        container.setGoChildren(null);
        String uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isBlank(uid)) {
          uid = daoOperation.commitANode(container);
          if (StringUtils.isNotBlank(uid)) {
            container.setUid(uid);
          }
        } else {
          container.setUid(uid);
          daoOperation.commitANode(container);
        }
        container.setGoParent(temp1);
        container.setGoChildren(temp2);
      }
      case EDGES -> {
        //core
        String uid = daoOperation.findUidFromAContainerNode(container.getContainerId());
        if (StringUtils.isNotBlank(uid)) {
          container.setUid(uid);
          daoOperation.commitNodeByJSON(container.createJson());
        }
        //relation (1)
        ContainerBlock parent = container.getGoParent();
        if (parent != null) {
          String uidParent = daoOperation.findUidFromAContainerNode(parent.getContainerId());
          if (StringUtils.isNotBlank(uidParent)) {
            parent.setUid(uidParent);
            daoOperation.commitNodeByJSON(parent.createJson());
          }
        }
        //relation (2)
        List<ContainerBlock> children = container.getGoChildren();
        if (children != null) {
          for (ContainerBlock child : children) {
            String uidChild = daoOperation.findUidFromAContainerNode(child.getContainerId());
            if (StringUtils.isNotBlank(uidChild)) {
              child.setUid(uidChild);
              daoOperation.commitNodeByJSON(child.createJson());
            }
          }
        }
      }
    }
  }

  @Override
  public void updateContainerInBulk(List<ContainerBlock> containerBlockList, NodePart element)
      throws Exception {
    switch (element) {
      case VERTEX -> {
        upsertOnlyContainerData(containerBlockList);
      }
      case EDGES -> {
        List<JsonObject> jsons = new LinkedList<>();
        for (ContainerBlock containerBlock : containerBlockList) {
          //relation (1)
          ContainerBlock parent = containerBlock.getGoParent();
          if (parent != null) {
            addContainerToJSONList(jsons, parent);
          }
          //relation (2)
          List<ContainerBlock> children = containerBlock.getGoChildren();
          if (children != null) {
            for (ContainerBlock child : children) {
              addContainerToJSONList(jsons, child);
            }
          }
        }
        daoOperation.commitNodesByJSON(jsons);
      }
      default -> throw new IllegalStateException("Unexpected value: " + element);
    }
  }

  private void addContainerToJSONList(List<JsonObject> jsons, ContainerBlock containerBlock) throws Exception {
    if (StringUtils.isBlank(containerBlock.getUid())) {
      String uidParent = daoOperation.findUidFromAContainerNode(containerBlock.getContainerId());
      if (StringUtils.isNotBlank(uidParent)) {
        containerBlock.setUid(uidParent);
      } else {
        throw new Exception("Not Found parent");
      }
    }
    jsons.add(containerBlock.createJson());
  }

  private void upsertOnlyContainerData(List<ContainerBlock> containerBlockList) {
    List<ContainerBlock> list = new LinkedList<>();
    for (ContainerBlock containerBlock : containerBlockList) {
      containerBlock.setGoParent(null);
      containerBlock.setGoChildren(null);
      list.add(containerBlock);
    }
    daoOperation.commitNodes(list);
  }

  @Override
  public void createContainersInBulk(List<ContainerBlock> containerBlockList, NodePart element)
      throws Exception {
    switch (element) {
      case VERTEX -> {
        upsertOnlyContainerData(containerBlockList);
      }
      case EDGES -> {
        List<JsonObject> jsons = new LinkedList<>();
        for (ContainerBlock containerBlock : containerBlockList) {
          //relation (1)
          ContainerBlock parent = containerBlock.getGoParent();
          if (parent != null) {
            if (StringUtils.isBlank(parent.getUid())) {
              throw new Exception("Not Found parent");
            }
            jsons.add(parent.createJson());
          }
          //relation (2)
          List<ContainerBlock> children = containerBlock.getGoChildren();
          if (children != null) {
            for (ContainerBlock child : children) {
              if (StringUtils.isBlank(child.getUid())) {
                throw new Exception("Not Found parent");
              }
              jsons.add(child.createJson());
            }
          }
        }
        daoOperation.commitNodesByJSON(jsons);
      }
    }
  }

  @Override
  public void upsertContent(ContentBlock content, NodePart element) {
    switch (element) {
      case VERTEX -> {
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
          contentUid = daoOperation.commitANode(content);
          if (StringUtils.isNotBlank(contentUid)) {
            content.setUid(contentUid);
          }
        } else {
          content.setUid(contentUid);
          daoOperation.commitANode(content);
        }
        content.setBelongsTo(temp1);
        content.setAssociatedTo(temp2);
        content.setGoData(temp3);
        content.setGoPrevious(temp4);
        content.setGoNext(temp5);

      }
      case EDGES -> {
        //core
        String contentUid = daoOperation.findUidFromAContentNode(content.getContentId());
        if (StringUtils.isNotBlank(contentUid)) {
          content.setUid(contentUid);
          daoOperation.commitNodeByJSON(content.createJson());
        }

        //relation (1)
        ContainerBlock container = content.getBelongsTo();
        if (container != null) {
          String containerUid = daoOperation.findUidFromAContainerNode(container.getContainerId());
          if (StringUtils.isNotBlank(containerUid)) {
            container.setUid(containerUid);
            daoOperation.commitNodeByJSON(container.createJson());
          }
        }

        //relation (2)
        Data data = content.getGoData();
        if (data != null) {
          String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
          if (StringUtils.isNotBlank(dataUid)) {
            data.setUid(dataUid);
            daoOperation.commitNodeByJSON(data.createJson());
          }
        }

      }
    }
  }

  @Override
  public void updateContentInBulk(ContentBlock contentBlock, NodePart element) {

  }

  @Override
  public void createContentInBulk(ContentBlock contentBlock, NodePart element) {

  }

  @Override
  public void upsertData(Data data, NodePart element) {
    switch (element) {
      case VERTEX -> {
        ContentBlock temp1 = data.getBelongsTo();
        temp1.setBelongsTo(null);
        String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isBlank(dataUid)) {
          dataUid = daoOperation.commitANode(data);
          if (StringUtils.isNotBlank(dataUid)) {
            data.setUid(dataUid);
          }
        } else {
          data.setUid(dataUid);
          daoOperation.commitANode(data);
        }
        data.setBelongsTo(temp1);
      }
      case EDGES -> {
        //core
        String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
        if (StringUtils.isNotBlank(dataUid)) {
          data.setUid(dataUid);
          daoOperation.commitNodeByJSON(data.createJson());
        }
        //relations
        ContentBlock contentBlock = data.getBelongsTo();
        if (contentBlock != null) {
          String contentUid = daoOperation.findUidFromAContentNode(contentBlock.getContentId());
          if (StringUtils.isNotBlank(contentUid)) {
            contentBlock.setUid(contentUid);
            daoOperation.commitNodeByJSON(contentBlock.createJson());
          }
        }
      }
    }
  }

  @Override
  public void updateDataInBulk(Data data, NodePart element) {

  }

  @Override
  public void createDataInBulk(Data data, NodePart element) {

  }

  @Override
  public void upsertFeature(Feature feature, NodePart element) {
    switch (element) {
      case VERTEX -> {
        Product temp1 = feature.getBelongsTo();
        feature.setBelongsTo(null);
        List<ContentBlock> temp2 = feature.getAssociatedTo();
        feature.setAssociatedTo(null);
        /*ContentBlock temp1 = data.getBelongsTo();
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
        data.setBelongsTo(temp1);*/
      }
      case EDGES -> {
        //core
/*        String dataUid = daoOperation.findUidFromADataNode(data.getDataId());
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
        }*/
      }
    }
  }

  @Override
  public void updateFeatureInBulk(Feature feature, NodePart element) {

  }

  @Override
  public void createFeatureInBulk(Feature feature, NodePart element) {

  }

  @Override
  public void upsertProduct(Product product, NodePart element) {
  }

  @Override
  public void updateProductInBulk(Product product, NodePart none) {

  }

  @Override
  public void createProductInBulk(Product product, NodePart none) {

  }

  @Override
  public List<Data> retrieveDataByState(DataState temporal) {
    return daoOperation.getDataByState(temporal);
  }

  @Override
  public Product retrieveProductWithFeatures(String productId) {
    return null;
  }

  @Override
  public List<String> insertObjects(LinkedList<Object> objects, NodePart none) {

  }
}
