package xscript.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.Invocable;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XClassProvider;
import xscript.runtime.clazz.XInputStreamSave;
import xscript.runtime.clazz.XOutputStreamSave;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.genericclass.XGenericMethodProviderImp;
import xscript.runtime.method.XMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.object.XObjectProvider;
import xscript.runtime.threads.XThreadProvider;

public class XVirtualMachine extends XMap<String, Map<String, Object>> implements Invocable{

	private XClassProvider classProvider;
	private XObjectProvider objectProvider;
	private XNativeProvider nativeProvider;
	private XThreadProvider threadProvider;
	private XTimer timer;
	private Object userData;
	
	public XVirtualMachine(XClassLoader standartClassLoader, int memSize){
		classProvider = new XClassProvider(this);
		objectProvider = new XObjectProvider(this, memSize);
		nativeProvider = new XNativeProvider(this, true);
		threadProvider = new XThreadProvider(this);
		classProvider.addClassLoader(standartClassLoader);
		timer = new XTimer();
	}
	
	public XVirtualMachine(List<XClassLoader> classLoaders, InputStream is, XTimer timer) throws IOException{
		XInputStreamSave dis = new XInputStreamSave(is);
		if(timer==null){
			timer = new XTimer();
		}
		this.timer = timer;
		nativeProvider = new XNativeProvider(this, false);
		classProvider = new XClassProvider(this, classLoaders);
		classProvider.loadAll(dis);
		nativeProvider.registerNatives();
		threadProvider = new XThreadProvider(this, dis);
		objectProvider = new XObjectProvider(this, dis);
	}
	
	public void save(OutputStream os) throws IOException{
		XOutputStreamSave outputStreamSave = new XOutputStreamSave();
		save(outputStreamSave);
		outputStreamSave.writeToOutputStream(os);
	}
	
	private void save(XOutputStreamSave dos) throws IOException{
		classProvider.save(dos);
		threadProvider.save(dos);
		objectProvider.save(dos);
	}
	
	public Object getUserData(){
		return userData;
	}
	
	public void setUserData(Object userData){
		this.userData = userData;
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

	public XTimer getTimer() {
		return timer;
	}
	
	public void setTimer(XTimer timer) {
		this.timer = timer;
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
	public Map<String, Object> put(String arg0, Map<String, Object> arg1) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends String, ? extends Map<String, Object>> arg0) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return classProvider.getAllLoadedClasses().size();
	}

	@Override
	protected Collection<? extends Map<String, Object>> getValues() {
		return classProvider.getAllLoadedClasses();
	}

	@Override
	protected Collection<? extends String> getKeys() {
		List<String> names = new ArrayList<String>();
		for(XClass c:classProvider.getAllLoadedClasses()){
			names.add(c.getName());
		}
		return names;
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
				obj = getObjectProvider().createArray(null, null, generic, XWrapper.castToInt(args[0]));
			}else if(generic.getXClass().getName().equals("xscript.lang.String")){
				obj = getObjectProvider().createString(null, null, (String)args[0]);
			}else{
				obj = getObjectProvider().createObject(null, null, generic);
			}
			return getObjectProvider().getObject(obj);
		}else{
			int g = name.indexOf('<');
			int c;
			if(g<i && g!=-1){
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
			XGenericClass[] paramtypes = method.getParams(xThis==null?null:xThis.getXClass(), generics==null?null:new XGenericMethodProviderImp(method, generics));
			for(int k=0; k<args.length; k++){
				int primitive = XPrimitive.getPrimitiveID(paramtypes[k].getXClass());
				long l = XWrapper.getXObject(objectProvider, primitive, args[k]);
				params[k+j] = l;
			}
			getThreadProvider().start(name, method, generics, params);
		}
		return null;
	}
	
}
