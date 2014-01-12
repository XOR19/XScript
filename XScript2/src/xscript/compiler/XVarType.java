package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.method.XMethod;


public abstract class XVarType {
	
	public static XVarType getVarTypeFor(XClass xClass, XVarType[] generics){
		int primitive = XPrimitive.getPrimitiveID(xClass);
		if(primitive==XPrimitive.OBJECT){
			return new XSingleType(xClass, generics);
		}else{
			return new XPrimitiveType(xClass);
		}
	}
	
	public static XVarType getVarTypeFor(XClassPtr classPtr, XVirtualMachine vm, XVarType[] generics, XVarType[] methodGenerics) {
		if(classPtr instanceof XClassPtrClass){
			XClassPtrClass cpc = (XClassPtrClass)classPtr;
			XClass c = cpc.getXClassNonNull(vm);
			int prim = XPrimitive.getPrimitiveID(c);
			if(prim==XPrimitive.OBJECT){
				return new XSingleType(c, new XVarType[0]);
			}else{
				return new XPrimitiveType(c);
			}
		}else if(classPtr instanceof XClassPtrGeneric){
			XClassPtrGeneric cpg = (XClassPtrGeneric)classPtr;
			XClass c = cpg.getXClassNonNull(vm);
			XVarType[] varTypes = new XVarType[c.getGenericParams()];
			for(int i=0; i<varTypes.length; i++){
				varTypes[i] = getVarTypeFor(cpg.genericPtrs[i], vm, generics, methodGenerics);
			}
			return new XSingleType(c, varTypes);
		}else if(classPtr instanceof XClassPtrClassGeneric){
			XClassPtrClassGeneric cpcg = (XClassPtrClassGeneric)classPtr;
			XClass c = vm.getClassProvider().getXClass(cpcg.className);
			int id = c.getGenericID(cpcg.genericName);
			if(generics==null){
				return new XMultibleTypeClassGeneric(c, id);
			}else{
				return generics[id];
			}
		}else if(classPtr instanceof XClassPtrMethodGeneric){
			XClassPtrMethodGeneric cpmg = (XClassPtrMethodGeneric)classPtr;
			XClass c = vm.getClassProvider().getXClass(cpmg.className);
			XMethod m = c.getMethod(cpmg.getMethodDesk());
			int id = m.getGenericID(cpmg.genericName);
			if(methodGenerics==null){
				return new XMultibleTypeMethodGeneric(m, id);
			}else{
				return methodGenerics[id];
			}
		}else if(classPtr instanceof XClassPtrErrored){
			return new XErroredType(((XClassPtrErrored)classPtr).erroredClassName());
		}
		return new XErroredType();
	}

	public abstract XClass[] getXClasses();

	public int getPrimitiveID() {
		return XPrimitive.OBJECT;
	}
	
	@Override
	public abstract String toString();
	
	@Override
	public abstract boolean equals(Object other);

	public boolean canCastTo(XVarType varTypeFor){
		if(equals(varTypeFor)){
			return true;
		}
		if(varTypeFor instanceof XAnyType){
			return true;
		}
		XClass ca[] = varTypeFor.getXClasses();
		if(ca==null){
			return false;
		}
		for(XClass c:ca){
			XVarType vt = getSuperClass(c.getName());
			if(vt==null || !varTypeFor.equals(vt)){
				return false;
			}
		}
		return true;
	}

	public XVarType getSuperClass(String name){
		List<XKnownType> superClasses = getSuperClasses();
		for(XKnownType superClass:superClasses){
			XClass c = superClass.getXClass();
			if(c.getName().equals(name)){
				return superClass;
			}
		}
		return null;
	}
	
	public List<XKnownType> getSuperClasses(){
		List<XKnownType> superClasses = new ArrayList<XKnownType>();
		getSuperClasses(superClasses);
		return superClasses;
	}
	
	public List<XKnownType> getSuperClassesAndThis(){
		List<XKnownType> superClasses = new ArrayList<XKnownType>();
		getSuperClassesAndThis(superClasses);
		return superClasses;
	}
	
	protected abstract void getSuperClasses(List<XKnownType> superClasses);
	
	protected abstract void getSuperClassesAndThis(List<XKnownType> superClasses);

	public abstract XClassPtr getXClassPtr();

	public XClass getXClass() {
		return null;
	}

	public XVarType getGeneric(int i) {
		return null;
	}

	public List<XVarType> getDirectSuperClasses() {
		return null;
	}
	
}
