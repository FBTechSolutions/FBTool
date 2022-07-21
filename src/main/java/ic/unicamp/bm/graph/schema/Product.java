package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;


@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
public class Product extends SchemaObject{

  @JsonProperty("dgraph.type")
  String schemaType = "Product";

  @JsonProperty("uid")
  String SchemaUid;

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
    result.addProperty("uid", getSchemaUid());

    if (!getAssociatedTo().isEmpty()) {
      JsonArray jsonList = new JsonArray();
      for (Feature feature : getAssociatedTo()) {
        if (!feature.getSchemaUid().equals("")) {
          JsonObject aChild = new JsonObject();
          aChild.addProperty("dgraph.type", "Feature");
          aChild.addProperty("uid", feature.getSchemaUid());
          jsonList.add(aChild);
        }
      }
      result.add("Product.associatedTo", jsonList);
    }
    return result;
  }
}
