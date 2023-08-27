package ic.unicamp.fb.graph.neo4j.services;

import ic.unicamp.fb.graph.neo4j.factory.Neo4jSessionFactory;
import ic.unicamp.fb.graph.neo4j.schema.Index;
import ic.unicamp.fb.graph.neo4j.schema.Fragment;
import ic.unicamp.fb.graph.neo4j.schema.relations.FragmentToIndex;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IndexServiceImpl extends GenericService<Index> implements IndexService {

    @Override
    public Iterable<Index> findAll() {
        return session.loadAll(Index.class, 1);
    }

    @Override
    public Index getIndexByID(String indexId) {
        Filter filter = new Filter("indexId", ComparisonOperator.EQUALS, Integer.parseInt(indexId));
        Collection<Index> indexes = session.loadAll(Index.class, new Filters().add(filter));
        if (indexes.size() > 1) {
            System.out.println("Database corrupted. Two or more IDs for a Fragment are not allowed.");
        }
        Iterator<Index> iter = indexes.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }
        //print warning
        return null;
    }

    @Override
    public Index getIndexByFeature(String featureId) {
        String queryTemplate = "MATCH (f:Feature{featureId: '%s'})-[rel:HAS_A]->(i:Index) return i";
        String query = String.format(queryTemplate, featureId);
        System.out.println(query);
        Iterable<Map<String, Object>> queryResult = Neo4jSessionFactory.getInstance().getNeo4jSession()
                .query(query, Collections.EMPTY_MAP);
        List<Index> result = new LinkedList<>();
        queryResult.forEach(map -> {
            Index index = (Index) map.get("i");
            Index fullIndex = getIndexByID(String.valueOf(index.getIndexId()));
            result.add(fullIndex);
        });
        return result.get(0);
    }

    @Override
    public List<Index> getIndexByFragment(String fragmentId) {
        FragmentService fragmentService = new FragmentServiceImpl();
        Fragment fragment = fragmentService.getFragmentByID(fragmentId);
        List<Index> indexList = new LinkedList<>();
        List<FragmentToIndex> relations = fragment.getAssociatedTo();
        if (relations != null) {
            for (FragmentToIndex fragmentToIndex : relations) {
                Index index = fragmentToIndex.getEndIndex();
                if (index != null) {
                    Index fullIndex = getIndexByID(String.valueOf(index.getIndexId()));
                    indexList.add(fullIndex);
                }
            }
        }
        return indexList;
    }

    @Override
    public Class<Index> getEntityType() {
        return Index.class;
    }

}