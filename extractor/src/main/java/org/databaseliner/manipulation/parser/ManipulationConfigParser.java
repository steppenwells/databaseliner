package org.databaseliner.manipulation.parser;

import java.util.List;

import org.databaseliner.manipulation.ManipulationService;
import org.databaseliner.manipulation.ManipulationType;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * 
 * <p>Configures the manipulations to be applied to the data that has been extracted from the database before 
 * it is output. Manipulations can be applied at the field, column or table level. When configuring manipulations
 * always refer to the original table name (and schema if appropriate), they are applied first to fields, then columns
 * and finally tables so that the original name can always be used to find data in the model. 
 * <p>
 * <code>
 * &lt;databaseliner&gt;<br/>
 *	<br/>
 *	...<br/>
 *	<br/>
 *	&lt;manipulations&gt;<br/>
 *		&lt;manipulation type="manipulation type" /&gt;<br/>
 *		...
 *	&lt;/manipulations&gt;<br/>
 *	<br/>
 * 	...<br/>
 * 	<br/>
 * &lt;/databaseliner&gt;
 * </code>
 * </p>
 * <p>The valid manipulation types are described in the ManipulationType enumeration</p>
 * @see ManipulationType
 **/
public class ManipulationConfigParser {

	@SuppressWarnings("unchecked")
	public static ManipulationService parse(Document configDocument) {
		ManipulationService manipulationService = new ManipulationService();
		
		List<Node> selectNodes = configDocument.selectNodes("//databaseliner/manipulations/manipulation");
		for (Node manipulationNode : selectNodes) {
			manipulationService.add(ManipulationType.getManipulationForNode(manipulationNode));
		}
		
		
		return manipulationService;
	}

}
