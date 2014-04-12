package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangArrayByte {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.ArrayByte", "operator[]{(int)byte}", new XMethodGetIndex());
		nativeProvider.addNativeMethod("xscript.lang.ArrayByte", "operator[]{(int, byte)byte}", new XMethodSetIndex());
	}
	
	private static class XMethodGetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return getIndex(virtualMachine, _this, (Integer)params[0]);
		}
		
		private static byte getIndex(XVirtualMachine virtualMachine, XObject _this, int index){
			return (byte) _this.getArrayElement(index);
		}
		
	}
	
	private static class XMethodSetIndex implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return setIndex(virtualMachine, _this, (Integer)params[0], (Byte)params[1]);
		}
		
		private static byte setIndex(XVirtualMachine virtualMachine, XObject _this, int index, byte value){
			_this.setArrayElement(index, value);
			return value;
		}
		
	}
	
}
