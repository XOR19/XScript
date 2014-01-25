package xscript.runtime.clazz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import xscript.runtime.XMap;
import xscript.runtime.XSet;

public class XPackage extends XMap<String, Object> {

	protected String name;
	protected XPackage parent;
	protected HashMap<String, XPackage> childs = new HashMap<String, XPackage>();
	
	public XPackage(String name){
		this.name = name;
	}
	
	public XPackage getChild(String name){
		String names[] = name.split("\\.", 2);
		XPackage child = childs.get(names[0]);
		if(child==null)
			return null;
		if(names.length>1){
			return child.getChild(names[1]);
		}
		return child;
	}
	
	public void addChild(XPackage child){
		childs.put(child.getSimpleName(), child);
		child.parent = this;
	}

	public String getName() {
		if(parent==null){
			return getSimpleName();
		}else{
			String s = parent.getName();
			if(s==null){
				s = getSimpleName();
			}else{
				s += "."+getSimpleName();
			}
			return s;
		}
	}
	
	@Override
	public String toString(){
		return getName();
	}
	
	public String getSimpleName(){
		return name;
	}

	public void markVisible() {
		for(XPackage child:childs.values()){
			child.markVisible();
		}
	}

	public void overridePackage(String name, XPackage xPackage) {
		childs.put(name, xPackage);
		xPackage.parent = this;
	}

	public XPackage getParent() {
		return parent;
	}
	
	public void remove(String name){
		childs.remove(name);
	}

	@Override
	public boolean containsKey(Object key) {
		return childs.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return childs.containsValue(value);
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		List<Entry<String, Object>> entries = new ArrayList<Entry<String, Object>>();
		for(Entry<String, XPackage> e:childs.entrySet()){
			entries.add(new PackageEntry(e));
		}
		return new XSet<Entry<String,Object>>(entries);
	}

	private static class PackageEntry implements Entry<String, Object>{

		private Entry<String, ? extends Object> e;
		
		public PackageEntry(Entry<String, ? extends Object> e){
			this.e = e;
		}
		
		@Override
		public String getKey() {
			return e.getKey();
		}

		@Override
		public Object getValue() {
			return e.getValue();
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public Object get(Object key) {
		return childs.get(key);
	}

	@Override
	public boolean isEmpty() {
		return childs.isEmpty();
	}

	@Override
	public Map<String, Map<String, ?>> put(String key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return childs.size();
	}

	public void addChildClasses(List<XClass> classes) {
		if(this instanceof XClass){
			classes.add((XClass) this);
		}
		for(XPackage p:childs.values()){
			p.addChildClasses(classes);
		}
	}

	@Override
	protected Collection<? extends Object> getValues() {
		return childs.values();
	}

	@Override
	protected Collection<String> getKeys() {
		return childs.keySet();
	}
	
}
