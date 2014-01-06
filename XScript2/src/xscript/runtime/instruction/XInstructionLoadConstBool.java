package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionLoadConstBool extends XInstruction {

	private boolean value;
	
	public XInstructionLoadConstBool(boolean value){
		this.value = value;
	}
	
	public XInstructionLoadConstBool(XInputStream inputStream) throws IOException{
		value = inputStream.readBoolean();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		methodExecutor.zPush(value);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeBoolean(value);
	}

	@Override
	public String getSource() {
		return "lcz "+value;
	}

}
