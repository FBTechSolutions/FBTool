package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToBitOrder;
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
        BitOrderService bitOrderService = new BitOrderServiceImpl();
        BitOrder bitOrder = bitOrderService.getBitOrderByFeature(featureId);

        String queryTemplate = "MATCH (fr:Fragment)-[rel:ASSOCIATED_TO]->(b:BitOrder{bitOrderId: '%s'}) return fr";
        String query = String.format(queryTemplate, bitOrder.getBitOrderId());
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
        BitOrderService bitOrderService = new BitOrderServiceImpl();
        FragmentService fragmentService = new FragmentServiceImpl();
        List<Fragment> fragmentList = new LinkedList<>();
        Set<String> bitOrderList = new HashSet<>();
        for (String featureId : featureList) {
            BitOrder bitOrder = bitOrderService.getBitOrderByFeature(featureId);
            bitOrderList.add(String.valueOf(bitOrder.getBitOrderId()));
        }

        for (Fragment fragment : fragmentService.findAll()) {
            Set<String> bitOrderListFromFragment = new HashSet<>();
            List<FragmentToBitOrder> relations = fragment.getAssociatedTo();
            if (relations != null) {
                for (FragmentToBitOrder relation : relations) {
                    BitOrder bitOrderTemp = relation.getEndBitOrder();
                    bitOrderListFromFragment.add(String.valueOf(bitOrderTemp.getBitOrderId()));
                }
            } else {
                System.out.println("fragment " + fragment.getFragmentId() + " does not have a bitOrders");
            }
            if (!bitOrderListFromFragment.isEmpty()) {
                if (bitOrderList.containsAll(bitOrderListFromFragment)) {
                    fragmentList.add(fragment);
                }
            }
        }
        return fragmentList;
    }

    @Override
    public Class<Fragment> getEntityType() {
        return Fragment.class;
    }

}