package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.unicamp.bm.scanner.BlockState;
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
public class ContentBlock {
  @JsonProperty("dgraph.type")
  String type = "ContentBlock";
  String uid;

  @JsonProperty("ContentBlock.contentId")
  String contentId;

  @JsonProperty("ContentBlock.currentState")
  BlockState currentState;

  @JsonBackReference
  @JsonProperty("ContentBlock.goPrevious")
  ContentBlock goPrevious;

  @JsonManagedReference
  @JsonProperty("ContentBlock.goNext")
  ContentBlock goNext;

  @JsonManagedReference
  @JsonProperty("ContentBlock.goData")
  Data goData;

  @JsonBackReference
  @JsonProperty("ContentBlock.belongsTo")
  ContainerBlock belongsTo;

  @JsonBackReference
  @JsonProperty("ContentBlock.associatedTo")
  Feature associatedTo;

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "ContentBlock");
    result.addProperty("uid", getUid());

    if(getGoPrevious()!=null && !getGoPrevious().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContentBlock");
      jsonParent.addProperty("uid", getGoPrevious().getUid());
      result.addProperty( "ContentBlock.goPrevious", jsonParent.toString());
    }
    if(getGoNext()!=null && !getGoNext().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContentBlock");
      jsonParent.addProperty("uid", getGoNext().getUid());
      result.addProperty( "ContentBlock.goNext", jsonParent.toString());
    }
    if(getGoData()!=null && !getGoData().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "Data");
      jsonParent.addProperty("uid", getGoData().getUid());
      result.addProperty( "ContentBlock.goData", jsonParent.toString());
    }
    if(getBelongsTo()!=null && !getBelongsTo().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContainerBlock");
      jsonParent.addProperty("uid", getBelongsTo().getUid());
      result.addProperty( "ContentBlock.belongsTo", jsonParent.toString());
    }
    if(getAssociatedTo()!=null && !getAssociatedTo().getUid().equals("")){
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "Feature");
      jsonParent.addProperty("uid", getAssociatedTo().getUid());
      result.addProperty( "ContentBlock.associatedTo", jsonParent.toString());
    }
    return result;
  }
}
