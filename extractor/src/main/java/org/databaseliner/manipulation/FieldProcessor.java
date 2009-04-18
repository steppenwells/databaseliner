package org.databaseliner.manipulation;

public interface FieldProcessor {

	/**
	 * 
	 * Calculates the value to set a field to given the input value. The input value is configured in the ValueProvider
	 * that invokes the processor. If you are implementing this this will be the value configured your xml.
	 * 
	 * @param inputValue
	 * @return any Object you like, so long as it is understood by the Dialect in use.
	 */
	public Object getFieldValue(Object inputValue);
}
