package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionVarJump extends XInstruction {
	
	public XInstructionVarJump(){}
	
	public XInstructionVarJump(XInputStream inputStream) throws IOException{}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int jumpTo = methodExecutor.iPop();
		long thrown = methodExecutor.oPop();
		if(thrown!=0){
			thread.setException(thrown);
		}else{
			methodExecutor.setProgramPointer(jumpTo);
		}
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {}

	@Override
	public String getSource() {
		return "vjump";
	}

}
