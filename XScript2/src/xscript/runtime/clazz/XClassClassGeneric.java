package xscript.runtime.clazz;

import xscript.runtime.XVirtualMachine;

public class XClassClassGeneric extends XClass {

	public XClassClassGeneric(XVirtualMachine virtualMachine, String name) {
		super(virtualMachine, name, new XPackage(""));
	}

}
