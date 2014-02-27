package xscript.runtime.nativemethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XNativeFactoryImp implements XNativeFactory {
	
	private Method method;
	private boolean needVM;
	private boolean needThread;
	private boolean needME;
	private boolean needThis;
	private int paramCount;
	
	public XNativeFactoryImp(Method method, boolean needVM, boolean needThread, boolean needME, boolean needThis) {
		this.method = method;
		this.needVM = needVM;
		this.needThread = needThread;
		this.needME = needME;
		this.needThis = needThis;
		paramCount = method.getParameterTypes().length;
	}

	@Override
	public Object makeObject(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass, XObject _this) {
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
		if(needThis){
			nparams[i++] = _this;
		}
		Object ret;
		try {
			ret = method.invoke(null, nparams);
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
