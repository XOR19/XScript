package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XChecks;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionGetLocalField extends XInstruction {

	private final int local;
	private String className;
	private String fieldName;
	private XClassPtr fieldType;
	private XField field;
	
	public XInstructionGetLocalField(int local, XField field){
		this.local = local;
		className = field.getDeclaringClass().getName();
		fieldName = field.getSimpleName();
		fieldType = field.getType();
		this.field = field;
	}
	
	public XInstructionGetLocalField(XInputStream inputStream) throws IOException{
		local = inputStream.readUnsignedShort();
		className = inputStream.readUTF();
		fieldName = inputStream.readUTF();
		fieldType = XClassPtr.load(inputStream);
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long pointer = methodExecutor.getLocal(local);
		XObject object = vm.getObjectProvider().getObject(pointer);
		long value = field.get(object);
		methodExecutor.push(value, getPrimitiveID(vm));
	}

	private int getPrimitiveID(XVirtualMachine vm){
		XClass xClass = fieldType.getXClass(vm);
		if(xClass==null)
			return XPrimitive.OBJECT;
		return XPrimitive.getPrimitiveID(xClass);
	}
	
	@Override
	public void resolve(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		if(field==null){
			XClass xClass = vm.getClassProvider().getXClass(className);
			field = xClass.getField(fieldName);
			XChecks.checkAccess(methodExecutor.getMethod().getDeclaringClass(), field);
			if(!field.getType().equals(fieldType)){
				throw new XRuntimeException("Type of field %s has changed", field);
			}
			if(XModifier.isStatic(field.getModifier())){
				throw new XRuntimeException("Field %s is static", field);
			}
		}
	}
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(local);
		outputStream.writeUTF(className);
		outputStream.writeUTF(fieldName);
		fieldType.save(outputStream);
	}

	@Override
	public String getSource() {
		return "rlf "+local+" "+className+"."+fieldName+":"+fieldType;
	}
	
	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return getPrimitiveID(vm)==XPrimitive.OBJECT?0:1;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return getPrimitiveID(vm)==XPrimitive.OBJECT?1:0;
	}

}
