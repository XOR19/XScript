package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionStackSizeSave extends XInstruction {

	private final int index;
	
	public XInstructionStackSizeSave(int index){
		this.index = index;
	}
	
	public XInstructionStackSizeSave(XInputStream inputStream) throws IOException{
		index = inputStream.readUnsignedShort();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		methodExecutor.saveStackSize(index);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(index);
	}

	@Override
	public String getSource() {
		return "sss "+index;
	}

}