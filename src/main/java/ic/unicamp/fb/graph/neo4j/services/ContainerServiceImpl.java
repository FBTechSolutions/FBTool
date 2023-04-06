package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Container;
import ic.unicamp.fb.graph.neo4j.schema.enums.ContainerType;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Iterator;

public class ContainerServiceImpl extends GenericService<Container> implements ContainerService {

    @Override
    public Iterable<Container> findAll() {
        return session.loadAll(Container.class, 1);
    }

    /*  @Override
      public Container getContainerByID(String productId) {
        Filter filter = new Filter("containerId", ComparisonOperator.EQUALS, productId);
        Collection<Container> features = session.loadAll(Container.class, new Filters().add(filter));
        if (features.size() > 1) {
          System.out.println("Two IDs for Product is not good");
        }
        Iterator<Container> iter = features.iterator();
        if (iter.hasNext()) {
          return iter.next();
        }
        return null;
      }*/
    @Override
    public Container getContainerByID(String containerId) {
        Filter filter = new Filter("containerId", ComparisonOperator.EQUALS, containerId);
        Collection<Container> containers = session.loadAll(Container.class, new Filters().add(filter));
        if (containers.size() > 1) {
            System.out.println("Two IDs for Product is not good");
        }
        Iterator<Container> iter = containers.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public Container getContainerByType(ContainerType type) {
        Filter filter = new Filter("containerType", ComparisonOperator.EQUALS, type);
        Collection<Container> containers = session.loadAll(Container.class, new Filters().add(filter));
        if (containers.size() > 1) {
            System.out.println("Two MAIN is not good");
        }
        Iterator<Container> iter = containers.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public Class<Container> getEntityType() {
        return Container.class;
    }

}