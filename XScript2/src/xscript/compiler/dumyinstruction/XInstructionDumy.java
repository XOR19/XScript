package xscript.compiler.dumyinstruction;

import java.io.IOException;
import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public abstract class XInstructionDumy extends XInstruction {

	public abstract XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions);
	
	public abstract void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction);
	
	public abstract void resolve(XCodeGen xCodeGen, List<XInstruction> instructions);
	
	public abstract boolean pointingTo(XInstruction inst);
	
	public abstract void replace(XCodeGen compiler, XInstruction instruction, XInstruction with, List<XInstruction> instructions);
	
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
		return getClass()+"@"+Integer.toHexString(hashCode());
	}

	private void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
}
