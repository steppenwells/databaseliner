package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.ValueProvider;
import org.dom4j.Node;

public interface ValueProviderParser {

	ValueProvider parse(Node valueNode);

}
