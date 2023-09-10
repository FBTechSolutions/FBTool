package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Feature;

import java.util.List;

public interface FeatureService extends Service<Feature> {

    Feature getFeatureByID(String featureId);

    List<Feature> getFeaturesByProductId(String productId);

    List<Feature> getAllFeatures();
}
