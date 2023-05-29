#!/bin/bash
set -e

DB_DIR="/var/lib/neo4j/data/databases/neo4j"

# Check if the database directory is empty or does not exist
if [ ! -d "$DB_DIR" ]; then
  mkdir -p $DB_DIR
fi

if [ -d "$DB_DIR" ]; then
  rm -rf $DB_DIR/*
fi

if [ -z "$(ls -A $DB_DIR)" ]; then
  # Load the data from the dump file
  bin/neo4j-admin database load --from-path=/var/lib/neo4j/ neo4j --overwrite-destination=true
fi

# Start Neo4j
/bin/bash /startup/docker-entrypoint.sh neo4j
