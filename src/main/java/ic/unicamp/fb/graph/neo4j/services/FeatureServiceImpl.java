package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.Feature;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FeatureServiceImpl extends GenericService<Feature> implements FeatureService {

    @Override
    public Iterable<Feature> findAll() {
        return session.loadAll(Feature.class, 1);
    }

    @Override
    public Feature getFeatureByID(String featureId) {
        Filter filter = new Filter("featureId", ComparisonOperator.EQUALS, featureId);
        Collection<Feature> features = session.loadAll(Feature.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Feature are not allowed.");
        }
        Iterator<Feature> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public List<Feature> getFeaturesByProductId(String productId) {
        String queryTemplate = "MATCH (p:Product{productId: '%s'})-[rel:ASSOCIATED_TO]->(f:Feature) return f";
        String query = String.format(queryTemplate, productId);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<Feature> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Feature feature = (Feature) map.get("f");
            result.add(feature);
        });
        return result;
    }

    @Override
    public Class<Feature> getEntityType() {
        return Feature.class;
    }

}