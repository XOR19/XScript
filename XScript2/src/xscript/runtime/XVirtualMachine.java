package xscript.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XClassProvider;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.genericclass.XGenericMethodProviderImp;
import xscript.runtime.method.XMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.object.XObjectProvider;
import xscript.runtime.threads.XThreadProvider;

public class XVirtualMachine implements Map<String, Map<String, Object>>, Invocable{

	private XClassProvider classProvider;
	private XObjectProvider objectProvider;
	private XNativeProvider nativeProvider;
	private XThreadProvider threadProvider;

	public XVirtualMachine(XClassLoader standartClassLoader, int memSize){
		classProvider = new XClassProvider(this);
		objectProvider = new XObjectProvider(this, memSize);
		nativeProvider = new XNativeProvider(this);
		threadProvider = new XThreadProvider(this);
		classProvider.addClassLoader(standartClassLoader);
	}
	
	public XClassProvider getClassProvider() {
		return classProvider;
	}
	
	public XObjectProvider getObjectProvider() {
		return objectProvider;
	}
	
	public XNativeProvider getNativeProvider() {
		return nativeProvider;
	}
	
	public XThreadProvider getThreadProvider() {
		return threadProvider;
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object name) {
		if(name instanceof String){
			return classProvider.getLoadedXClass((String)name)!=null;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object xClass) {
		if(xClass instanceof XClass){
			return ((XClass) xClass).getVirtualMachine()==this;
		}else if(xClass instanceof XGenericClass){
			return ((XGenericClass) xClass).getXClass().getVirtualMachine()==this;
		}
		return false;
	}

	@Override
	public Set<Entry<String, Map<String, Object>>> entrySet() {
		List<Entry<String, Map<String, Object>>> entries = new ArrayList<Entry<String, Map<String, Object>>>();
		for(XClass c:classProvider.getAllLoadedClasses()){
			entries.add(new ClassEntry(c));
		}
		return new XSet<Map.Entry<String, Map<String, Object>>>(entries);
	}

	private static class ClassEntry implements Entry<String, Map<String, Object>>{

		private XClass c;
		
		public ClassEntry(XClass c){
			this.c = c;
		}
		
		@Override
		public String getKey() {
			return c.getName();
		}

		@Override
		public Map<String, Object> getValue() {
			return c;
		}

		@Override
		public Map<String, Object> setValue(Map<String, Object> arg0) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@Override
	public Map<String, Object> get(Object name) {
		if(name instanceof String){
			return classProvider.getXClass((String)name);
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<String> keySet() {
		List<String> names = new ArrayList<String>();
		for(XClass c:classProvider.getAllLoadedClasses()){
			names.add(c.getName());
		}
		return new XSet<String>(names);
	}

	@Override
	public Map<String, Object> put(String arg0, Map<String, Object> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Map<String, Object>> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Object> remove(Object arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return classProvider.getAllLoadedClasses().size();
	}

	@Override
	public Collection<Map<String, Object>> values() {
		return new XSet<Map<String, Object>>(classProvider.getAllLoadedClasses());
	}

	@Override
	public <T> T getInterface(Class<T> clasz) {
		return null;
	}

	@Override
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		return null;
	}

	@Override
	public Object invokeFunction(String name, Object... args) throws NoSuchMethodException {
		return invokeMethod(null, name, args);
	}
	
	private XGenericClass[] makeGenericParams(String name){
		name = name.trim();
		if(name.isEmpty())
			return null;
		List<XGenericClass> generics = new ArrayList<XGenericClass>();
		int h = 0;
		String cname = "";
		for(int i=1; i<name.length(); i++){
			char ch = name.charAt(i);
			if(ch=='<'){
				h++;
			}else if(ch=='>'){
				if(h==0){
					generics.add(makeGenericClass(cname));
					break;
				}
				h--;
			}else if(h==0 && ch==','){
				generics.add(makeGenericClass(cname));
				cname = "";
			}else{
				cname += ch;
			}
		}
		return generics.toArray(new XGenericClass[generics.size()]);
	}
	
	private XGenericClass makeGenericClass(String name){
		name = name.trim();
		int i = name.indexOf('<');
		if(i==-1){
			XClass c = getClassProvider().getXClass(name);
			return new XGenericClass(c);
		}
		XClass c = getClassProvider().getXClass(name.substring(0, i));
		return new XGenericClass(c, makeGenericParams(name.substring(i)));
	}
	
	@Override
	public Object invokeMethod(Object thiz, String name, Object... args) throws NoSuchMethodException {
		int i = name.indexOf('(');
		if(i==-1){
			XGenericClass generic = makeGenericClass(name);
			long obj;
			if(generic.getXClass().isArray()){
				obj = getObjectProvider().createArray(generic, XWrapper.castToInt(args[0]));
			}else if(generic.getXClass().getName().equals("xscript.lang.String")){
				obj = getObjectProvider().createString((String)args[0]);
			}else{
				obj = getObjectProvider().createObject(generic);
			}
			return getObjectProvider().getObject(obj);
		}else{
			int g = name.indexOf('<');
			int c;
			if(g<i){
				c = g-1;
			}else{
				g = -1;
				c = name.lastIndexOf('.', i);
			}
			XClass xc = getClassProvider().getXClass(name.substring(0, c));
			XGenericClass[] generics = null;
			if(g!=-1){
				generics = makeGenericParams(name.substring(g, c = name.lastIndexOf('>', i)));
			}
			XMethod method = xc.getMethod(name.substring(c+1));
			long params[];
			int j;
			XObject xThis;
			if(XModifier.isStatic(method.getModifier())){
				xThis = null;
				j=0;
				params = new long[args.length];
			}else{
				params = new long[args.length+1];
				xThis = (XObject)thiz;
				params[0] = getObjectProvider().getPointer(xThis);
				j=1;
			}
			XGenericClass[] paramtypes = method.getParams(xThis==null?null:xThis.getXClass(), new XGenericMethodProviderImp(method, generics));
			for(int k=0; k<args.length; k++){
				int primitive = XPrimitive.getPrimitiveID(paramtypes[k].getXClass());
				long l = XWrapper.getXObject(objectProvider, primitive, args[k]);
				params[k+j] = l;
			}
			getThreadProvider().interrupt(name, null, method, generics, params);
		}
		return null;
	}
	
}
