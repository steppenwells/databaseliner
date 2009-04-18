package org.databaseliner.extraction.parser;

import org.databaseliner.extraction.Relationship;
import org.dom4j.Node;

public interface RelationshipParser {

	Relationship parse(Node relationshipNode);

}
