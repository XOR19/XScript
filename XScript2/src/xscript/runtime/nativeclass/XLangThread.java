package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.method.XMethod;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangThread {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.Thread", "sleep(long)void", new XMethodSleep());
		nativeProvider.addNativeMethod("xscript.lang.Thread", "getCurrentThread()xscript.lang.Thread", new XMethodGetCurrentThread());
		nativeProvider.addNativeMethod("xscript.lang.Thread", "start()void", new XMethodStart());
	}
	
	private static class XMethodSleep implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			thread.sleep((Long)params[0]);
			return null;
		}
		
	}
	
	private static class XMethodGetCurrentThread implements XNativeMethod{
		
		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return thread.getThreadObject();
		}
		
	}
	
	private static class XMethodStart implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			XClass c = virtualMachine.getClassProvider().getXClass("xscript.lang.Thread");
			XMethod method = c.getMethod("run()void");
			method = method.getMethod(_this);
			String tname = virtualMachine.getThreadProvider().getNextDefaultThreadName();
			long pointer = virtualMachine.getObjectProvider().getPointer(_this);
			virtualMachine.getThreadProvider().start(tname, method, null, new long[]{pointer}, pointer);
			return null;
		}
		
	}
	
}
