package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.FBToolConfiguration;

public interface BMConfigService extends Service<FBToolConfiguration> {

    FBToolConfiguration getBMConfigByDefaultID();

}
