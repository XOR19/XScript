package xscript.compiler.classtypes;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;


public abstract class XMultibleTypeGeneric extends XMultibleType {

	protected XClass xClass;
	
	public XMultibleTypeGeneric(XVarType[] classes, XClass xClass) {
		super(classes);
		this.xClass = xClass;
	}
	
	public boolean canCastTo(XVarType varTypeFor){
		if(varTypeFor instanceof XAnyType){
			return true;
		}
		return varTypeFor.getPrimitiveID()==XPrimitive.OBJECT;
	}

	@Override
	public XClass getXClass() {
		return xClass;
	}
	
	
	
}
