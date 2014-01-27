package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public abstract class XInstructionBMathDouble extends XInstruction {

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		double right = methodExecutor.dPop();
		double left = methodExecutor.dPop();
		methodExecutor.zPush(calc(left, right));
	}

	public abstract boolean calc(double left, double right);
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return name()+"d";
	}

	public abstract String name();
	
	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -3;
	}
	
}
