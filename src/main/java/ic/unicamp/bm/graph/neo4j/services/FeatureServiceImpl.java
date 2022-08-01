package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import ic.unicamp.bm.graph.neo4j.schema.Product;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class FeatureServiceImpl extends GenericService<Feature> implements FeatureService {

  @Override
  public Iterable<Feature> findAll() {
    return session.loadAll(Feature.class, 1);
  }

  @Override
  public Feature getFeatureByID(String featureId) {
    Filter filter = new Filter("featureId", ComparisonOperator.EQUALS, featureId);
    Collection<Feature> features = session.loadAll(Feature.class, new Filters().add(filter));
    if(features.size()>1){
      System.out.println("Two IDs for Product is not good");
    }
    Iterator<Feature> iter = features.iterator();
    if(iter.hasNext()){
      return iter.next();
    }
    return null;
  }

  @Override
  public Class<Feature> getEntityType() {
    return Feature.class;
  }

}