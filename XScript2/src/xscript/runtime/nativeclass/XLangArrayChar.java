package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangArrayChar{
	
	
	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.ArrayChar", "operator[](int)char", new XMethodGetIndex());
		nativeProvider.addNativeMethod("xscript.lang.ArrayChar", "operator[](int, char)char", new XMethodSetIndex());
	}
	
	private static class XMethodGetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return getIndex(virtualMachine, _this, (Integer)params[0]);
		}
		
		private static char getIndex(XVirtualMachine virtualMachine, XObject _this, int index){
			return (char) _this.getArrayElement(index);
		}
		
	}
	
	private static class XMethodSetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return setIndex(virtualMachine, _this, (Integer)params[0], (Character)params[1]);
		}
		
		private static char setIndex(XVirtualMachine virtualMachine, XObject _this, int index, char value){
			_this.setArrayElement(index, value);
			return value;
		}
		
	}
	
}
