package xscript.runtime.nativemethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XNativeMethodImp implements XNativeMethod {
	
	private Method method;
	private boolean needVM;
	private boolean needThread;
	private boolean needME;
	private boolean needGenerics;
	private boolean needThis;
	private boolean needUserdata;
	private int paramCount;
	
	public XNativeMethodImp(Method method, boolean needVM, boolean needThread, boolean needME, boolean needGenerics, boolean needThis, boolean needUserdata) {
		this.method = method;
		this.needVM = needVM;
		this.needThread = needThread;
		this.needME = needME;
		this.needGenerics = needGenerics;
		this.needThis = needThis;
		this.needUserdata = needUserdata;
		paramCount = method.getParameterTypes().length;
	}

	@Override
	public Object invoke(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, XGenericClass[] generics, String name, XObject _this, Object[] params) {
		Object[] nparams = new Object[paramCount];
		int i=0;
		if(needVM){
			nparams[i++] = virtualMachine;
		}
		if(needThread){
			nparams[i++] = thread;
		}
		if(needME){
			nparams[i++] = methodExecutor;
		}
		if(needGenerics){
			nparams[i++] = generics;
		}
		if(needThis){
			nparams[i++] = _this;
		}
		if(needUserdata){
			nparams[i++] = virtualMachine.getUserData();
		}
		for(Object param:params){
			nparams[i++] = param;
		}
		Object ret;
		try {
			ret = method.invoke(_this.getNativeObject(), nparams);
		} catch (IllegalAccessException e) {
			throw new XRuntimeException(e, "Error while call Native");
		} catch (IllegalArgumentException e) {
			throw new XRuntimeException(e, "Error while call Native");
		} catch (InvocationTargetException e) {
			Throwable targetException = e.getTargetException();
			if(targetException instanceof RuntimeException){
				throw (RuntimeException)targetException;
			}else if(targetException instanceof Error){
				throw (Error)targetException;
			}
			throw new XRuntimeException(targetException, "Error in Native");
		}
		return ret;
	}

}
