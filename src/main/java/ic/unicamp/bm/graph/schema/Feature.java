package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
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
public class Feature {

  @JsonProperty("dgraph.type")
  String type = "Feature";

  @JsonProperty("uid")
  String uid;

  @JsonProperty("Feature.featureId")
  String featureId;

  @JsonProperty("Feature.label")
  String label;

  @JsonBackReference(value = "Product-Feature")
  @JsonProperty("Feature.belongsTo")
  Product belongsTo;

  @JsonManagedReference(value = "Feature-Content")
  @JsonProperty("Feature.associatedTo")
  List<ContentBlock> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "Feature");
    result.addProperty("uid", getUid());

    if (getBelongsTo() != null && StringUtils.isNotBlank(getBelongsTo().getUid())) {
      JsonObject goContent = new JsonObject();
      goContent.addProperty("dgraph.type", "Product");
      goContent.addProperty("uid", getBelongsTo().getUid());
      result.add("Feature.belongsTo", goContent);
    }
    if (!getAssociatedTo().isEmpty()) {
      JsonArray jsonList = new JsonArray();
      for (ContentBlock content : getAssociatedTo()) {
        if (!content.getUid().equals("")) {
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "ContentBlock");
          aChild.addProperty("uid", content.getUid());
          jsonList.add(aChild);
        }
      }
      result.add("Product.associatedTo", jsonList);
    }
    return result;
  }
}
