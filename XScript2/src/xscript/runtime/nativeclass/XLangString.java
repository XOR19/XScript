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
		nativeProvider.addNativeMethod("xscript.lang.String", "add{(xscript.lang.String, xscript.lang.String)xscript.lang.String}", new XMethodAdd());
		nativeProvider.addNativeMethod("xscript.lang.String", "equals{(xscript.lang.String, xscript.lang.Object)bool}", new XMethodEquals());
	}
	
	private static class XMethodAdd implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return (String)params[0] + (String)params[1];
		}
		
	}

	private static class XMethodEquals implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			if(params[1] instanceof String){
				return params[0].equals(params[1]);
			}
			return false;
		}
		
	}
	
}
