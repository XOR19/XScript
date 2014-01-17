package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.compiler.XVariable;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionWriteLocal;

public class XInstructionDumyWriteLocal extends XInstructionDumy {

	public XVariable var;
	
	public XInstructionDumyWriteLocal(){
		
	}
	
	public XInstructionDumyWriteLocal(XVariable var){
		this.var = var;
	}
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return new XInstructionWriteLocal(var.id);
	}

	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {}

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return false;
	}

}
