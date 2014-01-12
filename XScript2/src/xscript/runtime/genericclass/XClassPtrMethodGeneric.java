package xscript.runtime.genericclass;

import java.io.IOException;
import java.util.List;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.method.XMethod;
import xscript.runtime.threads.XGenericMethodProvider;

public class XClassPtrMethodGeneric extends XClassPtr{

	public String className;
	public String methodName;
	public XClassPtr[] params;
	public XClassPtr returnType;
	private XMethod method;
	public String genericName;
	private int genericID;
	
	public XClassPtrMethodGeneric(String className, String methodName, XClassPtr[] params, XClassPtr returnType, String genericName){
		this.className = className;
		this.methodName = methodName;
		this.params = params;
		this.returnType = returnType;
		this.genericName = genericName;
	}
	
	public XClassPtrMethodGeneric() {}

	@Override
	public XClass getXClass(XVirtualMachine virtualMachine) {
		return null;
	}
	
	@Override
	public XGenericClass getXClass(XVirtualMachine virtualMachine, XGenericClass genericClass, XGenericMethodProvider methodExecutor) {
		if(method==null){
			XClass xClass = virtualMachine.getClassProvider().getXClass(className);
			method = xClass.getMethod(getMethodDesk());
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
	public void save(XOutputStream outputStream, List<XClassPtr> done) throws IOException {
		int id = done.indexOf(this);
		if(id==-1){
			done.add(this);
			outputStream.writeByte('M');
			outputStream.writeUTF(className);
			outputStream.writeUTF(getMethodName());
			XClassPtr[] params = getMethodParams();
			outputStream.writeByte(params.length);
			for(int i=0; i<params.length; i++){
				params[i].save(outputStream, done);
			}
			getMethodReturnType().save(outputStream, done);
			outputStream.writeUTF(genericName);
		}else{
			outputStream.writeByte('D');
			outputStream.writeShort(id);
		}
	}

	@Override
	public String toString() {
		return genericName;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof XClassPtrMethodGeneric){
			if(className.equals(((XClassPtrMethodGeneric) other).className)){
				if(getMethodDesk().equals(((XClassPtrMethodGeneric) other).getMethodDesk())){
					if(genericName.equals(((XClassPtrMethodGeneric) other).genericName)){
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getMethodDesk(){
		String desk = getMethodName()+"(";
		XClassPtr[] params = getMethodParams();
		if(params.length>0){
			desk += params[0];
			for(int i=1; i<params.length; i++){
				desk += ", "+params[i];
			}
		}
		desk += ")"+getMethodReturnType();
		return desk;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public XClassPtr[] getMethodParams() {
		return params;
	}
	
	public XClassPtr getMethodReturnType() {
		return returnType;
	}
	
}