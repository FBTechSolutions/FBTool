package ic.unicamp.bm.graph.neo4j.services;

import ic.unicamp.bm.graph.neo4j.schema.BMConfig;

public interface BMConfigService extends Service<BMConfig> {

    BMConfig getBMConfigByDefaultID();

}
