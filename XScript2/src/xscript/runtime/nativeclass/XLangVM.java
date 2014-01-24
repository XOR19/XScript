package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangVM {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.VM", "println(xscript.lang.String)void", new XMethodPrintln());
	}
	
	private static class XMethodPrintln implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			System.out.println(virtualMachine.getObjectProvider().getString((XObject)params[0]));
			return null;
		}
		
	}
	
}
