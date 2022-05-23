package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.schema.enums.ContainerType;
import ic.unicamp.bm.scanner.BlockState;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ContainerBlock {

  public static String TAG = "ContainerBlock";

  @Getter
  @Setter
  private String containerId;
  @Getter
  @Setter
  private ContainerType type;  //added

  @Getter
  @Setter
  private ContainerBlock goParent;
  @Getter
  @Setter
  private List<ContainerBlock> goChildren = new LinkedList<>();
  @Getter
  @Setter
  private ContentBlock goContent;

  public JsonObject createJson() {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", "ContainerBlock");
    json.addProperty(TAG + ".type", containerId);
    json.addProperty(TAG + ".type", String.valueOf(type));
    json.addProperty(TAG + ".goParent", String.valueOf(goParent.createJson()));

    JsonArray array = new JsonArray();
    for (ContainerBlock containerBlock : goChildren) {
      array.add(containerBlock.createJson());
    }
    json.addProperty(TAG + ".goChildren", String.valueOf(array));
    json.addProperty(TAG + ".goContent", String.valueOf(goContent.createJson()));
    return json;
  }
}
