package xscript.compiler.classtypes;

import java.io.IOException;
import java.util.List;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrErrored extends XClassPtr {

	private String name;
	
	private XGenericClass c;
	
	public XClassPtrErrored(String name){
		this.name = name;
	}
	
	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		if(c==null){
			XClass xc = virtualMachine.getClassProvider().getXClass("xscript.lang.Object");
			c = new XGenericClass(xc);
		}
		return c.getXClass();
	}

	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		if(c==null){
			getXClass(virtualMachine);
		}
		return c;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void save(XOutputStream outputStream, List<XClassPtr> done) throws IOException {
		throw new AssertionError();
	}

	@Override
	public String toString() {
		return "errored:"+name;
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

	public String erroredClassName(){
		return name;
	}
	
}
