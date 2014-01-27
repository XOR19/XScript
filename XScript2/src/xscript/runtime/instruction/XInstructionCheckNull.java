package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionCheckNull extends XInstruction {
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long value = methodExecutor.oPop();
		methodExecutor.oPush(value);
		XObject obj = vm.getObjectProvider().getObject(value);
		if(obj==null){
			throw new XRuntimeException("nullpointer");
		}
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "cn";
	}

	
}
