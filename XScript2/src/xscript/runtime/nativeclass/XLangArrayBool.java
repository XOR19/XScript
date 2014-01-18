package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangArrayBool {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.ArrayBool", "operator[](int)bool", new XMethodGetIndex());
		nativeProvider.addNativeMethod("xscript.lang.ArrayBool", "operator[](int, bool)bool", new XMethodSetIndex());
	}
	
	private static class XMethodGetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return getIndex(virtualMachine, _this, (Integer)params[0]);
		}
		
		private static boolean getIndex(XVirtualMachine virtualMachine, XObject _this, int index){
			return _this.getArrayElement(index)!=0;
		}
		
	}
	
	private static class XMethodSetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return setIndex(virtualMachine, _this, (Integer)params[0], (Boolean)params[1]);
		}
		
		private static boolean setIndex(XVirtualMachine virtualMachine, XObject _this, int index, boolean value){
			_this.setArrayElement(index, value?-1:0);
			return value;
		}
		
	}
	
}
