package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BitOrderServiceImpl extends GenericService<BitOrder> implements BitOrderService {

    @Override
    public Iterable<BitOrder> findAll() {
        return session.loadAll(BitOrder.class, 1);
    }

    @Override
    public BitOrder getBitOrderByID(String bitOrderId) {
        Filter filter = new Filter("bitOrderId", ComparisonOperator.EQUALS, bitOrderId);
        Collection<BitOrder> bitOrders = session.loadAll(BitOrder.class, new Filters().add(filter));
        if (bitOrders.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Fragment are not allowed.");
        }
        Iterator<BitOrder> iter = bitOrders.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public BitOrder getBitOrderByFeature(String featureId) {
        String queryTemplate = "MATCH (f:Feature{featureId: '%s'})-[rel:HAS_A]->(b:BitOrder) return b";
        String query = String.format(queryTemplate, featureId);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<BitOrder> result = new LinkedList<>();
        queryResult.forEach(map -> {
            BitOrder bitOrder = (BitOrder) map.get("b");
            BitOrder fullBitOrder = getBitOrderByID(String.valueOf(bitOrder.getBitOrderId()));
            result.add(fullBitOrder);
        });
        return result.get(0);
    }

    @Override
    public Class<BitOrder> getEntityType() {
        return BitOrder.class;
    }

}