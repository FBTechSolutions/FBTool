package ic.unicamp.bm.graph.schema;

import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.apache.tinkerpop.shaded.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Setter
@Getter
public class Raw<T> {

  List<T> raw;
}
