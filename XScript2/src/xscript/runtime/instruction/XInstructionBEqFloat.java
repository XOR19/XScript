package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionBEqFloat extends XInstruction {

	public XInstructionBEqFloat(){}
	
	public XInstructionBEqFloat(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		float right = methodExecutor.fPop();
		float left = methodExecutor.fPop();
		methodExecutor.zPush(left>=right);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "beqf";
	}

	
}
