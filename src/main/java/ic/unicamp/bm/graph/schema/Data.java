package ic.unicamp.bm.graph.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import ic.unicamp.bm.graph.schema.enums.DataState;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
public class Data {

  @JsonProperty("dgraph.type")
  String type = "Data";

  @JsonProperty("uid")
  String uid;

  @JsonProperty("Data.dataId")
  String dataId;

  @JsonProperty("Data.sha")
  String sha;

  @JsonProperty("Data.currentState")
  DataState currentState;

  @JsonBackReference(value = "Content-Data")
  @JsonProperty("Data.belongsTo")
  ContentBlock belongsTo;

  public JsonObject createJson() {
    JsonObject result = new JsonObject();
    result.addProperty("dgraph.type", "Data");
    result.addProperty("uid", getUid());
    ContentBlock contentBlock = getBelongsTo();
    if (contentBlock != null && StringUtils.isNotBlank(contentBlock.getUid())) {
      JsonObject goContent = new JsonObject();
      goContent.addProperty("dgraph.type", "ContentBlock");
      goContent.addProperty("uid", contentBlock.getUid());
      result.add("Data.belongsTo", goContent);
    }
    return result;
  }
}