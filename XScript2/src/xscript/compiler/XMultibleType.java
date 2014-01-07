package xscript.compiler;

import java.util.Arrays;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.genericclass.XClassPtr;


public class XMultibleType extends XVarType {

	public XClassPtr type;
	public XSingleType[] classes;

	public XMultibleType(XClassPtr type, XSingleType[] classes) {
		this.type = type;
		this.classes = classes;
	}

	@Override
	public XField getField(String name) {
		for(int i=0; i<classes.length; i++){
			XField field = classes[i].getField(name);
			if(field!=null)
				return field;
		}
		return null;
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
	public XClassPtr getXClassPtr() {
		return type;
	}

	@Override
	public String toString() {
		if(type==null){
			return Arrays.toString(classes);
		}else{
			return type.toString();
		}
	}

	
	
}
