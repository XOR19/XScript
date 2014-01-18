package xscript.runtime.clazz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;

public class XClassProvider {

	protected XVirtualMachine virtualMachine;
	private XPackage rootPackage = new XPackage(null);
	private List<XClass> toPostLoad = new ArrayList<XClass>();
	private int rec = 0;
	private List<XClassLoader> classLoaders = new ArrayList<XClassLoader>();
	
	public final XPrimitive BOOL;
	public final XPrimitive BYTE;
	public final XPrimitive CHAR;
	public final XPrimitive SHORT;
	public final XPrimitive INT;
	public final XPrimitive LONG;
	public final XPrimitive FLOAT;
	public final XPrimitive DOUBLE;
	public final XPrimitive VOID;
	
	public XClassProvider(XVirtualMachine virtualMachine){
		this.virtualMachine = virtualMachine;
		BOOL = createPrimitive(XPrimitive.BOOL);
		BYTE = createPrimitive(XPrimitive.BYTE);
		CHAR = createPrimitive(XPrimitive.CHAR);
		SHORT = createPrimitive(XPrimitive.SHORT);
		INT = createPrimitive(XPrimitive.INT);
		LONG = createPrimitive(XPrimitive.LONG);
		FLOAT = createPrimitive(XPrimitive.FLOAT);
		DOUBLE = createPrimitive(XPrimitive.DOUBLE);
		VOID = createPrimitive(XPrimitive.VOID);
	}
	
	private XPrimitive createPrimitive(int id){
		XPrimitive p = new XPrimitive(virtualMachine, id, rootPackage);
		rootPackage.addChild(p);
		return p;
	}
	
	public XPackage getPackage(String name) {
		XPackage xPackage = rootPackage.getChild(name);
		if(xPackage==null){
			rec++;
			try{
				createClass(name);
				xPackage = rootPackage.getChild(name);
				if(xPackage==null){
					throw new XRuntimeException("Class %s not found", name);
				}
				if(rec==1){
					postLoad();
				}
			}catch(Throwable e){
				rec--;
				if(!(e instanceof XRuntimeException)){
					e = new XRuntimeException(e, "Native error while load Class %s", name);
				}
				throw (XRuntimeException)e;
			}
			rec--;
		}
		if(xPackage instanceof XClassMaker){
			XClass xClass = ((XClassMaker)xPackage).makeClass(xPackage.getParent());
			xPackage.getParent().overridePackage(xPackage.getSimpleName(), xClass);
			((XClassMaker)xPackage).onReplaced(xClass);
			xClass.onRequest();
			xPackage = rootPackage.getChild(name);
		}
		if(xPackage instanceof XClass){
			((XClass) xPackage).onRequest();
		}
		return xPackage;
	}
	
	public XClass getXClass(String name) {
		XPackage xPackage = getPackage(name);
		if(xPackage instanceof XClass){
			return (XClass) xPackage;
		}
		throw new XRuntimeException("Class %s not found", name);
		//if(xClass.getState()==XClass.STATE_ERRORED){
		//	throw new XRuntimeException("Class %s errored", name);
		//}
	}
	
	private void postLoad(){
		while(!toPostLoad.isEmpty()){
			XClass xClass = toPostLoad.remove(0);
			xClass.postLoad();
		}
	}
	
	protected void createClass(String name) {
		XInputStream inputStream = getInputStream(name);
		if(inputStream!=null){
			String fileName = inputStream.getFileName();
			String s[] = fileName.split("\\.");
			XPackage p = getClassToPackage(s);
			XClass xClass = new XClass(virtualMachine, s[s.length-1], p);
			p.addChild(xClass);
			try{
				xClass.load(inputStream);
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try {
					inputStream.close();
				} catch (IOException e) {}
			}
		}
	}

	protected void addClassForLoading(XClass xClass){
		toPostLoad.add(xClass);
	}
	
	protected void removeClassForLoading(XClass xClass){
		toPostLoad.remove(xClass);
	}
	
	protected XInputStream getInputStream(String name){
		for(XClassLoader classLoader:classLoaders){
			XInputStream inputStream = classLoader.getInputStream(name);
			if(inputStream!=null)
				return inputStream;
		}
		return null;
	}
	
	protected XPackage getClassToPackage(String s[]){
		XPackage xPackage = rootPackage;
		for(int i=0; i<s.length-1; i++){
			XPackage xPackage2 = xPackage.getChild(s[i]);
			if(xPackage2==null){
				xPackage2 = new XPackage(s[i]);
				xPackage.addChild(xPackage2);
			}
			xPackage = xPackage2;
		}
		return xPackage;
	}
	
	public void addClassMaker(XClassMaker maker, String name){
		String s[] = name.split("\\.");
		XPackage xPackage = rootPackage;
		for(int i=0; i<s.length-1; i++){
			XPackage xPackage2 = xPackage.getChild(s[i]);
			if(xPackage2==null){
				xPackage2 = new XPackage(s[i]);
				xPackage.addChild(xPackage2);
			}
			xPackage = xPackage2;
		}
		xPackage.addChild(maker);
	}
	
	public void markVisible() {
		rootPackage.markVisible();
	}
	
	public void addClassLoader(XClassLoader classLoader){
		if(!classLoaders.contains(classLoader))
			classLoaders.add(classLoader);
	}

	public XClass getLoadedXClass(String name) {
		XPackage xPackage = rootPackage.getChild(name);
		if(xPackage instanceof XClass){
			XClass xClass = (XClass)xPackage;
			if(xClass.getState()==XClass.STATE_RUNNABLE){
				return xClass;
			}
		}
		return null;
	}

	public boolean existsClass(String className) {
		return rootPackage.getChild(className) instanceof XClass;
	}

	public List<XClass> getAllLoadedClasses() {
		List<XClass> classes = new ArrayList<XClass>();
		rootPackage.addChildClasses(classes);
		return classes;
	}
	
}
