package xscript.compiler.dumyinstruction;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionIf;

public class XInstructionDumyIf extends XInstructionDumyJump {

	@Override
	protected XInstruction makeReplaceInstruction(int target) {
		return new XInstructionIf(target);
	}
	
}
