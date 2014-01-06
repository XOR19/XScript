package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionToTop extends XInstruction {
	
	private final int pos;
	
	public XInstructionToTop(int pos){
		this.pos = pos;
	}
	
	public XInstructionToTop(XInputStream inputStream) throws IOException{
		pos = inputStream.readUnsignedShort();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int i = methodExecutor.iRead(pos);
		methodExecutor.iPush(i);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(pos);
	}

	@Override
	public String getSource() {
		return "tt "+pos;
	}

}
