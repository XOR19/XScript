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

	@Override
	public boolean equals(Object other) {
		if(other instanceof XVarType){
			return primitiveID == ((XVarType)other).getPrimitiveID();
		}
		return false;
	}

	@Override
	public boolean canCastTo(XVarType varTypeFor) {
		if(varTypeFor instanceof XVarType){
			int prim = ((XVarType)varTypeFor).getPrimitiveID();
			if(primitiveID==prim){
				return true;
			}
			if(primitiveID>=2 && primitiveID<=8 && prim>=2 && prim<=8){
				if(primitiveID==XPrimitive.CHAR){
					return true;
				}
				int s1 = primitiveID-2;
				int s2 = prim-2;
				if(s1==0){
					s1 = 1;
				}
				if(s2==1){
					s2 = 3;
				}else if(s2==0){
					s2 = 1;
				}
				return s2>s1;
			}
		}
		return false;
	}
	
}
