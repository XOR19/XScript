package xscript.compiler;

import java.util.List;

import xscript.compiler.classtypes.XVarType;
import xscript.compiler.tree.XTree.XTreeVarDecl;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XLocalEntry;


public class XVariable {

	public XTreeVarDecl varDecl;
	
	public int modifier;
	
	public XVarType type;
	
	public String name;

	public int id;
	
	public XInstruction start;
	
	public XInstruction end;

	public XLocalEntry localEntry;

	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		localEntry = new XLocalEntry(getLoc(start, instructions), getLoc(end, instructions), id, modifier, name, type.getXClassPtr());
	}

	public void deleteInstruction(XCodeGen xCodeGen, List<XInstruction> instructions, XInstruction inst) {
		start = checkDelete(instructions, inst, start);
		end = checkDelete(instructions, inst, end);
	}
	
	private XInstruction checkDelete(List<XInstruction> instructions, XInstruction inst, XInstruction inst2){
		if(inst==inst2){
			int pos = instructions.indexOf(inst);
			if(pos==-1)
				throw new AssertionError();
			pos++;
			if(pos<instructions.size()){
				inst2 = instructions.get(pos);
			}else{
				inst2 = null;
			}
		}
		return inst2;
	}
	
	private int getLoc(XInstruction target, List<XInstruction> instructions){
		int t;
		if(target==null){
			t = instructions.size();
		}else{
			t = instructions.indexOf(target);
		}
		if(t==-1)
			throw new AssertionError();
		return t;
	}

	public void replace(XCodeGen xCodeGen, XInstruction instruction, XInstruction with, List<XInstruction> instructions) {
		if(start==instruction){
			start = with;
		}
		if(end==instruction){
			end = with;
		}
	}
	
}
