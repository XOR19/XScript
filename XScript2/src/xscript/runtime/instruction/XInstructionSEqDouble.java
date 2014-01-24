package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionSEqDouble extends XInstruction {

	public XInstructionSEqDouble(){}
	
	public XInstructionSEqDouble(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		double right = methodExecutor.dPop();
		double left = methodExecutor.dPop();
		methodExecutor.zPush(left<=right);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "seqd";
	}

}
