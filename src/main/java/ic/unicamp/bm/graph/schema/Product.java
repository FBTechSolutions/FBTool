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
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
public class Product {

  @JsonProperty("dgraph.type")
  String type = "Product";

  @JsonProperty("uid")
  String uid;

  @JsonProperty("Product.productId")
  String productId;

  @JsonProperty("Product.label")
  String label;

  @JsonManagedReference(value = "Product-Feature")
  @JsonProperty("Product.associatedTo")
  List<Feature> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "Product");
    result.addProperty("uid", getUid());

    if (!getAssociatedTo().isEmpty()) {
      JsonArray jsonList = new JsonArray();
      for (Feature feature : getAssociatedTo()) {
        if (!feature.getUid().equals("")) {
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "Feature");
          aChild.addProperty("uid", feature.getUid());
          jsonList.add(aChild);
        }
      }
      result.add("Product.associatedTo", jsonList);
    }
    return result;
  }
}
