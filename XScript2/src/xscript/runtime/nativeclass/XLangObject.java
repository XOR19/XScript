package xscript.runtime.nativeclass;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeMethod;
import xscript.runtime.nativemethod.XNativeProvider;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XLangObject {

	public static void registerNatives(XNativeProvider nativeProvider) {
		nativeProvider.addNativeMethod("xscript.lang.Object", "getClass()xscript.lang.Class", new XMethodGetClass());
		nativeProvider.addNativeMethod("xscript.lang.Object", "hashCode()long", new XMethodHashCode());
		nativeProvider.addNativeMethod("xscript.lang.Object", "notify()void", new XMethodNotify());
		nativeProvider.addNativeMethod("xscript.lang.Object", "notifyAll()void", new XMethodNotifyAll());
		nativeProvider.addNativeMethod("xscript.lang.Object", "wait()void", new XMethodWait());
	}
	
	private static class XMethodGetClass implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return virtualMachine.getObjectProvider().getObject(_this.getXClass().getXClass().getClassObject(thread, methodExecutor));
		}
		
	}
	
	private static class XMethodHashCode implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			return virtualMachine.getObjectProvider().getPointer(_this);
		}
		
	}
	
	private static class XMethodNotify implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			_this.monitorNotify(thread);
			return null;
		}
		
	}
	
	private static class XMethodNotifyAll implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			_this.monitorNotifyAll(thread);
			return null;
		}
		
	}
	
	private static class XMethodWait implements XNativeMethod{

		@Override
		public Object invoke(XVirtualMachine virtualMachine, XThread thread,
				XMethodExecutor methodExecutor, XGenericClass[] generics,
				String name, XObject _this, Object[] params) {
			_this.monitorWait(thread);
			return null;
		}
		
	}
	
}
