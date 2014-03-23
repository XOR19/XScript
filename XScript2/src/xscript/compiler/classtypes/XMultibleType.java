package xscript.compiler.classtypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;


public class XMultibleType extends XVarType {

	public XVarType[] classes;

	public XMultibleType(XVarType[] classes) {
		this.classes = classes;
	}

	@Override
	public XClass[] getXClasses() {
		XClass[] c = new XClass[classes.length];
		for(int i=0; i<c.length; i++){
			c[i] = classes[i].getXClass();
		}
		return c;
	}

	@Override
	public String toString() {
		return Arrays.toString(classes);
	}

	@Override
	protected void getSuperClasses(List<XKnownType> superClasses) {
		for(XVarType c:classes){
			c.getSuperClasses(superClasses);
		}
	}

	@Override
	protected void getSuperClassesAndThis(List<XKnownType> superClasses) {
		for(XVarType c:classes){
			c.getSuperClassesAndThis(superClasses);
		}
	}

	@Override
	public XClassPtr getXClassPtr() {
		return null;
	}

	@Override
	public XVarType[] getXVarTypes() {
		return classes;
	}

	@Override
	public boolean equals(Object other) {
		return false;
	}
	
	@Override
	public List<XKnownType> getKnownTypes(){
		List<XKnownType> list = new ArrayList<XKnownType>();
		for(XVarType classs:classes){
			List<XKnownType> l = classs.getKnownTypes();
			if(l!=null){
				list.addAll(l);
			}
		}
		return list;
	}
	
}
