package ic.unicamp.fb.graph.neo4j.utils;

import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.services.IndexService;

import static ic.unicamp.fb.cli.cmd.FBConfigure.FB_GENERIC_BIT_ORDER;

public class IndexUtil {
    public static Index retrieveOrCreateGenericIndexBean(IndexService indexService) {
        Index index = indexService.getIndexByID(FB_GENERIC_BIT_ORDER);
        if (index == null) {
            index = new Index();
            index.setIndexId(Integer.parseInt(FB_GENERIC_BIT_ORDER));
        }
        return index;
    }

    public static Index retrieveOrCreateAStandardIndexBean(IndexService indexService, int indexId) {
        Index index = indexService.getIndexByID(String.valueOf(indexId));
        if (index == null) {
            index = new Index();
            index.setIndexId(indexId);
        }
        return index;
    }
}
