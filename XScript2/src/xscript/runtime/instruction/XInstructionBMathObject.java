package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public abstract class XInstructionBMathObject extends XInstruction {

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long right = methodExecutor.oPop();
		long left = methodExecutor.oPop();
		methodExecutor.zPush(calc(left, right));
	}

	public abstract boolean calc(long left, long right);
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return name()+"o";
	}

	public abstract String name();
	
	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return 1;
	}
	
	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -2;
	}
	
}
