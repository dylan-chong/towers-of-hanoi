package com.bytebach.model;

public class BooleanValue implements Value {
	private final boolean value;
	
	public BooleanValue(boolean val) {
		this.value = val;
	}
	
	public boolean value() {
		return value;
	}
	
	public boolean equals(Object o) {
		if(o instanceof BooleanValue) {
			BooleanValue v = (BooleanValue) o;
			return v.value == value;
		}
		return false;
	}
	
	public int hashCode() {
		return value ? 1 : 0;
	}
	
	public String toString() {
		return Boolean.toString(value);
	}
}
