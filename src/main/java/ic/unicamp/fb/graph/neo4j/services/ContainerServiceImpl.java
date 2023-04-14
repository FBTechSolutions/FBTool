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

    @Override
    public Container getContainerByID(String containerId) {
        Filter filter = new Filter("containerId", ComparisonOperator.EQUALS, containerId);
        Collection<Container> containers = session.loadAll(Container.class, new Filters().add(filter));
        if (containers.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Container are not allowed.");
        }
        Iterator<Container> iter = containers.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public Container getContainerByType(ContainerType containerType) {
        Filter filter = new Filter("containerType", ComparisonOperator.EQUALS, containerType);
        Collection<Container> containers = session.loadAll(Container.class, new Filters().add(filter));
        if (containers.size() > 1) {
            System.out.println("Database corrupted. Two or more MAIN Containers are not allowed.");
        }
        Iterator<Container> iter = containers.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public Class<Container> getEntityType() {
        return Container.class;
    }

}