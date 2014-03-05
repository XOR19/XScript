package xscript.runtime.nativemethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.clazz.XWrapper;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.genericclass.XGenericMethodProviderImp;
import xscript.runtime.method.XMethod;
import xscript.runtime.nativeclass.XLangArray;
import xscript.runtime.nativeclass.XLangArrayBool;
import xscript.runtime.nativeclass.XLangArrayByte;
import xscript.runtime.nativeclass.XLangArrayChar;
import xscript.runtime.nativeclass.XLangArrayDouble;
import xscript.runtime.nativeclass.XLangArrayFloat;
import xscript.runtime.nativeclass.XLangArrayInt;
import xscript.runtime.nativeclass.XLangArrayLong;
import xscript.runtime.nativeclass.XLangArrayShort;
import xscript.runtime.nativeclass.XLangDouble;
import xscript.runtime.nativeclass.XLangFloat;
import xscript.runtime.nativeclass.XLangInt;
import xscript.runtime.nativeclass.XLangLong;
import xscript.runtime.nativeclass.XLangObject;
import xscript.runtime.nativeclass.XLangString;
import xscript.runtime.nativeclass.XLangThread;
import xscript.runtime.nativeclass.XLangVM;
import xscript.runtime.nativemethod.XNativeClass.XType;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XNativeProvider {

	private int TIME_OUT=-1;
	private XVirtualMachine virtualMachine;
	private HashMap<String, XNativeMethod> nativeMethods = new HashMap<String, XNativeMethod>();
	private HashMap<String, XNativeField> nativeFields = new HashMap<String, XNativeField>();
	private HashMap<String, XNativeFactory> nativeFactories = new HashMap<String, XNativeFactory>();
	
	public XNativeProvider(XVirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
		XLangArray.registerNatives(this);
		XLangArrayBool.registerNatives(this);
		XLangArrayByte.registerNatives(this);
		XLangArrayChar.registerNatives(this);
		XLangArrayDouble.registerNatives(this);
		XLangArrayFloat.registerNatives(this);
		XLangArrayInt.registerNatives(this);
		XLangArrayLong.registerNatives(this);
		XLangArrayShort.registerNatives(this);
		XLangInt.registerNatives(this);
		XLangLong.registerNatives(this);
		XLangFloat.registerNatives(this);
		XLangDouble.registerNatives(this);
		XLangObject.registerNatives(this);
		XLangString.registerNatives(this);
		XLangThread.registerNatives(this);
		XLangVM.registerNatives(this);
	}

	public void call(XThread thread, XMethodExecutor methodExecutor, XMethod method, XGenericClass[] generics, long[] params) {
		XNativeMethod nativeMethod = method.getNativeMethod();
		if(nativeMethod==null)
			throw new XRuntimeException("Native %s not found", method.getName());
		XObject _this = null;
		int i=0;
		if(!XModifier.isStatic(method.getModifier())){
			_this = virtualMachine.getObjectProvider().getObject(params[0]);
			i=1;
		}
		Object[] oParam = new Object[params.length-i];
		XGenericClass[] genericClasses = method.getParams(_this==null?null:_this.getXClass(), new XGenericMethodProviderImp(method, generics));
		if(oParam.length!=genericClasses.length)
			throw new XRuntimeException("An native call error happened %s", method.getName());
		for(int j=0; j<oParam.length; j++){
			long value = params[j+i];
			XGenericClass genericClass = genericClasses[j];
			oParam[j] = XWrapper.getJavaObject(virtualMachine.getObjectProvider(), genericClass, value);
		}
		
		Object ret = invokeNative(nativeMethod, thread, methodExecutor, generics, method.getName(), _this, oParam);
		XGenericClass genericClass  = method.getReturnType(_this==null?null:_this.getXClass(), new XGenericMethodProviderImp(method, generics));
		if(XPrimitive.getPrimitiveID(genericClass.getXClass())!=XPrimitive.VOID){
			long l = XWrapper.getXObject(virtualMachine.getObjectProvider(), genericClass, ret);
			methodExecutor.push(l, XPrimitive.getPrimitiveID(genericClass.getXClass()));
		}
	}

	private Object invokeNative(XNativeMethod nativeMethod, XThread thread, XMethodExecutor methodExecutor, XGenericClass[] generics, String name, XObject _this, Object[] params){
		if(TIME_OUT==-1){
			return nativeMethod.invoke(virtualMachine, thread, methodExecutor, generics, name, _this, params);
		}else{
			Object sync = new Object();
			InvokeThread invoke = new InvokeThread(sync, nativeMethod, virtualMachine, thread, methodExecutor, generics, name, _this, params);
			synchronized(sync){
				while(true){
					try {
						sync.wait(TIME_OUT);
						break;
					} catch (InterruptedException e) {}
				}
			}
			if(invoke.hasResult())
				return invoke.getReturn();
			throw new XRuntimeException("Timeout of native call");
		}
	}
	
	public void set(XThread thread, XMethodExecutor methodExecutor, XField field, XObject _this, long value) {
		XNativeField nativeField = field.getNativeField();
		if(nativeField==null)
			throw new XRuntimeException("Native %s not found", field.getName());
		XGenericClass gc = _this==null?null:_this.getXClass();
		Object oValue =  XWrapper.getJavaObject(virtualMachine.getObjectProvider(), gc, value);
		setNative(nativeField, thread, methodExecutor, field.getName(), _this, oValue);
	}
	
	private void setNative(XNativeField nativeField, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this, Object value){
		if(TIME_OUT==-1){
			nativeField.set(virtualMachine, thread, methodExecutor, name, _this, value);
		}else{
			Object sync = new Object();
			SetThread invoke = new SetThread(sync, nativeField, virtualMachine, thread, methodExecutor, name, _this, value);
			synchronized(sync){
				while(true){
					try {
						sync.wait(TIME_OUT);
						break;
					} catch (InterruptedException e) {}
				}
			}
			if(invoke.hasResult())
				return;
			throw new XRuntimeException("Timeout of native call");
		}
	}
	
	public long get(XThread thread, XMethodExecutor methodExecutor, XField field, XObject _this) {
		XNativeField nativeField = field.getNativeField();
		if(nativeField==null)
			throw new XRuntimeException("Native %s not found", field.getName());
		Object ret = getNative(nativeField, thread, methodExecutor, field.getName(), _this);
		XGenericClass genericClass = field.getType(_this==null?null:_this.getXClass());
		return XWrapper.getXObject(virtualMachine.getObjectProvider(), genericClass, ret);
	}
	
	private Object getNative(XNativeField nativeField, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this){
		if(TIME_OUT==-1){
			return nativeField.get(virtualMachine, thread, methodExecutor, name, _this);
		}else{
			Object sync = new Object();
			GetThread invoke = new GetThread(sync, nativeField, virtualMachine, thread, methodExecutor, name, _this);
			synchronized(sync){
				while(true){
					try {
						sync.wait(TIME_OUT);
						break;
					} catch (InterruptedException e) {}
				}
			}
			if(invoke.hasResult())
				return invoke.getReturn();
			throw new XRuntimeException("Timeout of native call");
		}
	}
	
	public XNativeMethod removeNativeMethod(String name) {
		return nativeMethods.remove(name);
	}
	
	public XNativeField removeNativeField(String name) {
		return nativeFields.remove(name);
	}
	
	public XNativeFactory removeNativeFactory(String name) {
		return nativeFactories.remove(name);
	}

	public void addNativeMethod(String classname, String name, XNativeMethod nativeMethod){
		XClass xClass = virtualMachine.getClassProvider().getLoadedXClass(classname);
		XMethod method = xClass==null?null:xClass.getMethod(name);
		if(method==null){
			if(nativeMethod==null){
				nativeMethods.remove(classname+"."+name);
			}else{
				nativeMethods.put(classname+"."+name, nativeMethod);
			}
		}else{
			method.setNativeMethod(nativeMethod);
		}
	}
	
	public void addNativeField(String classname, String name, XNativeField nativeField){
		XClass xClass = virtualMachine.getClassProvider().getLoadedXClass(classname);
		XField field = xClass==null?null:xClass.getField(name);
		if(field==null){
			if(nativeField==null){
				nativeFields.remove(classname+"."+name);
			}else{
				nativeFields.put(classname+"."+name, nativeField);
			}
		}else{
			field.setNativeField(nativeField);
		}
	}
	
	public void addNativeFactroy(String classname, XNativeFactory nativeFactory){
		XClass xClass = virtualMachine.getClassProvider().getLoadedXClass(classname);
		if(xClass==null){
			if(nativeFactory==null){
				nativeFactories.remove(classname);
			}else{
				nativeFactories.put(classname, nativeFactory);
			}
		}else{
			xClass.setNativeFactory(nativeFactory);
		}
	}
	
	public void addNativeClass(Class<?> nativeClass){
		
		XNativeClass classInfo = nativeClass.getAnnotation(XNativeClass.class);
		
		if(classInfo!=null){
			
			Method[] methods = nativeClass.getMethods();
			
			for(Method method:methods){
				
				XNativeClass.XNativeMethod methodInfo = method.getAnnotation(XNativeClass.XNativeMethod.class);
				
				if(methodInfo!=null){
					
					XParamScanner paramScanner = new XParamScanner();
					
					paramScanner.scann(method);
					
					String name = methodInfo.value();
					
					if(name.isEmpty()){
						name = method.getName();
					}
					
					System.out.println("add:"+name+paramScanner.getDesk());
					
					addNativeMethod(classInfo.value(), name+paramScanner.getDesk(), new XNativeMethodImp(method, paramScanner.isNeedVM(), 
							paramScanner.isNeedThread(), paramScanner.isNeedME(), paramScanner.isNeedGenerics(), paramScanner.isNeedThis()));
					
				}else{
					
					XNativeClass.XNativeFactory factoryInfo = method.getAnnotation(XNativeClass.XNativeFactory.class);
					
					if(factoryInfo!=null){
						
						XParamScanner paramScanner = new XParamScanner();
						
						paramScanner.scann(method);
						
						System.out.println("add:"+classInfo.value());
						
						addNativeFactroy(classInfo.value(), new XNativeFactoryImp(method, paramScanner.isNeedVM(), 
								paramScanner.isNeedThread(), paramScanner.isNeedME(), paramScanner.isNeedThis()));
						
					}
					
				}
				
			}
			
			Field[] fields = nativeClass.getFields();
			
			for(Field field:fields){
				
				XNativeClass.XNativeField fieldInfo = field.getAnnotation(XNativeClass.XNativeField.class);
				
				if(fieldInfo!=null){
					
					String name = fieldInfo.value();
					
					if(name.isEmpty()){
						name = field.getName();
					}
					
					System.out.println("add:"+name+":"+XParamScanner.getClassName(field.getAnnotation(XType.class), field.getClass()));
					
					addNativeField(classInfo.value(), name+":"+XParamScanner.getClassName(field.getAnnotation(XType.class), field.getClass()), 
							new XNativeFieldImp(field));
					
				}
				
			}
		
			addNativeClass(nativeClass.getSuperclass());
			
		}
		
	}
	
	private static class InvokeThread extends Thread{
		
		private Object ret;
		private boolean hasRet;
		private Object sync;
		private XNativeMethod nativeMethod;
		private XVirtualMachine virtualMachine;
		private XThread thread;
		private XMethodExecutor methodExecutor;
		private XGenericClass[] generics;
		private String name;
		private XObject _this;
		private Object[] params;
		
		public InvokeThread(Object sync, XNativeMethod nativeMethod, XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, XGenericClass[] generics, String name, XObject _this, Object[] params){
			this.sync = sync;
			this.nativeMethod = nativeMethod;
			this.virtualMachine = virtualMachine;
			this.thread = thread;
			this.methodExecutor = methodExecutor;
			this.generics = generics;
			this.name = name;
			this._this = _this;
			this.params = params;
			setDaemon(true);
			start();
		}

		public void run(){
			ret = nativeMethod.invoke(virtualMachine, thread, methodExecutor, generics, name, _this, params);
			hasRet = true;
			synchronized(sync){
				sync.notify();
			}
		}
		
		private Object getReturn(){
			return ret;
		}
		
		public boolean hasResult() {
			return hasRet;
		}
		
	}
	
	private static class SetThread extends Thread{
		
		private boolean hasRet;
		private Object sync;
		private XNativeField nativeField;
		private XVirtualMachine virtualMachine;
		private XThread thread;
		private XMethodExecutor methodExecutor;
		private String name;
		private XObject _this;
		private Object value;
		
		public SetThread(Object sync, XNativeField nativeField, XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this, Object value){
			this.sync = sync;
			this.nativeField = nativeField;
			this.virtualMachine = virtualMachine;
			this.thread = thread;
			this.methodExecutor = methodExecutor;
			this.name = name;
			this._this = _this;
			this.value = value;
			setDaemon(true);
			start();
		}

		public void run(){
			nativeField.set(virtualMachine, thread, methodExecutor, name, _this, value);
			hasRet = true;
			synchronized(sync){
				sync.notify();
			}
		}
		
		public boolean hasResult() {
			return hasRet;
		}
		
	}
	
	private static class GetThread extends Thread{
		
		private Object ret;
		private boolean hasRet;
		private Object sync;
		private XNativeField nativeField;
		private XVirtualMachine virtualMachine;
		private XThread thread;
		private XMethodExecutor methodExecutor;
		private String name;
		private XObject _this;
		
		public GetThread(Object sync, XNativeField nativeField, XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this){
			this.sync = sync;
			this.nativeField = nativeField;
			this.virtualMachine = virtualMachine;
			this.thread = thread;
			this.methodExecutor = methodExecutor;
			this.name = name;
			this._this = _this;
			setDaemon(true);
			start();
		}

		public void run(){
			ret = nativeField.get(virtualMachine, thread, methodExecutor, name, _this);
			hasRet = true;
			synchronized(sync){
				sync.notify();
			}
		}
		
		private Object getReturn(){
			return ret;
		}
		
		public boolean hasResult() {
			return hasRet;
		}
		
	}
	
}
