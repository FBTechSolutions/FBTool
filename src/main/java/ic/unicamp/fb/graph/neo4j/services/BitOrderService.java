package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.schema.Feature;

public interface BitOrderService extends Service<BitOrder> {

    BitOrder getBitOrderByID(String bitOrderId);

    BitOrder getBitOrderByFeature(String featureId);
}
