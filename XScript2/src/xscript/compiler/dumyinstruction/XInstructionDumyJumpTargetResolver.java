package xscript.compiler.dumyinstruction;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionLoadConstInt;


public class XInstructionDumyJumpTargetResolver extends XInstructionDumyJump {

	protected XInstruction makeReplaceInstruction(int target){
		return new XInstructionLoadConstInt(target);
	}

}
