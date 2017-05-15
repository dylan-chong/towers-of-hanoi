package swen221.lab7;
import java.util.*;

public class BiHashMap<KeyT, ValueT> implements Map<KeyT, ValueT> {
	private HashMap<KeyT, ValueT> keyToValue = new HashMap<>();
	private HashMap<ValueT, List<KeyT>> valueToKeys = new HashMap<>();


	@Override
	public boolean containsKey(Object key) {		
		return keyToValue.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) { 
		return keyToValue.containsValue(value);
	}
	
	/**
	 * Associates the specified value with the specified key in this
	 * map. If the map previously contained a mapping for this key,
	 * the old value is replaced.
     * Returns the previous value associated with key, or null if there was no mapping for key.
     *(A null return can also indicate that the map previously associated null with key).
	 */
	@Override
	public ValueT put(KeyT key, ValueT value) {
		if (keyToValue.containsKey(key)) {
			remove(key);
		}

		// first, update the value associated with this key
		ValueT removed = keyToValue.put(key, value);

		// now, update the set of keys associated with this value
		List<KeyT> keys = valueToKeys.computeIfAbsent(value, k -> new ArrayList<>());
		keys.add(key);
		// finally, return the original mapping in order to ad here to Map
		// interface.
		return removed;
	}

	/**
	 * Copies all of the mappings from the specified map to this map (optional
	 * operation). The effect of this call is equivalent to that of calling
	 * put(k, v) on this map once for each mapping from key k to value v in the
	 * specified map. The behavior of this operation is unspecified if the
	 * specified map is modified while the operation is in progress.
	 */
	@Override	
	public void putAll(Map<? extends KeyT, ? extends ValueT> m) {
		m.forEach(this::put);
	}
	
	/**
	 * Removes the mapping for this key from this map if present. Returns the
	 * previous value associated with key, or null if there was no mapping for
	 * key. (A null return can also indicate that the map previously associated
	 * null with key).
	 */
	@Override
	public ValueT remove(Object key) {
		// first, update the value associated with this key
		ValueT value = keyToValue.remove(key);

		if (value != null) {
			// now, update the set of keys associated with this value
			List<KeyT> keys = valueToKeys.get(value);
			// Note, keys should not be null!
			//noinspection SuspiciousMethodCalls
			keys.remove(key);
		}
		// finally, return the original mapping in order to ad here to Map
		// interface.    	
		return value;
	}

	/**
	 * Returns the value to which the specified key is mapped in this identity
	 * hash map, or null if the map contains no mapping for this key. A return
	 * value of null does not necessarily indicate that the map contains no
	 * mapping for the key; it is also possible that the map explicitly maps the
	 * key to null. The containsKey method may be used to distinguish these two
	 * cases.
	 * 
	 * @param key
	 * @return
	 */
	@Override	
	public ValueT get(Object key) {
		return keyToValue.get(key);
	}

	/**
	 * Get the set of keys associated with a particular value
	 */	
	public List<KeyT> getKeys(ValueT value) {
		return valueToKeys.get(value);
	}

	@Override
	public void clear() {
		keyToValue.clear();
		valueToKeys.clear();
	}	
	
	/**
	 * Returns a collection view of the mappings contained in this map. Each
	 * element in the returned collection is a Map.Entry. The collection is
	 * backed by the map, so changes to the map are reflected in the collection,
	 * and vice-versa. The collection supports element removal, which removes
	 * the corresponding mapping from the map, via the Iterator.remove,
	 * Collection.remove, removeAll, retainAll, and clear operations. It does
	 * not support the add or addAll operations.
	 * 
	 * @return
	 */
	@Override
	public Set<Map.Entry<KeyT, ValueT>> entrySet() {
		return keyToValue.entrySet();
	}

    /**
	 * Returns a set view of the keys contained in this map. The set is backed
	 * by the map, so changes to the map are reflected in the set, and
	 * vice-versa. If the map is modified while an iteration over the set is in
	 * progress (except through the iterator's own remove operation), the
	 * results of the iteration are undefined. The set supports element removal,
	 * which removes the corresponding mapping from the map, via the
	 * Iterator.remove, Set.remove, removeAll retainAll, and clear operations.
	 * It does not support the add or addAll operations.
	 */
	@Override
	public Set<KeyT> keySet() {
		return keyToValue.keySet();
	}

	/**
	 * Returns a collection view of the values contained in this map. The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa. If the map is modified while an iteration
	 * over the collection is in progress (except through the iterator's own
	 * remove operation), the results of the iteration are undefined. The
	 * collection supports element removal, which removes the corresponding
	 * mapping from the map, via the Iterator.remove, Collection.remove,
	 * removeAll, retainAll and clear operations. It does not support the add or
	 * addAll operations.
	 * 
	 * @return
	 */
	@Override
	public Collection<ValueT> values() {
		return keyToValue.values();
	}

	/**
	 * Returns true if this map contains no key-value mappings.
	 * 
	 * @return
	 */
	@Override
	public boolean isEmpty() {
		return keyToValue.isEmpty();
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * 
	 * @return
	 */
	@Override
	public int size() {
		return keyToValue.size();
	}

	/**
	 * Returns the hash code value for this map. The hash code of a map is
	 * defined to be the sum of the hashCodes of each entry in the map's
	 * entrySet view. This ensures that t1.equals(t2) implies that
	 * t1.hashCode()==t2.hashCode() for any two maps t1 and t2, as required by
	 * the general contract of Object.hashCode.
	 * 
	 */
	@Override
	public int hashCode() {
		return keyToValue.hashCode();
	}

	/**
	 * This is some sample code to illustrate the current usage.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BiHashMap<String, String> map = new BiHashMap<>();

		map.put("Dave", "ENGR202");
		map.put("Alex", "COMP205");
		map.put("James", "ENGR202");

		for (String x : map.getKeys("ENGR202")) {
			System.out.println("GOT: " + x);
		}
	}
}
