package xscript.runtime.nativemethod;

import java.lang.reflect.Field;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XNativeFieldImp implements XNativeField {
	
	private Field field;
	
	public XNativeFieldImp(Field field) {
		this.field = field;
	}

	@Override
	public void set(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this, Object value) {
		try {
			field.set(_this.getNativeObject(), value);
		} catch (IllegalArgumentException e) {
			throw new XRuntimeException(e, "Error while set Native");
		} catch (IllegalAccessException e) {
			throw new XRuntimeException(e, "Error while set Native");
		}
	}

	@Override
	public Object get(XVirtualMachine virtualMachine, XThread thread, XMethodExecutor methodExecutor, String name, XObject _this) {
		try {
			return field.get(_this.getNativeObject());
		} catch (IllegalArgumentException e) {
			throw new XRuntimeException(e, "Error while get Native");
		} catch (IllegalAccessException e) {
			throw new XRuntimeException(e, "Error while get Native");
		}
	}

}
