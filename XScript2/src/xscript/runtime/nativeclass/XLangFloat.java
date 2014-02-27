package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangFloat  {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.Float", "toString(float)xscript.lang.String", new XMethodToString());
	}
	
	private static class XMethodToString implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return Float.toString((Float)params[0]);
		}
		
	}
	
}
