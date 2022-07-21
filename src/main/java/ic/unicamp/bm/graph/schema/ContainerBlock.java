package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.schema.enums.ContainerType;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;



@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
public class ContainerBlock {

  @JsonProperty("dgraph.type")
  String type = "ContainerBlock";

  @JsonProperty("uid")
  String uid;

  @JsonProperty("ContainerBlock.containerId")
  String containerId;

  @JsonProperty("ContainerBlock.containerType")
  ContainerType containerType;

  @JsonBackReference(value = "Container-Container")
  @JsonProperty("ContainerBlock.goParent")
  ContainerBlock goParent;

  @JsonManagedReference(value = "Container-Container")
  @JsonProperty("ContainerBlock.goChildren")
  List<ContainerBlock> goChildren = new LinkedList<>();

  @JsonManagedReference(value = "Container-Content")
  @JsonProperty("ContainerBlock.goContent")
  ContentBlock goContent;

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "ContainerBlock");
    result.addProperty("uid", getUid());

    if (getGoParent() != null && StringUtils.isNotBlank(getGoParent().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContainerBlock");
      jsonParent.addProperty("uid", getGoParent().getUid());
      result.add("ContainerBlock.goParent", jsonParent);
    }
    if (!getGoChildren().isEmpty()) {
      JsonArray jsonChildren = new JsonArray();
      for (ContainerBlock children : getGoChildren()) {
        String uid = children.getUid();
        if (StringUtils.isNotBlank(uid)) {
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "ContainerBlock");
          aChild.addProperty("uid", children.getUid());
          jsonChildren.add(aChild);
        }
      }
      result.add("ContainerBlock.goChildren", jsonChildren);
    }
    if (getGoContent() != null && StringUtils.isNotBlank(getGoContent().getUid())) {
      JsonObject goContent = new JsonObject();
      goContent.addProperty("dgraph.type", "ContentBlock");
      goContent.addProperty("uid", getGoContent().getUid());
      result.add("ContainerBlock.goContent", goContent);
    }
    return result;
  }
}
