package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Product {

  public static String NAME = "Product";

  @Getter
  @Setter
  private String productId;
  @Getter
  @Setter
  private String label;
  @Getter
  @Setter
  private List<Feature> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", NAME);
    json.addProperty(NAME + ".productId", productId);
    json.addProperty(NAME + ".label", label);
    JsonArray array = new JsonArray();
    for (Feature feature : associatedTo) {
      array.add(feature.createJson());
    }
    json.addProperty(NAME + ".associatedTo", String.valueOf(array));
    return json;
  }
}
