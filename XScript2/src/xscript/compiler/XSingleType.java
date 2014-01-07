package xscript.compiler;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.genericclass.XClassPtr;

public class XSingleType extends XVarType {

	public XClassPtr type;
	public XClass c;
	
	public XSingleType(XClassPtr type, XClass c) {
		this.type = type;
		this.c = c;
	}

	@Override
	public XField getField(String name) {
		return c.getField(name);
	}

	public XClass getXClass() {
		return c;
	}

	@Override
	public XClass[] getXClasses() {
		return new XClass[]{c};
	}

	@Override
	public XClassPtr getXClassPtr() {
		return type;
	}

	@Override
	public String toString() {
		return type.toString();
	}

}
