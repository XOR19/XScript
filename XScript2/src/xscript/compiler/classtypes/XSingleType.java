package xscript.compiler.classtypes;

import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XRuntimeException;
import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrGeneric;

public class XSingleType extends XKnownType {

	public XVarType generics[];
	
	public XSingleType(XClass xClass, XVarType generics[]) {
		super(xClass);
		this.generics = generics;
		if(xClass.getGenericParams()!=generics.length && xClass.getGenericParams()!=-1){
			throw new XRuntimeException("Can't create a generic class of %s with %s generic params, need %s generic params", xClass, generics.length, xClass.getGenericParams());
		}
	}

	@Override
	public String toString() {
		String out = xClass.getName();
		if(generics.length>0){
			out += "<"+generics[0];
			for(int i=1; i<generics.length; i++){
				out += ", "+generics[i];
			}
			out += ">";
		}
		return out;
	}

	@Override
	public boolean equals(Object other) {
		if(other instanceof XSingleType){
			if(xClass == ((XSingleType) other).xClass){
				for(int i=0; i<generics.length; i++){
					if(!generics[i].equals(((XSingleType) other).generics[i])){
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected void getSuperClasses(List<XKnownType> superClasses) {
		XClassPtr[] cps = xClass.getSuperClasses();
		for(XClassPtr cp:cps){
			XVarType vt = XVarType.getVarTypeFor(cp, xClass.getVirtualMachine(), generics, null);
			vt.getSuperClassesAndThis(superClasses);
		}
	}

	@Override
	public XClassPtr getXClassPtr() {
		if(generics.length==0){
			return new XClassPtrClass(xClass.getName());
		}
		XClassPtr[] g = new XClassPtr[generics.length];
		for(int i=0; i<g.length; i++){
			g[i] = generics[i].getXClassPtr();
		}
		return new XClassPtrGeneric(xClass.getName(), g);
	}
	
	@Override
	public XVarType getGeneric(int i) {
		return generics[i];
	}
	
	@Override
	public List<XVarType> getDirectSuperClasses() {
		XClassPtr[] cps = xClass.getSuperClasses();
		List<XVarType> varType = new ArrayList<XVarType>();
		for(int i=0; i<cps.length; i++){
			varType.add(XVarType.getVarTypeFor(cps[i], xClass.getVirtualMachine(), generics, null));
		}
		return varType;
	}

	@Override
	public XVarType[] getXVarTypes() {
		return new XVarType[]{this};
	}
	
}
