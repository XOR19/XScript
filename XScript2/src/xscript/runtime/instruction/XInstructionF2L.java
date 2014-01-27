package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionF2L extends XInstruction {

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		float value = methodExecutor.fPop();
		methodExecutor.lPush((long) value);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "f2l";
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return 1;
	}
	
}
