package xscript.runtime.genericclass;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.method.XMethod;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrMethodGeneric extends XClassPtr{

	public final String className;
	private final String methodName;
	private XMethod method;
	public final String genericName;
	private int genericID;
	
	public XClassPtrMethodGeneric(String className, String methodName, String genericName){
		this.className = className;
		this.methodName = methodName;
		this.genericName = genericName;
	}
	
	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		return null;
	}
	
	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		if(method==null){
			XClass xClass = virtualMachine.getClassProvider().getXClass(className);
			method = xClass.getMethod(getMethodName());
			genericID = method.getGenericID(genericName);
		}
		if(methodExecutor.getMethod()!=method)
			throw new XRuntimeException("Can't get generic %s of method %s in method %s", genericName, method, methodExecutor.getMethod());
		return methodExecutor.getGeneric(genericID);
	}

	@Override
	public boolean isStatic() {
		return false;
	}

	@Override
	public void save(XOutputStream outputStream) throws IOException {
		outputStream.writeByte('M');
		outputStream.writeUTF(className);
		outputStream.writeUTF(getMethodName());
		outputStream.writeUTF(genericName);
	}

	@Override
	public String toString() {
		return genericName;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof XClassPtrMethodGeneric){
			if(className.equals(((XClassPtrMethodGeneric) other).className)){
				if(getMethodName().equals(((XClassPtrMethodGeneric) other).getMethodName())){
					if(genericName.equals(((XClassPtrMethodGeneric) other).genericName)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getMethodName() {
		return methodName;
	}
	
}