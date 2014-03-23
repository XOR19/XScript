package xscript.compiler.classtypes;

import java.util.ArrayList;
import java.util.List;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;

public abstract class XKnownType extends XVarType {

	protected XClass xClass;
	
	protected XKnownType(XClass xClass){
		this.xClass = xClass;
	}
	
	public XClass getXClass(){
		return xClass;
	}
	
	@Override
	public XClass[] getXClasses() {
		return new XClass[]{xClass};
	}

	@Override
	public int getPrimitiveID() {
		return XPrimitive.getPrimitiveID(xClass);
	}
	
	@Override
	protected void getSuperClassesAndThis(List<XKnownType> superClasses) {
		superClasses.add(this);
		getSuperClasses(superClasses);
	}
	
	@Override
	public List<XKnownType> getKnownTypes(){
		List<XKnownType> list = new ArrayList<XKnownType>();
		list.add(this);
		return list;
	}
	
}
