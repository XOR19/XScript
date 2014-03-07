package xscript.runtime.clazz;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;

public class XClassClassGeneric extends XClass {

	public XClassClassGeneric(XVirtualMachine virtualMachine, String name) {
		super(virtualMachine, name, new XPackage(""));
		genericInfos = new XGenericInfo[0];
	}
	
	@Override
	public XClassPtr[] getSuperClasses(){
		return new XClassPtr[]{new XClassPtrClass("xscript.lang.Object")};
	}
	
}
