package xscript.runtime.nativemethod;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public interface XNativeFactory {
	
	public Object makeObject(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, XGenericClass xClass, XObject _this);
	
}
