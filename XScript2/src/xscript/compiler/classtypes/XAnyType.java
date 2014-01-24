package xscript.compiler.classtypes;

import java.util.List;

import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;


public class XAnyType extends XVarType {

	public static final XVarType type = new XAnyType();
	
	private XVarType should;
	
	private XAnyType(){}

	public XAnyType(XVarType should){
		this.should = should;
	}
	
	@Override
	public XClass[] getXClasses() {
		return null;
	}

	@Override
	public String toString() {
		return "<any>";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof XVarType;
	}

	@Override
	public boolean canCastTo(XVarType varTypeFor) {
		return true;
	}

	@Override
	protected void getSuperClasses(List<XKnownType> superClasses) {}

	@Override
	protected void getSuperClassesAndThis(List<XKnownType> superClasses) {}

	@Override
	public XClassPtr getXClassPtr() {
		return null;
	}

	@Override
	public XVarType[] getXVarTypes() {
		return null;
	}

	public XVarType getShould(){
		return should;
	}
	
}
