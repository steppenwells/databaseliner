package org.databaseliner.manipulation;

import org.databaseliner.manipulation.parser.AsIsValueProviderParser;
import org.databaseliner.manipulation.parser.DecimalValueProviderParser;
import org.databaseliner.manipulation.parser.FieldProcessorValueProviderParser;
import org.databaseliner.manipulation.parser.IntegerValueProviderParser;
import org.databaseliner.manipulation.parser.StringValueProviderParser;
import org.databaseliner.manipulation.parser.ValueOfColumnValueProviderParser;
import org.databaseliner.manipulation.parser.ValueProviderParser;
import org.dom4j.Node;

public enum ValueProviderType {
	
	/**
	 * sets the value of the field to the integer given.
	 * 
	 * @see IntegerValueProviderParser
	 */
	integer(new IntegerValueProviderParser()),
	
	/**
	 * sets the value of the field to the decimal given.
	 * 
	 * @see DecimalValueProviderParser
	 */
	decimal(new DecimalValueProviderParser()),
	
	/**
	 * sets the value of the field to the string given. When output
	 * this will be output in single quotes (or however is defined in the
	 * dialect being used)
	 * 
	 * @see StringValueProviderParser
	 */
	string(new StringValueProviderParser()),
	
	/**
	 * sets the value of the field to be exactly the string given, this
	 * can be a sql statement which will be run in the context of the generated
	 * data insert script. An example of this would be getting the current date
	 * from the database.
	 * 
	 * @see AsIsValueProviderParser
	 */
	asIs(new AsIsValueProviderParser()),
	
	/**
	 * sets the value of the field to be the same as the value of another field
	 * in the same row. this would be used if you wanted to set every user's password
	 * to be the same as their username.
	 * 
	 * @see ValueOfColumnValueProviderParser
	 */
	valueOfColumn(new ValueOfColumnValueProviderParser()),
	
	/**
	 * Allows an arbitrary java class to generate a value for the field from a given
	 * input field. The value defines which class to use, it must be on the classpath and 
	 * implement the FieldProcessor interface. The FieldProcessor is given the value of
	 * one of the fields on the row (which it can ignore) and outputs an object which must
	 * be defined in the Dialect's known types (to output an arbitrary string use the 
	 * AsIsSQLString helper class).
	 * 
	 * <p>This ValueProvider is intended to be used when you need to apply application 
	 * specific logic to generate the field's value, examples are generating random numbers
	 * in place of credit card details or hashing passwords.</p>
	 * 
	 * @see FieldProcessorValueProviderParser
	 * @see FieldProcessor
	 * @see AsIsSQLString
	 */
	processor(new FieldProcessorValueProviderParser());
	
	private final ValueProviderParser valueProviderParser;
	
	private ValueProviderType(ValueProviderParser valueProviderParser) {
		this.valueProviderParser = valueProviderParser;
	}

	private ValueProvider parseFromNode(Node valueNode) {
		return valueProviderParser.parse(valueNode);
	}

	public static ValueProvider getValueProviderForNode(Node valueNode) {
		String type = valueNode.selectSingleNode("@type").getText().trim();
		ValueProviderType valueProviderType = ValueProviderType.valueOf(type);
		
		return valueProviderType.parseFromNode(valueNode); 
	}


}
