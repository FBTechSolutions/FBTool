package ic.unicamp.fb.graph.neo4j.services;

public interface Service<T> {

    Iterable<T> findAll();

    T find(Long id);

    void delete(Long id);

    void deleteAll();

    T createOrUpdate(T object);

    T createOrUpdate(T object, int depth);

    Class<T> getEntityType();
}
