package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangArrayDouble {
	
	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.ArrayDouble", "operator[](int)double", new XMethodGetIndex());
		nativeProvider.addNativeMethod("xscript.lang.ArrayDouble", "operator[](int, double)double", new XMethodSetIndex());
	}
	
	private static class XMethodGetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return getIndex(virtualMachine, _this, (Integer)params[0]);
		}
		
		private static double getIndex(XVirtualMachine virtualMachine, XObject _this, int index){
			return _this.getArrayElement(index);
		}
		
	}
	
	private static class XMethodSetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				XObject _this, Object[] params) {
			return setIndex(virtualMachine, _this, (Integer)params[0], (Double)params[1]);
		}
		
		private static double setIndex(XVirtualMachine virtualMachine, XObject _this, int index, double value){
			_this.setArrayElement(index, Double.doubleToLongBits(value));
			return value;
		}
		
	}
	
}
