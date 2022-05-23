package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.schema.enums.DataState;
import lombok.Getter;
import lombok.Setter;

public class Data {

  public static String NAME = "Data";

  @Getter
  @Setter
  private String sha;
  @Getter
  @Setter
  private DataState currentState;

  public JsonObject createJson() {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", NAME);
    json.addProperty(NAME + ".sha", sha);
    json.addProperty(NAME + ".currentState", String.valueOf(currentState));
    return json;
  }
}
