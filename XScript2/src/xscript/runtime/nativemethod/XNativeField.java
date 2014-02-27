package xscript.runtime.nativemethod;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public interface XNativeField {

	public void set(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this, Object value);
	
	public Object get(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this);
	
}
