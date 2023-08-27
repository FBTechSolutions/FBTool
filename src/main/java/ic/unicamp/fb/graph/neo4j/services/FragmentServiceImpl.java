package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FragmentServiceImpl extends GenericService<Fragment> implements FragmentService {

    @Override
    public Iterable<Fragment> findAll() {
        return session.loadAll(Fragment.class, 1);
    }

    @Override
    public Fragment getFragmentByID(String fragmentId) {
        Filter filter = new Filter("fragmentId", ComparisonOperator.EQUALS, fragmentId);
        Collection<Fragment> fragments = session.loadAll(Fragment.class, new Filters().add(filter));
        if (fragments.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Fragment are not allowed.");
        }
        Iterator<Fragment> iter = fragments.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public List<Fragment> calcFragmentsByFeatureId(String featureId) {
        IndexService indexService = new IndexServiceImpl();
        Index index = indexService.getIndexByFeature(featureId);

        String queryTemplate = "MATCH (fr:Fragment)-[rel:ASSOCIATED_TO]->(i:Index{indexId: '%s'}) return fr";
        String query = String.format(queryTemplate, index.getIndexId());
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<Fragment> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Fragment fragment = (Fragment) map.get("fr");
            Fragment fullFragment = getFragmentByID(fragment.getFragmentId());
            result.add(fullFragment);
        });
        return result;
    }

    @Override
    public List<Fragment> calculateFragmentsByFeatureList(List<String> featureList) {
        IndexService indexService = new IndexServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();
        List<Fragment> fragmentList = new LinkedList<>();
        Set<String> indexList = new HashSet<>();
        for (String featureId : featureList) {
            Index index = indexService.getIndexByFeature(featureId);
            indexList.add(String.valueOf(index.getIndexId()));
        }

        for (Fragment fragment : fragmentService.findAll()) {
            Set<String> indexListFromFragment = new HashSet<>();
            List<FragmentToIndex> relations = fragment.getAssociatedTo();
            if (relations != null) {
                for (FragmentToIndex relation : relations) {
                    Index indexTemp = relation.getEndIndex();
                    indexListFromFragment.add(String.valueOf(indexTemp.getIndexId()));
                }
            } else {
                System.out.println("fragment " + fragment.getFragmentId() + " does not have a Indexes");
            }
            if (!indexListFromFragment.isEmpty()) {
                if (indexList.containsAll(indexListFromFragment)) {
                    fragmentList.add(fragment);
                }
            }
        }
        return fragmentList;
    }

    @Override
    public Fragment retrieveUniqueAndFragment(List<Integer> orderList) {
        Set<String> OrderStringList = orderList.stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        FragmentService fragmentService = new FragmentServiceImpl();
        for (Fragment fragment : fragmentService.findAll()) {
            Set<String> indexListFromFragment = new HashSet<>();
            List<FragmentToIndex> relations = fragment.getAssociatedTo();
            if (relations != null) {
                for (FragmentToIndex relation : relations) {
                    Index indexTemp = relation.getEndIndex();
                    indexListFromFragment.add(String.valueOf(indexTemp.getIndexId()));
                }
            } else {
                System.out.println("fragment " + fragment.getFragmentId() + " does not have a Indexes");
            }
            if (!indexListFromFragment.isEmpty()) {
                if (OrderStringList.equals(indexListFromFragment)) {
                    return fragment;
                }
            }
        }
        return null;
    }

    @Override
    public Class<Fragment> getEntityType() {
        return Fragment.class;
    }

}