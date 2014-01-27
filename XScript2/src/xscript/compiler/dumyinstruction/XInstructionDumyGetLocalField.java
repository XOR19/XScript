package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.compiler.XVariable;
import xscript.runtime.clazz.XField;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionGetLocalField;

public class XInstructionDumyGetLocalField extends XInstructionDumy {

	public XField field;
	public XVariable var;
	
	public XInstructionDumyGetLocalField(){
		
	}
	
	public XInstructionDumyGetLocalField(XVariable var, XField field){
		this.var = var;
		this.field = field;
	}
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return new XInstructionGetLocalField(var.id, field);
	}

	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {}

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return false;
	}

	@Override
	public void replace(XCodeGen compiler, XInstruction instruction, XInstruction with, List<XInstruction> instructions) {}

}
