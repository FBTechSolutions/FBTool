package ic.unicamp.fb.graph.neo4j.schema;

/**
 * Represents an abstract node in the Neo4j database.
 */
public abstract class AbstractNode {

    /**
     * Returns the ID of the node.
     *
     * @return the ID of the node.
     */
    public abstract Long getId();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "AbstractNode{" +
                "id=" + getId() +
                '}';
    }
}
