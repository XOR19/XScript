package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionJump;

public class XInstructionDumyJump extends XInstructionDumy {

	public XInstruction target;

	public int targetID;
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		if(targetID==-1)
			throw new AssertionError();
		return makeReplaceInstruction(targetID);
	}

	protected XInstruction makeReplaceInstruction(int target){
		return new XInstructionJump(target);
	}
	
	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {
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

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		if(target==null){
			targetID = instructions.size();
		}else{
			targetID = instructions.indexOf(target);
		}
	}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return inst==target;
	}

	@Override
	public void replace(XCodeGen compiler, XInstruction instruction, XInstruction with, List<XInstruction> instructions) {
		if(instruction==target){
			target = with;
		}
	}
	
}
