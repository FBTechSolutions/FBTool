package ic.unicamp.bm.graph.neo4j.services;

import com.google.common.collect.Lists;
import ic.unicamp.bm.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.bm.graph.neo4j.schema.BMConfig;
import ic.unicamp.bm.graph.neo4j.schema.Block;
import ic.unicamp.bm.graph.neo4j.schema.Container;
import ic.unicamp.bm.graph.neo4j.schema.enums.DataState;
import ic.unicamp.bm.graph.neo4j.schema.relations.ContainerToBlock;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

public class BMConfigServiceImpl extends GenericService<BMConfig> implements BMConfigService {

    public static String BM_CONFIG_ID = "BM_CONFIG_ID";

    @Override
    public Iterable<BMConfig> findAll() {
        return session.loadAll(BMConfig.class, 1);
    }

    @Override
    public BMConfig getBMConfigByDefaultID() {
        Filter filter = new Filter("configId", ComparisonOperator.EQUALS, BM_CONFIG_ID);
        Collection<BMConfig> features = session.loadAll(BMConfig.class, new Filters().add(filter));
        if (features.size() > 1) {
            System.out.println("Two IDs for Product is not good");
        }
        Iterator<BMConfig> iter = features.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public Class<BMConfig> getEntityType() {
        return BMConfig.class;
    }

}