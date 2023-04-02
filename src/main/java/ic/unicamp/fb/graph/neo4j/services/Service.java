package ic.unicamp.fb.graph.neo4j.services;

public interface Service<T> {

    Iterable<T> findAll();

    T find(Long id);

    void delete(Long id);

    T createOrUpdate(T object);

    Class<T> getEntityType();
}
