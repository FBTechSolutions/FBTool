package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;

import java.util.List;

public interface BitOrderService extends Service<BitOrder> {

    BitOrder getBitOrderByID(String bitOrderId);

    BitOrder getBitOrderByFeature(String featureId);

    List<BitOrder> getBitOrderByFragment(String fragmentId);
}
