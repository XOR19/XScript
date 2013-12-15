package xscript.compiler;

import java.util.List;

import xscript.runtime.instruction.XInstruction;

public class XInstructionDumyDelete extends XInstructionDumy {

	@Override
	public XInstruction replaceWith(XMethodCompiler compiler, List<XInstruction> instructions) {
		return null;
	}

	@Override
	public void deleteInstruction(XMethodCompiler compiler, List<XInstruction> instructions, XInstruction instruction) {
		
	}

}
