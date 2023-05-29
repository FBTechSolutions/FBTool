package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.BitOrder;
import ic.unicamp.fb.graph.neo4j.services.BitOrderService;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_BIT_ORDER;

public class BitOrderUtil {
    public static BitOrder retrieveOrCreateGenericBitOrderBean(BitOrderService bitOrderService) {
        BitOrder bitOrder = bitOrderService.getBitOrderByID(FB_GENERIC_BIT_ORDER);
        if (bitOrder == null) {
            bitOrder = new BitOrder();
            bitOrder.setBitOrderId(Integer.parseInt(FB_GENERIC_BIT_ORDER));
        }
        return bitOrder;
    }

    public static BitOrder retrieveOrCreateAStandardBitOrderBean(BitOrderService bitOrderService, int bitOrderId) {
        BitOrder bitOrder = bitOrderService.getBitOrderByID(String.valueOf(bitOrderId));
        if (bitOrder == null) {
            bitOrder = new BitOrder();
            bitOrder.setBitOrderId(bitOrderId);
        }
        return bitOrder;
    }
}
