package xscript.compiler;

import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;

public class XErroredType extends XVarType {

	@Override
	public XClass[] getXClasses() {
		return null;
	}

	@Override
	public XClassPtr getXClassPtr() {
		return null;
	}

	@Override
	public String toString() {
		return "!error!";
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

}
