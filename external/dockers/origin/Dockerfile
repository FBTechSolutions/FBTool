# Use the official Neo4j image as a base
FROM neo4j:latest

ENV NEO4J_AUTH=neo4j/password

# copy the dump
COPY ./neo4j.dump /var/lib/neo4j/

# Copy the script
COPY ./entrypoint.sh /var/lib/neo4j/

# Run script
ENTRYPOINT ["/var/lib/neo4j/entrypoint.sh"]
