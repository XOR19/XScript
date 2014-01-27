package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionThrow extends XInstruction {

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		thread.setException(methodExecutor.oPop());
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "throw";
	}
	
	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}

}
