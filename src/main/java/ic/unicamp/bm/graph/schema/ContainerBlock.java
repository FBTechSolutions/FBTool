package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.RecordOrientation;
import ic.unicamp.bm.graph.schema.enums.ContainerType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
public class ContainerBlock {
  @JsonProperty("dgraph.type")
  String type = "ContainerBlock";
  String uid;

  @JsonProperty("ContainerBlock.containerId")
  String containerId;

  @JsonProperty("ContainerBlock.containerType")
  ContainerType containerType;

  @JsonBackReference
  @JsonProperty("ContainerBlock.goParent")
  ContainerBlock goParent;

  @JsonManagedReference
  @JsonProperty("ContainerBlock.goChildren")
  List<ContainerBlock> goChildren = new LinkedList<>();

  @JsonManagedReference
  @JsonProperty("ContainerBlock.goContent")
  ContentBlock goContent;

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "ContainerBlock");
    result.addProperty("uid", getUid());

    if(getGoParent()!=null && !getGoParent().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContainerBlock");
      jsonParent.addProperty("uid", getGoParent().getUid());
      result.add( "ContainerBlock.goParent", jsonParent);
    }
    if(!getGoChildren().isEmpty()){
      JsonArray jsonChildren = new JsonArray();
      for(ContainerBlock children: getGoChildren()){
        if(!children.getUid().equals("")){
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "ContainerBlock");
          aChild.addProperty("uid", children.getUid());
          jsonChildren.add(aChild);
        }
      }
      result.add( "ContainerBlock.goChildren", jsonChildren);
    }
    if(getGoContent()!=null && !getGoContent().getUid().equals("")){
      JsonObject goContent = new JsonObject();
      goContent.addProperty("dgraph.type", "ContentBlock");
      goContent.addProperty("uid", getGoContent().getUid());
      result.add( "ContainerBlock.goContent", goContent);
    }
    return result;
  }
}
