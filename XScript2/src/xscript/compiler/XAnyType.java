package xscript.compiler;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.genericclass.XClassPtr;


public class XAnyType extends XVarType {

	public static final XVarType type = new XAnyType();
	
	private XAnyType(){}

	@Override
	public XField getField(String name) {
		return null;
	}

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
		return "<any>";
	}

}
