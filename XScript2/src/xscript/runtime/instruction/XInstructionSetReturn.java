package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionSetReturn extends XInstruction {

	public XInstructionSetReturn(){}
	
	public XInstructionSetReturn(XInputStream inputStream){}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int primitive = methodExecutor.getMethod().getReturnTypePrimitive();
		if(primitive != XPrimitive.VOID)
			methodExecutor.setReturn(methodExecutor.pop(methodExecutor.getMethod().getReturnTypePrimitive()));
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "sret";
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		int prim = mi.getMethodReturnPrimitveID();
		return prim==XPrimitive.OBJECT?0:prim==XPrimitive.VOID?0:-1;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return mi.getMethodReturnPrimitveID()==XPrimitive.OBJECT?-1:0;
	}
	
}
