package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonObject;
import ic.unicamp.bm.scanner.BlockState;
import lombok.Getter;
import lombok.Setter;

public class ContentBlock {

  public static String NAME = "ContentBlock";

  @Getter
  @Setter
  private String contentId;
  @Getter
  @Setter
  private BlockState currentState;
  @Getter
  @Setter
  private ContentBlock goPrevious;
  @Getter
  @Setter
  private ContentBlock goNext;
  @Getter
  @Setter
  private Data goData;

  public JsonObject createJson() {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", NAME);
    json.addProperty(NAME + ".contentId", contentId);
    json.addProperty(NAME + ".currentState", String.valueOf(currentState));
    json.addProperty(NAME + ".goPrevious", String.valueOf(goPrevious.createJson()));
    json.addProperty(NAME + ".goNext", String.valueOf(goNext.createJson()));
    json.addProperty(NAME + ".goData", String.valueOf(goData.createJson()));
    return json;
  }
}
