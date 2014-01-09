package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionOSwap extends XInstruction {

	public XInstructionOSwap(){}
	
	public XInstructionOSwap(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long value1 = methodExecutor.oPop();
		long value2 = methodExecutor.oPop();
		methodExecutor.oPush(value1);
		methodExecutor.oPush(value2);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "oswap";
	}

}
