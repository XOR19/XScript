package xscript.compiler.dumyinstruction;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionNIf;

public class XInstructionDumyNIf extends XInstructionDumyJump {

	@Override
	protected XInstruction makeReplaceInstruction(int target) {
		return new XInstructionNIf(target);
	}
	
}
