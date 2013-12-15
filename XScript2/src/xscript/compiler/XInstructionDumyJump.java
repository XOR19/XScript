package xscript.compiler;

import java.util.List;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionJump;

public class XInstructionDumyJump extends XInstructionDumy {

	public XInstruction target;

	@Override
	public XInstruction replaceWith(XMethodCompiler compiler, List<XInstruction> instructions) {
		int t;
		if(target==null){
			t = instructions.size();
		}else{
			t = instructions.indexOf(target);
		}
		if(t==-1)
			throw new AssertionError();
		return makeReplaceInstruction(t);
	}

	protected XInstruction makeReplaceInstruction(int target){
		return new XInstructionJump(target);
	}
	
	@Override
	public void deleteInstruction(XMethodCompiler compiler, List<XInstruction> instructions, XInstruction instruction) {
		if(instruction==target){
			int pos = instructions.indexOf(instruction);
			if(pos==-1)
				throw new AssertionError();
			pos++;
			if(pos<instructions.size()){
				target = instructions.get(pos);
			}else{
				target = null;
			}
		}
	}

}
