package xscript.compiler.dumyinstruction;

import java.util.List;

import xscript.compiler.XCodeGen;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionInvokeConstructor;
import xscript.runtime.method.XMethod;

public class XInstructionDumyInvokeConstructor extends XInstructionDumy {

	public XMethod method;
	public XClassPtr[] generics;
	
	public XInstructionDumyInvokeConstructor(){
		
	}
	
	public XInstructionDumyInvokeConstructor(XMethod method, XClassPtr[] generics){
		this.method = method;
		this.generics = generics;
	}
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		return new XInstructionInvokeConstructor(method, generics);
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
