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
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
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

  @JsonBackReference(value = "Content-Content")
  @JsonProperty("ContentBlock.goPrevious")
  ContentBlock goPrevious;

  @JsonManagedReference(value = "Content-Content")
  @JsonProperty("ContentBlock.goNext")
  ContentBlock goNext;

  @JsonManagedReference(value = "Content-Data")
  @JsonProperty("ContentBlock.goData")
  Data goData;

  @JsonBackReference(value = "Container-Content")
  @JsonProperty("ContentBlock.belongsTo")
  ContainerBlock belongsTo;

  @JsonBackReference(value = "Feature-Content")
  @JsonProperty("ContentBlock.associatedTo")
  Feature associatedTo;

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "ContentBlock");
    result.addProperty("uid", getUid());

    if (getGoPrevious() != null && StringUtils.isNotBlank(getGoPrevious().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContentBlock");
      jsonParent.addProperty("uid", getGoPrevious().getUid());
      result.add("ContentBlock.goPrevious", jsonParent);
    }
    if (getGoNext() != null && StringUtils.isNotBlank(getGoNext().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContentBlock");
      jsonParent.addProperty("uid", getGoNext().getUid());
      result.add("ContentBlock.goNext", jsonParent);
    }
    if (getGoData() != null && StringUtils.isNotBlank(getGoData().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "Data");
      jsonParent.addProperty("uid", getGoData().getUid());
      result.add("ContentBlock.goData", jsonParent);
    }
    if (getBelongsTo() != null && StringUtils.isNotBlank(getBelongsTo().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "ContainerBlock");
      jsonParent.addProperty("uid", getBelongsTo().getUid());
      result.add("ContentBlock.belongsTo", jsonParent);
    }
    if (getAssociatedTo() != null && StringUtils.isNotBlank(getAssociatedTo().getUid())) {
      JsonObject jsonParent = new JsonObject();
      jsonParent.addProperty("dgraph.type", "Feature");
      jsonParent.addProperty("uid", getAssociatedTo().getUid());
      result.add("ContentBlock.associatedTo", jsonParent);
    }
    return result;
  }
}
