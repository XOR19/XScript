package xscript.compiler;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;

public class XPrimitiveType extends XVarType {

	public int primitiveID;
	public XClass c;
	
	public XPrimitiveType(int primitiveID, XClass c) {
		this.primitiveID = primitiveID;
		this.c = c;
	}

	@Override
	public XClass[] getXClasses() {
		return new XClass[]{c};
	}

	@Override
	public int getPrimitiveID() {
		return primitiveID;
	}

	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrClass(XPrimitive.getName(primitiveID));
	}

	@Override
	public String toString() {
		return XPrimitive.getName(primitiveID);
	}
	
}
