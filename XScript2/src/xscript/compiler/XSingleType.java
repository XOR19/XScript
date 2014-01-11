package xscript.compiler;

import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;

public class XSingleType extends XVarType {

	public XClassPtr type;
	public XClass c;
	
	public XSingleType(XClassPtr type, XClass c) {
		this.type = type;
		this.c = c;
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

	@Override
	public boolean equals(Object other) {
		if(other instanceof XSingleType){
			return type.equals(((XSingleType) other).type);
		}
		return false;
	}

	@Override
	public boolean canCastTo(XVarType varTypeFor) {
		if(equals(varTypeFor)){
			return true;
		}
		if(varTypeFor instanceof XAnyType){
			return true;
		}else if(varTypeFor instanceof XSingleType){
			XClassPtr[] classPtr = c.getSuperClasses();
			for(XClassPtr cp:classPtr){
				if(XVarType.getVarTypeFor(cp, c.getVirtualMachine()).canCastTo(varTypeFor)){
					return true;
				}
			}
		}
		return false;
	}

}
