package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import java.util.List;

public interface FeatureService extends Service<Feature> {

  Feature getFeatureByID(String productId);

  List<Feature> getFeaturesByProductId();

}
