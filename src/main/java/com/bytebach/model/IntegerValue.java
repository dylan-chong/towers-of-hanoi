package com.bytebach.model;

public class IntegerValue implements Value {
	private final int value;
	
	public IntegerValue(int val) {
		this.value = val;
	}
	
	public int value() {
		return value;
	}
	
	public boolean equals(Object o) {
		if(o instanceof IntegerValue) {
			IntegerValue v = (IntegerValue) o;
			return v.value == value;
		}
		return false;
	}
	
	public int hashCode() {
		return value;
	}
	
	public String toString() {
		return Integer.toString(value);
	}
}
