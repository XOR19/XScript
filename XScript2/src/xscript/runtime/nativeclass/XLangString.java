package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangString  {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.String", "add(xscript.lang.String, xscript.lang.String)xscript.lang.String", new XMethodAdd());
	}
	
	private static class XMethodAdd implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return (String)params[0] + (String)params[1];
		}
		
	}
	
}
