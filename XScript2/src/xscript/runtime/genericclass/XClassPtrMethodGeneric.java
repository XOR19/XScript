package xscript.runtime.genericclass;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.method.XMethod;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrMethodGeneric extends XClassPtr{

	private String className;
	private String methodName;
	private String[] paramNames;
	private String retName;
	private XMethod method;
	private String genericName;
	private int genericID;
	
	public XClassPtrMethodGeneric(String className, String methodName, String[] paramNames, String retName, String genericName){
		this.className = className;
		this.methodName = methodName;
		this.paramNames = paramNames;
		this.retName = retName;
		this.genericName = genericName;
	}
	
	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		return null;
	}
	
	@Override
	public XClassPtr[] getPossibleClasses(XVirtualMachine virtualMachine){
		if(method==null){
			XClass xClass = virtualMachine.getClassProvider().getXClass(className);
			method = xClass.getMethod(methodName, paramNames, retName);
			genericID = method.getGenericID(genericName);
		}
		XGenericInfo info = method.getGenericInfo(genericID);
		if(info.isSuper()){
			return new XClassPtr[]{new XClassPtrClass("xstript.lang.Object")};
		}
		return info.getTypeParams();
	}
	
	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		if(method==null){
			XClass xClass = virtualMachine.getClassProvider().getXClass(className);
			method = xClass.getMethod(methodName, paramNames, retName);
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
		outputStream.writeUTF(methodName);
		outputStream.writeByte(paramNames.length);
		for(int i=0; i<paramNames.length; i++){
			outputStream.writeUTF(paramNames[i]);
		}
		outputStream.writeUTF(retName);
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
				if(methodName.equals(((XClassPtrMethodGeneric) other).methodName)){
					for(int i=0; i<paramNames.length; i++){
						if(!paramNames[i].equals(((XClassPtrMethodGeneric) other).paramNames[i])){
							return false;
						}
					}
					if(retName.equals(((XClassPtrMethodGeneric) other).retName)){
						if(genericName.equals(((XClassPtrMethodGeneric) other).genericName)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}