package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionOToTop extends XInstruction {
	
	private final int pos;
	
	public XInstructionOToTop(int pos){
		this.pos = pos;
	}
	
	public XInstructionOToTop(XInputStream inputStream) throws IOException{
		pos = inputStream.readUnsignedShort();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long o = methodExecutor.oRead(pos);
		methodExecutor.oPush(o);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(pos);
	}

	@Override
	public String getSource() {
		return "ott "+pos;
	}

}
