package xscript.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XCatchEntry;
import xscript.runtime.method.XCatchEntry.XCatchType;

public class XTryHandle {

	public XInstruction startInstruction;
	
	public XInstruction endInstruction;
	
	public HashMap<XClassPtr, XInstruction> jumpTargets = new HashMap<XClassPtr, XInstruction>();

	public XCatchEntry catchEntry;
	
	public void deleteInstruction(XCodeGen xCodeGen, List<XInstruction> instructions, XInstruction inst) {
		startInstruction = checkDelete(instructions, inst, startInstruction);
		endInstruction = checkDelete(instructions, inst, endInstruction);
		for(Entry<XClassPtr, XInstruction> jumpTarget:jumpTargets.entrySet()){
			jumpTarget.setValue(checkDelete(instructions, inst, jumpTarget.getValue()));
		}
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
	
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		int start = getLoc(startInstruction, instructions);
		int end = getLoc(endInstruction, instructions);
		XCatchType types[] = new XCatchType[jumpTargets.size()];
		int i=0;
		for(Entry<XClassPtr, XInstruction> jumpTarget:jumpTargets.entrySet()){
			types[i++] = new XCatchType(getLoc(jumpTarget.getValue(), instructions), jumpTarget.getKey());
		}
		catchEntry = new XCatchEntry(start, end, types);
	}

	public boolean pointingTo(XInstruction inst) {
		return jumpTargets.containsValue(inst);
	}
	
}
