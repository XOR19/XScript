package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionJump extends XInstruction {

	public final int target;
	
	public XInstructionJump(int target){
		this.target = target;
	}
	
	public XInstructionJump(XInputStream inputStream) throws IOException{
		target = inputStream.readInt();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		methodExecutor.setProgramPointer(target);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeInt(target);
	}

	@Override
	public String getSource() {
		return "jump "+target;
	}

}
