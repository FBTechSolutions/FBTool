package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.schema.Fragment;

import java.util.List;

public interface FragmentService extends Service<Fragment> {

    Fragment getFragmentByID(String fragmentId);

    List<Fragment> calcFragmentsByFeatureId(String featureId);

    List<Fragment> calculateFragmentsByFeatureList(List<String> featureList);
}
