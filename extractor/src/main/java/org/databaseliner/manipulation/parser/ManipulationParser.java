package org.databaseliner.manipulation.parser;

import org.databaseliner.manipulation.Manipulation;
import org.dom4j.Node;

public interface ManipulationParser {

	Manipulation parse(Node manipulationNode);

}
