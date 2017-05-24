package com.bytebach.model;

/**
 * A field object is used to define the permissible contents of data at
 * corresponding entries in rows of the enclosing table. A field must specify
 * the name, type and key status. If the type is REFERENCE, then it must
 * additionally specificy the table which the reference is allowed
 * 
 * @author djp
 * 
 */
public final class Field {
	private String title;
	private Type type;
	private String refTable;
	private boolean isKey;	
	
	public Field(String title, Type type, boolean isKey) {
		if(type == Type.REFERENCE) {
			throw new IllegalArgumentException(
					"Cannot create reference type with this constructor");
		}
		this.title = title;
		this.type = type;
		this.isKey = isKey;		
	}
	
	public Field(String title, String refTable, boolean isKey) {
		this.title = title;
		this.type = Type.REFERENCE;
		this.refTable = refTable;
		this.isKey = isKey;		
	}
	
	public enum Type { INTEGER, BOOLEAN, TEXT, TEXTAREA, REFERENCE };
	
	/**
	 * Get the title of this field.
	 * 
	 * @return
	 */
	public String title() {
		return title;
	}
	
	/**
	 * Get the type of this field
	 * 
	 * @return
	 */
	public Type type() {
		return type;
	}

	public String refTable() {
		if(type != Type.REFERENCE) {
			throw new InvalidOperation("Cannot get refTable from non-reference field");
		}
		return refTable;
	}
	
	/**
	 * Check whether this is a key field or not. The key fields of a given
	 * table row are guaranteed to be unique in that table.
	 * 
	 * @return
	 */
	public boolean isKey() {
		return isKey;
	}
}
