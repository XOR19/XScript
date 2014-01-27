package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public abstract class XInstructionMathInt extends XInstruction {

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int right = methodExecutor.iPop();
		int left = methodExecutor.iPop();
		methodExecutor.iPush(calc(left, right));
	}

	public abstract int calc(int left, int right);
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return name()+"i";
	}

	public abstract String name();

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
