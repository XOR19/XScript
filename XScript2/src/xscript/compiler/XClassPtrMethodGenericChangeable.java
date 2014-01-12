package xscript.compiler;

import xscript.runtime.genericclass.XClassPtrMethodGeneric;

public class XClassPtrMethodGenericChangeable extends XClassPtrMethodGeneric {

	public String methodName;
	
	public XClassPtrMethodGenericChangeable(String className, String methodName, String genericName){
		super(className, methodName, genericName);
		this.methodName = methodName;
	}

	@Override
	public String getMethodName() {
		return methodName;
	}

}
