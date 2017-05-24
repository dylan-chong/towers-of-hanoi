package com.bytebach.model;

public class StringValue implements Value {
	private final String value;
	
	public StringValue(String val) {
		this.value = val;
	}
	
	public String value() {
		return value;
	}
	
	public boolean equals(Object o) {
		if(o instanceof StringValue) {
			StringValue v = (StringValue) o;
			return v.value.equals(value);
		}
		return false;
	}
	
	public int hashCode() {
		return value.hashCode();
	}
	
	public String toString() {
		return value;
	}
}
