package xscript.compiler;

import java.util.List;

import xscript.runtime.instruction.XInstruction;

public class XInstructionDumyDelete extends XInstructionDumy {

	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return null;
	}

	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {
		
	}

}
