package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XCatchEntry;
import xscript.runtime.method.XLineEntry;
import xscript.runtime.method.XLocalEntry;

public class XCodeGen {

	private List<XInstruction> instructions = new ArrayList<XInstruction>();
	private List<Integer> lines = new ArrayList<Integer>();
	
	public void addInstruction(XInstruction instruction, int line){
		instructions.add(instruction);
		lines.add(line);
	}
	
	public void addInstructions(XCodeGen codeGen){
		instructions.addAll(codeGen.instructions);
		lines.addAll(codeGen.lines);
	}
	
	public void generateFinalCode(){
		ListIterator<XInstruction> i = instructions.listIterator();
		ListIterator<Integer> i2 = lines.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			i2.next();
			if(inst instanceof XInstructionDumyDelete){
				for(XInstruction inst2:instructions){
					if(inst2 instanceof XInstructionDumy){
						((XInstructionDumy) inst2).deleteInstruction(this, instructions, inst);
					}
				}
				i.remove();
				i2.remove();
			}
		}
		i = instructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				i.set(((XInstructionDumy) inst).replaceWith(this, instructions));
			}
		}
		System.out.println("gen:"+instructions);
	}
	
	public XInstruction[] getInstructions(){
		return instructions.toArray(new XInstruction[instructions.size()]);
	}
	
	public XLineEntry[] getLineEntries(){
		List<XLineEntry> lineEntries = new ArrayList<XLineEntry>();
		int lastLine = 0;
		for(int i=0; i<lines.size(); i++){
			int line = lines.get(i);
			if(lastLine!=line){
				lineEntries.add(new XLineEntry(i, line));
				lastLine = line;
			}
		}
		return lineEntries.toArray(new XLineEntry[lineEntries.size()]);
	}

	public XCatchEntry[] getCatchEntries() {
		// TODO Auto-generated method stub
		return new XCatchEntry[0];
	}

	public XLocalEntry[] getLocalEntries() {
		// TODO Auto-generated method stub
		return new XLocalEntry[0];
	}

	public boolean isEmpty() {
		return instructions.isEmpty();
	}

	public List<XInstruction> getInstructionList() {
		return instructions;
	}
	
}
