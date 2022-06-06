package ic.unicamp.bm.graph.schema;

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
public class Product {
  @JsonProperty("dgraph.type")
  String type = "Product";
  String uid;

  @JsonProperty("Product.productId")
  String productId;

  @JsonProperty("Product.label")
  String label;

  @JsonManagedReference
  @JsonProperty("Product.associatedTo")
  List<Feature> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "Product");
    result.addProperty("uid", getUid());

    if(!getAssociatedTo().isEmpty()){
      JsonArray jsonList = new JsonArray();
      for(Feature feature: getAssociatedTo()){
        if(!feature.getUid().equals("")){
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "Feature");
          aChild.addProperty("uid", feature.getUid());
          jsonList.add(aChild);
        }
      }
      result.addProperty( "Product.associatedTo", jsonList.toString());
    }
    return result;
  }
}
