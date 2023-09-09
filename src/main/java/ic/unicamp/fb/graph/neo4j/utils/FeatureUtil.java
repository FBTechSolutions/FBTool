package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Feature;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.ProductToFeature;
import ic.unicamp.fb.graph.neo4j.services.FeatureService;
import ic.unicamp.fb.graph.neo4j.services.FragmentService;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_FEATURE;

public class FeatureUtil {
    public static Feature retrieveOrCreateGenericFeatureBean(FeatureService featureService) {
        Feature fullFeature = featureService.getFeatureByID(FB_GENERIC_FEATURE);
        if (fullFeature == null) {
            fullFeature = new Feature();
            fullFeature.setFeatureId(FB_GENERIC_FEATURE);
            fullFeature.setFeatureLabel(FB_GENERIC_FEATURE);
        }
        return fullFeature;
    }

    public static Feature retrieveOrCreateAStandardFeatureBean(FeatureService featureService, String featureId, String featureLabel) {
        Feature fullFeature = featureService.getFeatureByID(featureId);
        if (fullFeature == null) {
            fullFeature = new Feature();
            fullFeature.setFeatureId(featureId);
            fullFeature.setFeatureLabel(featureLabel);
        }
        return fullFeature;
    }

    public static List<String> retrieveFeatureIdsByProductToFeatureRelation(List<ProductToFeature> featureList) {
        List<String> featureIds = new LinkedList<>();
        for (ProductToFeature rel : featureList) {
            Feature tempFeature = rel.getEndFeature();
            if (tempFeature != null) {
                featureIds.add(tempFeature.getFeatureId());
            }
        }
        return featureIds;
    }

    public static List<String> retrieveFeatureIds(List<Feature> featureList) {
        List<String> featureIds = new LinkedList<>();
        for (Feature feature : featureList) {
            featureIds.add(feature.getFeatureId());
        }
        return featureIds;
    }

    public static boolean exitsFeatureBean(FeatureService featureService, String featureId) {
        Feature fullFeature = featureService.getFeatureByID(featureId);
        return fullFeature != null;
    }

    public static List<Feature> orderFeatures(Iterable<Feature> featureIterable){
        List<Feature> orderedFeatures = new LinkedList<>();
        for (Feature obj : featureIterable) {
            orderedFeatures.add(obj);
        }
        Collections.sort(orderedFeatures);
        return orderedFeatures;
    }
}
