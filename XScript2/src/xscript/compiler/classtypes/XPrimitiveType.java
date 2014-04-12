package xscript.compiler.classtypes;

import java.util.List;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;

public class XPrimitiveType extends XKnownType {
	
	public XPrimitiveType(XClass xClass) {
		super(xClass);
	}

	@Override
	public String toString() {
		return xClass.getName();
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof XPrimitiveType){
			return getPrimitiveID() == ((XPrimitiveType)other).getPrimitiveID();
		}
		return false;
	}

	@Override
	public boolean canCastTo(XVarType varTypeFor) {
		if(varTypeFor instanceof XAnyType){
			return getPrimitiveID()!=XPrimitive.VOID;
		}
		int prim = varTypeFor.getPrimitiveID();
		if(prim==XPrimitive.OBJECT){
			XClass xClass = varTypeFor.getXClass();
			if(xClass==null){
				return false;
			}
			String cName = xClass.getName();
			if(cName.equals("xscript.lang.Object")){
				return getPrimitiveID()!=XPrimitive.VOID;
			}
			if(cName.startsWith("xscript.lang.")){
				for(int i=1; i<9; i++){
					if(cName.equals("xscript.lang."+XPrimitive.getWrapper(i))){
						prim = i;
						break;
					}
				}
			}
		}
		int primitiveID = getPrimitiveID();
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
		return false;
	}

	@Override
	protected void getSuperClasses(List<XKnownType> superClasses) {}

	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrClass(xClass.getName());
	}

	@Override
	public XVarType[] getXVarTypes() {
		return new XVarType[]{this};
	}
	
}
