package xscript.runtime.genericclass;

import java.io.IOException;
import java.util.List;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrClassByID extends XClassPtr {

	private int id;
	
	public XClassPtrClassByID(int id){
		this.id = id;
	}
	
	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		return null;
	}

	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		return genericClass.getGeneric(id);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public void save(XOutputStream outputStream, List<XClassPtr> done) throws IOException {
		throw new RuntimeException();
	}

	@Override
	public String toString() {
		return "ID:"+id;
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

}
