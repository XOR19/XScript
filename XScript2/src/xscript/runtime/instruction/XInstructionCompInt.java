package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionCompInt extends XInstruction {

	public XInstructionCompInt(){}
	
	public XInstructionCompInt(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int right = methodExecutor.iPop();
		int left = methodExecutor.iPop();
		methodExecutor.iPush(right==left?0:left>right?-1:1);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "compi";
	}

	
}
