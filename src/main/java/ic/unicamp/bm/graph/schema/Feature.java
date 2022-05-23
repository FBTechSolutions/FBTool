package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class Feature {

  public static String NAME = "Feature";

  @Getter
  @Setter
  private String featureId;
  @Getter
  @Setter
  private String label;
  @Getter
  @Setter
  private Product belongsTo;
  @Getter
  @Setter
  private List<ContentBlock> associatedTo = new LinkedList<>();

  public JsonObject createJson() {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", "Feature");
    json.addProperty(NAME + ".featureId", featureId);
    json.addProperty(NAME + ".label", label);
    json.addProperty(NAME + ".belongsTo", String.valueOf(belongsTo.createJson()));
    JsonArray array = new JsonArray();
    for (ContentBlock contentBlock : associatedTo) {
      array.add(contentBlock.createJson());
    }
    json.addProperty(NAME + ".associatedTo", String.valueOf(array));
    return json;
  }
}
