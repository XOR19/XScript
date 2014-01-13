package xscript.compiler;

import xscript.runtime.clazz.XPrimitive;


public class XMultibleTypeGeneric extends XMultibleType {

	public XMultibleTypeGeneric(XVarType[] classes) {
		super(classes);
	}
	
	public boolean canCastTo(XVarType varTypeFor){
		if(varTypeFor instanceof XAnyType){
			return true;
		}
		return varTypeFor.getPrimitiveID()==XPrimitive.OBJECT;
	}
	
}
