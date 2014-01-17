package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.compiler.XVariable;
import xscript.runtime.clazz.XField;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionSetLocalField;

public class XInstructionDumySetLocalField extends XInstructionDumy {

	public XField field;
	public XVariable var;
	
	public XInstructionDumySetLocalField(){
		
	}
	
	public XInstructionDumySetLocalField(XVariable var, XField field) {
		this.var = var;
		this.field = field;
	}

	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return new XInstructionSetLocalField(var.id, field);
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
