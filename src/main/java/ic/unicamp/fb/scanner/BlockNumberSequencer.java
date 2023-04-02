package ic.unicamp.fb.scanner;

import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;
import ic.unicamp.fb.graph.neo4j.services.BMConfigService;
import ic.unicamp.fb.graph.neo4j.services.BMConfigServiceImpl;
import org.apache.commons.lang3.StringUtils;

public class BlockNumberSequencer {

    private static final BMConfigService bmConfigService = new BMConfigServiceImpl();

    public static String getNextStringCode() {
        return transformToString(generateNextCode());
    }

    private static String transformToString(long generateNextCode) {
        return StringUtils.leftPad(String.valueOf(generateNextCode), 16, "0");
    }

    private static long generateNextCode() {
        FBToolConfiguration bmConfig = bmConfigService.getBMConfigByDefaultID();
        long code = bmConfig.getLastBlockId() + 1;
        if (code == 10000000000000000L) {
            code = 0;
        }
        bmConfig.setLastBlockId(code);
        bmConfigService.createOrUpdate(bmConfig);
        return code;
    }
}
