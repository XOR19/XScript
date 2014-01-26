package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangInt  {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.Int", "toHex(long)xscript.lang.String", new XMethodToHex());
	}
	
	private static class XMethodToHex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return Long.toHexString((Long)params[0]);
		}
		
	}
	
}
