package ic.unicamp.bm.graph.schema;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.RecordOrientation;
import ic.unicamp.bm.graph.schema.enums.ContainerType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class ContainerBlock {

  public static String TAG = "ContainerBlock";

  @Getter
  @Setter
  private String containerId;
  @Getter
  @Setter
  private ContainerType containerType;  //added

  @Getter
  @Setter
  private ContainerBlock goParent;
  @Getter
  @Setter
  private List<ContainerBlock> goChildren = new LinkedList<>();
  @Getter
  @Setter
  private ContentBlock goContent;

  //add map here to check if visitied or not, o the visitor issue. or put in a stack and remove when processing.
/*  public JsonObject createJson(Map<String, Boolean> map, RecordOrientation orientation) {
    JsonObject json = new JsonObject();
    json.addProperty("dgraph.type", "ContainerBlock");
    if(containerId!=null){
      json.addProperty(TAG + ".containerId", containerId);
    }
    if(containerType !=null){
      json.addProperty(TAG + ".containerType", containerType.toString());
    }
    if(map >0){
      switch (orientation){
        case UP -> {
          if(goParent!=null){
            json.addProperty(TAG + ".goParent", goParent.createJson(0, orientation).toString());
          }
        }
        case DOWN -> {
          JsonArray array = new JsonArray();
          for (ContainerBlock containerBlock : goChildren) {
            array.add(containerBlock.createJson(0, orientation));
          }
          if(goChildren!=null){
            json.add(TAG + ".goChildren", array);
          }
        }
      }
    }
    if(goContent!=null){
      json.addProperty(TAG + ".goContent", goContent.createJson().toString());
    }
    //System.out.println(json.toString());
    return json;
  }*/
}
