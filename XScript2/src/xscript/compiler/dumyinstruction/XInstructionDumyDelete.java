package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.runtime.instruction.XInstruction;

public class XInstructionDumyDelete extends XInstructionDumy {

	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return null;
	}

	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {
		
	}

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		
	}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return false;
	}

}
