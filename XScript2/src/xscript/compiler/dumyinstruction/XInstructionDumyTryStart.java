package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.compiler.XTryHandle;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionStackSizeSave;

public class XInstructionDumyTryStart extends XInstructionDumy {

	public XTryHandle handle;
	
	public int index;
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return new XInstructionStackSizeSave(index);
	}

	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {}

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		index = xCodeGen.getTryHandlerIndex(handle);
	}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return false;
	}

}
