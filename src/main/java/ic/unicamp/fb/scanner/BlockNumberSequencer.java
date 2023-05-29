package ic.unicamp.fb.scanner;

import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;
import ic.unicamp.fb.graph.neo4j.services.FBConfigService;
import ic.unicamp.fb.graph.neo4j.services.FBConfigServiceImpl;
import org.apache.commons.lang3.StringUtils;

public class BlockNumberSequencer {

    private static final FBConfigService fbConfigService = new FBConfigServiceImpl();

    public static String getNextStringCode() {
        return transformToString(generateNextCode());
    }

    private static String transformToString(long generateNextCode) {
        return StringUtils.leftPad(String.valueOf(generateNextCode), 16, "0");
    }

    private static long generateNextCode() {
        FBToolConfiguration bmConfig = fbConfigService.getFBConfigByDefaultID();
        long code = bmConfig.getLastBlockId() + 1;
        if (code == 10000000000000000L) {
            code = 0;
        }
        bmConfig.setLastBlockId(code);
        fbConfigService.createOrUpdate(bmConfig);
        return code;
    }
}
