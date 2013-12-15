package xscript.compiler;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrAny extends XClassPtr {

	public final static XClassPtrAny instance = new XClassPtrAny();
	
	private XClassPtrAny(){}
	
	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		return null;
	}

	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		return null;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String toString() {
		return "any";
	}

}
