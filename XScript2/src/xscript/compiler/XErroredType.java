package xscript.compiler;

import java.util.List;

import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;

public class XErroredType extends XVarType {

	private String className;
	
	public XErroredType(String className){
		this.className = className;
	}
	
	public XErroredType() {
		className = "!error!";
	}

	@Override
	public XClass[] getXClasses() {
		return null;
	}

	@Override
	public String toString() {
		return "!error!";
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}

	@Override
	protected void getSuperClasses(List<XKnownType> superClasses) {}

	@Override
	protected void getSuperClassesAndThis(List<XKnownType> superClasses) {}

	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrErrored(className);
	}

	@Override
	public XVarType[] getXVarTypes() {
		return null;
	}

}
