package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.Feature;
import java.util.Collection;
import java.util.Iterator;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class ContainerServiceImpl extends GenericService<Container> implements ContainerService {

  @Override
  public Iterable<Container> findAll() {
    return session.loadAll(Container.class, 1);
  }

  @Override
  public Container getContainerByID(String productId) {
    Filter filter = new Filter("featureId", ComparisonOperator.EQUALS, productId);
    Collection<Container> features = session.loadAll(Container.class, new Filters().add(filter));
    if(features.size()>1){
      System.out.println("Two IDs for Product is not good");
    }
    Iterator<Container> iter = features.iterator();
    if(iter.hasNext()){
      return iter.next();
    }
    return null;
  }

  @Override
  public Class<Container> getEntityType() {
    return Container.class;
  }

}