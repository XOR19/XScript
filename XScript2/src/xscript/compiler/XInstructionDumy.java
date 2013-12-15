package xscript.compiler;

import java.io.IOException;
import java.util.List;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public abstract class XInstructionDumy extends XInstruction {

	public abstract XInstruction replaceWith(XMethodCompiler compiler, List<XInstruction> instructions);
	
	public abstract void deleteInstruction(XMethodCompiler compiler, List<XInstruction> instructions, XInstruction instruction);
	
	@Override
	public final void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		shouldNeverCalled();
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		shouldNeverCalled();
	}

	@Override
	public String getSource() {
		shouldNeverCalled();
		return null;
	}

	private void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
}
