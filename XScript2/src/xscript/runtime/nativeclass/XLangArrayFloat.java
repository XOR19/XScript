package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangArrayFloat {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.ArrayFloat", "operator[](int)float", new XMethodGetIndex());
		nativeProvider.addNativeMethod("xscript.lang.ArrayFloat", "operator[](int, float)float", new XMethodSetIndex());
	}
	
	private static class XMethodGetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return getIndex(virtualMachine, _this, (Integer)params[0]);
		}
		
		private static float getIndex(XVirtualMachine virtualMachine, XObject _this, int index){
			return _this.getArrayElement(index);
		}
		
	}
	
	private static class XMethodSetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return setIndex(virtualMachine, _this, (Integer)params[0], (Float)params[1]);
		}
		
		private static float setIndex(XVirtualMachine virtualMachine, XObject _this, int index, float value){
			_this.setArrayElement(index, Float.floatToIntBits(value));
			return value;
		}
		
	}
	
}