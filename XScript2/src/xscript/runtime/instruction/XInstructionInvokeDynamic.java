package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XChecks;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.genericclass.XGenericMethodProviderImp;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionInvokeDynamic extends XInstruction {

	private String className;
	private String methodName;
	private XClassPtr[] methodParams;
	private XClassPtr methodReturn;
	private XClassPtr[] generics;
	private XMethod method;
	
	public XInstructionInvokeDynamic(XMethod method, XClassPtr[] generics){
		className = method.getDeclaringClass().getName();
		methodName = method.getRealName();
		methodParams = method.getParams();
		methodReturn = method.getReturnTypePtr();
		this.generics = generics;
		this.method = method;
		if(generics.length==0){
			if(method.getGenericParams()!=0)
				throw new XRuntimeException("Can't create a generic method %s without generic params, need %s generic params", method, method.getGenericParams());
		}else if(generics.length!=method.getGenericParams()){
			throw new XRuntimeException("Can't create a generic method %s with %s generic params, need %s generic params", method, generics.length, method.getGenericParams());
		}
		if(XModifier.isStatic(method.getModifier())){
			throw new XRuntimeException("Method %s is static", method);
		}
	}
	
	public XInstructionInvokeDynamic(XInputStream inputStream) throws IOException{
		className = inputStream.readUTF();
		methodName = inputStream.readUTF();
		methodParams = new XClassPtr[inputStream.readUnsignedByte()];
		for(int i=0; i<methodParams.length; i++){
			methodParams[i] = XClassPtr.load(inputStream);
		}
		methodReturn = XClassPtr.load(inputStream);
		generics = new XClassPtr[inputStream.readUnsignedByte()];
		for(int i=0; i<generics.length; i++){
			generics[i] = XClassPtr.load(inputStream);
		}
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		final XGenericClass[] solvedGenerics = new XGenericClass[generics.length];
		for(int i=0; i<solvedGenerics.length; i++){
			solvedGenerics[i] = generics[i].getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor);
		}
		long[] params = new long[method.getParamCount()+1];
		for(int i=params.length-2; i>=0; i--){
			int pID = XPrimitive.getPrimitiveID(method.getParams()[i].getXClass(vm));
			params[i+1] = methodExecutor.pop(pID);
		}
		params[0] = methodExecutor.oPop();
		XObject _this = vm.getObjectProvider().getObject(params[0]);
		XGenericClass[] paramTypes = method.getParams(_this.getXClass(), new XGenericMethodProviderImp(method, solvedGenerics));
		for(int i=paramTypes.length-1; i>=0; i--){
			int pID = XPrimitive.getPrimitiveID(paramTypes[i].getXClass());
			if(pID==XPrimitive.OBJECT){
				XObject obj = vm.getObjectProvider().getObject(params[i+1]);
				if(obj!=null)
					XChecks.checkCast(obj.getXClass(), paramTypes[i]);
			}
		}
		XMethod xMethod = method.getMethod(_this);
		thread.call(xMethod, solvedGenerics, params);
	}

	@Override
	public void resolve(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		if(method==null){
			XClass xClass = vm.getClassProvider().getXClass(className);
			method = xClass.getMethod(methodName+makeDesk());
			XChecks.checkAccess(methodExecutor.getMethod().getDeclaringClass(), method);
			if(generics==null){
				if(method.getGenericParams()!=0)
					throw new XRuntimeException("Can't create a generic method %s without generic params, need %s generic params", method, method.getGenericParams());
			}else if(generics.length!=method.getGenericParams()){
				throw new XRuntimeException("Can't create a generic method %s with %s generic params, need %s generic params", method, generics.length, method.getGenericParams());
			}
			if(XModifier.isStatic(method.getModifier())){
				throw new XRuntimeException("Method %s is static", method);
			}
		}
	}
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeUTF(className);
		outputStream.writeUTF(methodName);
		outputStream.writeByte(methodParams.length);
		for(int i=0; i<methodParams.length; i++){
			methodParams[i].save(outputStream);
		}
		methodReturn.save(outputStream);
		outputStream.writeByte(generics.length);
		for(int i=0; i<generics.length; i++){
			generics[i].save(outputStream);
		}
	}

	@Override
	public String getSource() {
		return "invd "+className+"."+methodName+makeDesk();
	}

	private String makeDesk(){
		String s = "";
		if(generics.length>0){
			s+="<"+generics[0];
			for(int i=1; i<generics.length; i++){
				s += ", "+generics[i];
			}
			s+=">";
		}
		s += "(";
		if(methodParams.length>0){
			s += methodParams[0];
			for(int i=1; i<methodParams.length; i++){
				s += ", "+methodParams[i];
			}
		}
		s += ")"+methodReturn;
		return s;
	}
	
	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		XClassPtr params[] = method.getParams();
		int change = 0;
		for(int i=0; i<params.length; i++){
			if(XPrimitive.getPrimitiveID(params[i].getXClass(vm))!=XPrimitive.OBJECT)
				change++;
		}
		if(method.getReturnTypePrimitive()!=XPrimitive.OBJECT && method.getReturnTypePrimitive()!=XPrimitive.VOID)
			change--;
		return -change;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		XClassPtr params[] = method.getParams();
		int change = 0;
		for(int i=0; i<params.length; i++){
			if(XPrimitive.getPrimitiveID(params[i].getXClass(vm))==XPrimitive.OBJECT)
				change++;
		}
		if(method.getReturnTypePrimitive()==XPrimitive.OBJECT)
			change--;
		return -change-1;
	}
	
}
