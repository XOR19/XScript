package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionPowInt extends XInstruction {

	public XInstructionPowInt(){}
	
	public XInstructionPowInt(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int right = methodExecutor.iPop();
		int left = methodExecutor.iPop();
		methodExecutor.iPush((int)Math.pow(left, right));
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "powi";
	}

}
