package org.databaseliner.extraction.model;

import java.util.HashMap;
import java.util.Map;

public class Row {

	private final Map<Column, Object> fields;
	private boolean dirty = true;
	
	public Row() {
		fields = new HashMap<Column, Object>();
	}

	public void addFieldData(Column column, Object fieldValue) {
		fields.put(column, fieldValue);
	}
	
	public Object getColumnValue(Column column) {
		return fields.get(column);
	}
	
	public Object getColumnValue(String columnName) {
		Column columnWithName = getColumnWithName(columnName);
		if (columnWithName != null) {
			return getColumnValue(columnWithName);
		}
		return null;
	}
	
	private Column getColumnWithName(String columnName) {
		for (Column column : fields.keySet()) {
			if (column.getName().equals(columnName)){
				return column;
			}
		}
		return null;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	public void markClean() {
		dirty = false;
	}
	
	@Override
	public int hashCode() {
		boolean usePKHash = false;
		int hashOfPKColumns = 0;
		int hashOfAllColumns = 0;
		
		for (Column column : fields.keySet()) {
			Object field = fields.get(column);
			if (column.isPrimaryKey()) {
				hashOfPKColumns = hashOfPKColumns ^ hashField(field);
				usePKHash = true;
			} else if (!usePKHash) {
				hashOfAllColumns = hashOfAllColumns ^ hashField(field);
			}
		}
		return usePKHash ? hashOfPKColumns : hashOfAllColumns;
	}

	private int hashField(Object field) {
		return field != null ? field.hashCode() : "NULL".hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {return true;}
		if (obj == null) {return false;}
		if (getClass() != obj.getClass()) {return false;}
		
		final Row other = (Row) obj;
		boolean pkColumnsEqual = true;
		boolean allColumnsEqual = true;
		boolean usePKEqual = false;
		
		for (Column column : fields.keySet()) {
			Object columnValue = fields.get(column);
			Object otherColumnValue = other.getColumnValue(column);
			if (column.isPrimaryKey() && pkColumnsEqual) {
				pkColumnsEqual = pkColumnsEqual && columnValue.equals(otherColumnValue);
				usePKEqual = true;
			} else if (!usePKEqual && allColumnsEqual) {
				if (columnValue != null) {
					allColumnsEqual = allColumnsEqual && columnValue.equals(otherColumnValue);
				} else {
					allColumnsEqual = otherColumnValue == null;
				}
			}
		}
		return usePKEqual ? pkColumnsEqual : allColumnsEqual;
	}
	
}
