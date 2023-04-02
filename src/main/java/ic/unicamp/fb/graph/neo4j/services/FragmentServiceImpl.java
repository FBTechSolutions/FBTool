package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FragmentServiceImpl extends GenericService<Fragment> implements FragmentService {

    @Override
    public Iterable<Fragment> findAll() {
        return session.loadAll(Fragment.class, 1);
    }

    @Override
    public Fragment getFragmentByID(String featureId) {
        Filter filter = new Filter("fragmentId", ComparisonOperator.EQUALS, featureId);
        Collection<Fragment> fragments = session.loadAll(Fragment.class, new Filters().add(filter));
        if (fragments.size() > 1) {
            System.out.println("Two IDs for Product is not good");
        }
        Iterator<Fragment> iter = fragments.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    @Override
    public List<Fragment> getFragmentsByFeatureId(String featureId) {
        String queryTemplate = "MATCH (p:Feature{featureId: '%s'})-[rel:ASSOCIATED_TO]->(f:Fragment) return f";
        String query = String.format(queryTemplate, featureId);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<Fragment> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Fragment fragment = (Fragment) map.get("f");
            result.add(fragment);
        });
        return result;
    }

    @Override
    public Class<Fragment> getEntityType() {
        return Fragment.class;
    }

}