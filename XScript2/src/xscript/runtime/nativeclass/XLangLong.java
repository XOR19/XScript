package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangLong  {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.Long", "toString{(long, int)xscript.lang.String}", new XMethodToString());
		nativeProvider.addNativeMethod("xscript.lang.Long", "toUnsignedString{(long, int)xscript.lang.String}", new XMethodToUnsigendString());
	}
	
	private static class XMethodToString implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return Long.toString((Long)params[0], (Integer)params[1]);
		}
		
	}
	
	private static class XMethodToUnsigendString implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return toUnsignedString((Long)params[0], (Integer)params[1]);
		}
		
		private static String toUnsignedString(long i, int shift) {
	        char[] buf = new char[64];
	        int charPos = 64;
	        int radix = 1 << shift;
	        int mask = radix - 1;
	        do {
	            buf[--charPos] = digits[(int) (i & mask)];
	            i >>>= shift;
	        } while (i != 0);

	        return new String(buf, charPos, (64 - charPos));
	    }
		
		 final static char[] digits = {
		        '0' , '1' , '2' , '3' , '4' , '5' ,
		        '6' , '7' , '8' , '9' , 'a' , 'b' ,
		        'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
		        'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
		        'o' , 'p' , 'q' , 'r' , 's' , 't' ,
		        'u' , 'v' , 'w' , 'x' , 'y' , 'z'
		    };
		
	}
	
}
