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
import org.apache.tinkerpop.shaded.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@ToString
@Setter
@Getter
public class Feature {
  @JsonProperty("dgraph.type")
  String type = "Feature";
  String uid;

  @JsonProperty("Feature.featureId")
  String featureId;

  @JsonProperty("Feature.label")
  String label;

  @JsonBackReference
  @JsonProperty("Feature.belongsTo")
  Product belongsTo;

  @JsonManagedReference
  @JsonProperty("Feature.associatedTo")
  List<ContentBlock> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "Feature");
    result.addProperty("uid", getUid());

    if(getBelongsTo()!=null && !getBelongsTo().getUid().equals("")){
      JsonObject goContent = new JsonObject();
      goContent.addProperty("dgraph.type", "Product");
      goContent.addProperty("uid", getBelongsTo().getUid());
      result.addProperty( "Feature.belongsTo", goContent.toString());
    }
    if(!getAssociatedTo().isEmpty()){
      JsonArray jsonList = new JsonArray();
      for(ContentBlock content: getAssociatedTo()){
        if(!content.getUid().equals("")){
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "ContentBlock");
          aChild.addProperty("uid", content.getUid());
          jsonList.add(aChild);
        }
      }
      result.addProperty( "Product.associatedTo", jsonList.toString());
    }
    return result;
  }
}
