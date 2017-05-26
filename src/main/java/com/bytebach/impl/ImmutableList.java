package com.bytebach.impl;

import com.bytebach.model.InvalidOperation;
import com.bytebach.model.Table;

import java.util.*;

/**
 * Created by Dylan on 26/05/17.
 *
 * Decorator list that throws {@link com.bytebach.model.InvalidOperation} when
 * someone tryies to modify it. The stupid requirement of the
 * {@link Table#fields()} method doesn't allow using Java's already existing
 * {@link java.util.Collections.UnmodifiableList} class
 */
public class ImmutableList<T> extends AbstractList<T> {
	private final List<T> list;

	public ImmutableList(List<T> list) {
		this(list, true);
	}

	public ImmutableList(List<T> list, boolean shouldCopy) {
		if (shouldCopy)
			list = new ArrayList<>(list);
		this.list = list;
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public void add(int index, T element) {
		throwInvalid();
	}

	@Override
	public boolean add(T t) {
		throwInvalid();
		return false;
	}

	@Override
	public T set(int index, T element) {
		throwInvalid();
		return null;
	}

	@Override
	public boolean remove(Object o) {
		throwInvalid();
		return false;
	}

	@Override
	public T remove(int index) {
		throwInvalid();
		return null;
	}

	private void throwInvalid() {
		throw new InvalidOperation("No modifying this list");
	}
}
