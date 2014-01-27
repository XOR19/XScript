package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionIsNNull extends XInstruction {
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long value = methodExecutor.oPop();
		XObject obj = vm.getObjectProvider().getObject(value);
		methodExecutor.zPush(obj!=null);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "isnn";
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return 1;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
