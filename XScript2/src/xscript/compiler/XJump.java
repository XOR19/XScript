package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.dumyinstruction.XInstructionDumyJump;
import xscript.compiler.dumyinstruction.XInstructionDumyJumpTargetResolver;
import xscript.compiler.tree.XTree;

public class XJump {
	
	private XInstructionDumyJump jump;
	
	private List<XInstructionDumyJumpTargetResolver> jumps;
	
	public XJump(XInstructionDumyJump jump){
		this.jump = jump;
	}
	
	public void addInstructions(XCodeGen gen, XTree tree){
		if(jumps!=null){
			for(XInstructionDumyJumpTargetResolver j:jumps){
				gen.addInstruction(j, tree.line.startLine);
			}
		}
		gen.addInstruction(jump, tree.line.startLine);
	}

	public XInstructionDumyJump addJump() {
		if(jumps==null){
			jumps = new ArrayList<XInstructionDumyJumpTargetResolver>();
			return jump;
		}
		XInstructionDumyJumpTargetResolver tr = new XInstructionDumyJumpTargetResolver();
		jumps.add(0, tr);
		return tr;
	}
	
}
