package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Index;

import java.util.List;

public interface IndexService extends Service<Index> {

    Index getIndexByID(String indexId);

    Index getIndexByFeature(String featureId);

    List<Index> getIndexByFragment(String fragmentId);
}
