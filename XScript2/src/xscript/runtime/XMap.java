package xscript.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class XMap<K, V> implements Map<K, V>{

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return getKeys().contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getValues().contains(value);
	}

	@Override
	public Set<Entry<K, V>> entrySet(){
		Collection<? extends K> keys = getKeys();
		List<Entry<K, V>> entries = new ArrayList<Map.Entry<K,V>>(keys.size());
		for(K key:keys){
			entries.add(new KeyEntry(key));
		}
		return new XSet<Entry<K, V>>(entries);
	}
	
	private class KeyEntry implements Entry<K, V>{

		private K key;
		
		public KeyEntry(K key){
			this.key = key;
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return get(key);
		}

		@Override
		public V setValue(V value) {
			return put(key, value);
		}
		
	}
	
	@Override
	public boolean isEmpty() {
		return size()==0;
	}

	@Override
	public Set<K> keySet() {
		return new XSet<K>(getKeys());
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for(Entry<? extends K, ? extends V> e:m.entrySet()){
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return getKeys().size();
	}
	
	@Override
	public Collection<V> values() {
		return new ArrayList<V>(getValues());
	}

	protected abstract Collection<? extends V> getValues();
	
	protected abstract Collection<? extends K> getKeys();
	
}
