package org.databaseliner.extraction;

import org.dom4j.Node;

public enum ConverterType {

	noAction(new NoActionConverter()),
	integer(new AsIntegerConverter()),
	string(new ToStringConverter());

	private final Converter converter;
	
	private ConverterType(Converter converter) {
		this.converter = converter;
	}

	public Converter getConverter() {
		return converter;
	}

	public static Converter getConverterForNode(Node convertToAttribute) {
		String type = convertToAttribute.getText().trim();
		ConverterType converterType = ConverterType.valueOf(type);
		
		return converterType.getConverter(); 
	}
	
	// Oh I wish I had delegates...
	public static class AsIntegerConverter implements Converter {
		
		@Override
		public Object convert(Object objectToConvert) {
			if (objectToConvert instanceof Integer || 
					objectToConvert instanceof Short ||
					objectToConvert instanceof Long) {
				return objectToConvert;
			} else {
				return new Integer(objectToConvert.toString());
			}
		}
		
	}
	
	public static class ToStringConverter implements Converter {

		@Override
		public Object convert(Object objectToConvert) {
			return objectToConvert.toString();
		}

	}
	
	public static class NoActionConverter implements Converter {

		@Override
		public Object convert(Object objectToConvert) {
			return objectToConvert;
		}

	}
	
}
