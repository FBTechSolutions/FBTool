package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;

import java.util.Collection;
import java.util.Iterator;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class BMConfigServiceImpl extends GenericService<FBToolConfiguration> implements BMConfigService {

    public static String BM_CONFIG_ID = "BM_CONFIG_ID";

    @Override
    public Iterable<FBToolConfiguration> findAll() {
        return session.loadAll(FBToolConfiguration.class, 1);
    }

    @Override
    public FBToolConfiguration getBMConfigByDefaultID() {
        Filter filter = new Filter("configId", ComparisonOperator.EQUALS, BM_CONFIG_ID);
        Collection<FBToolConfiguration> features = session.loadAll(FBToolConfiguration.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Two IDs for Product is not good");
        }
        Iterator<FBToolConfiguration> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public Class<FBToolConfiguration> getEntityType() {
        return FBToolConfiguration.class;
    }

}