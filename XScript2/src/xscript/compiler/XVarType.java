package xscript.compiler;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;


public abstract class XVarType {
	
	public static XVarType getVarTypeFor(XClassPtr classPtr, XVirtualMachine vm){
		if(classPtr instanceof XClassPtrErrored){
			return new XErroredType();
		}
		XClass c = classPtr.getXClass(vm);
		if(c==null){
			XClassPtr ca[] = classPtr.getPossibleClasses(vm);
			XSingleType[] type = new XSingleType[ca.length];
			for(int i=0; i<ca.length; i++){
				XVarType varType = getVarTypeFor(ca[i], vm);
				type[i] = (XSingleType)varType;
			}
			return new XMultibleType(classPtr, type);
		}else{
			int primitiveID = XPrimitive.getPrimitiveID(c);
			if(primitiveID==XPrimitive.OBJECT){
				return new XSingleType(classPtr, c);
			}else{
				return new XPrimitiveType(primitiveID, c);
			}
		}
	}

	public abstract XClass[] getXClasses();

	public int getPrimitiveID() {
		return XPrimitive.OBJECT;
	}

	public abstract XClassPtr getXClassPtr();
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object other);

	public abstract boolean canCastTo(XVarType varTypeFor);
	
}
