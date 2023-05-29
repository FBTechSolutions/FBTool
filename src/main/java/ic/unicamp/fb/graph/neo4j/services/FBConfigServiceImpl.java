package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Iterator;

public class FBConfigServiceImpl extends GenericService<FBToolConfiguration> implements FBConfigService {

    public static String FB_CONFIG_ID = "FB_CONFIG_ID";

    @Override
    public Iterable<FBToolConfiguration> findAll() {
        return session.loadAll(FBToolConfiguration.class, 1);
    }

    @Override
    public FBToolConfiguration getFBConfigByDefaultID() {
        Filter filter = new Filter("configId", ComparisonOperator.EQUALS, FB_CONFIG_ID);
        Collection<FBToolConfiguration> features = session.loadAll(FBToolConfiguration.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Configuration are not allowed.");
        }
        Iterator<FBToolConfiguration> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public Class<FBToolConfiguration> getEntityType() {
        return FBToolConfiguration.class;
    }

}