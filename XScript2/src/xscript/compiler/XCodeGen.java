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
	private List<XTryHandle> tryHandles = new ArrayList<XTryHandle>();
	private List<XVariable> variables = new ArrayList<XVariable>();
	
	public void addInstruction(XInstruction instruction, int line){
		instructions.add(instruction);
		lines.add(line);
	}
	
	public void addInstructions(XCodeGen codeGen){
		instructions.addAll(codeGen.instructions);
		lines.addAll(codeGen.lines);
		tryHandles.addAll(codeGen.tryHandles);
		variables.addAll(codeGen.variables);
	}
	
	private void deleteDumies(){
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
				for(XTryHandle tryHandle:tryHandles){
					tryHandle.deleteInstruction(this, instructions, inst);
				}
				for(XVariable variable:variables){
					variable.deleteInstruction(this, instructions, inst);
				}
				i.remove();
				i2.remove();
			}
		}
	}
	
	private void deleteDeadCode(){
		boolean deleted;
		do{
			deleted = false;
			boolean wasJump = false;
			ListIterator<XInstruction> i = instructions.listIterator();
			ListIterator<Integer> i2 = lines.listIterator();
			while(i.hasNext()){
				XInstruction inst = i.next();
				i2.next();
				if(wasJump){
					for(XInstruction instr:instructions){
						if(instr instanceof XInstructionDumy){
							if(((XInstructionDumy)instr).pointingTo(inst)){
								wasJump = false;
								break;
							}
						}
					}
					if(wasJump){
						for(XTryHandle tryHandle:tryHandles){
							if(tryHandle.pointingTo(inst)){
								wasJump = false;
								break;
							}
						}
					}
					if(wasJump){
						deleted = true;
						for(XInstruction inst2:instructions){
							if(inst2 instanceof XInstructionDumy){
								((XInstructionDumy) inst2).deleteInstruction(this, instructions, inst);
							}
						}
						for(XTryHandle tryHandle:tryHandles){
							tryHandle.deleteInstruction(this, instructions, inst);
						}
						for(XVariable variable:variables){
							variable.deleteInstruction(this, instructions, inst);
						}
						i.remove();
						i2.remove();
					}
				}else{
					if(inst instanceof XInstructionDumyJump && !(inst instanceof XInstructionDumyJumpTargetResolver)){
						wasJump = true;
						XInstruction next = null;
						if(i.hasNext()){
							next = i.next();
							i.previous();
							i.previous();
							i.next();
						}
						if(((XInstructionDumy) inst).pointingTo(next)){
							deleted = true;
							for(XInstruction inst2:instructions){
								if(inst2 instanceof XInstructionDumy){
									((XInstructionDumy) inst2).deleteInstruction(this, instructions, inst);
								}
							}
							for(XTryHandle tryHandle:tryHandles){
								tryHandle.deleteInstruction(this, instructions, inst);
							}
							for(XVariable variable:variables){
								variable.deleteInstruction(this, instructions, inst);
							}
							i.remove();
							i2.remove();
							wasJump = false;
						}
					}
				}
			}
		}while(deleted);
	}
	
	private void resolve(){
		ListIterator<XInstruction> i = instructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				((XInstructionDumy)inst).resolve(this, instructions);
			}
		}
		for(XTryHandle tryHandle:tryHandles){
			tryHandle.resolve(this, instructions);
		}
		for(XVariable variable:variables){
			variable.resolve(this, instructions);
		}
	}
	
	private void replace(){
		ListIterator<XInstruction> i = instructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				i.set(((XInstructionDumy) inst).replaceWith(this, instructions));
			}
		}
	}
	
	public void generateFinalCode(){
		deleteDumies();
		deleteDeadCode();
		resolve();
		replace();
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
		XCatchEntry[] catchEntries = new XCatchEntry[tryHandles.size()];
		for(int i=0; i<catchEntries.length; i++){
			catchEntries[i] = tryHandles.get(i).catchEntry;
		}
		return catchEntries;
	}

	public XLocalEntry[] getLocalEntries() {
		XLocalEntry[] localEntries = new XLocalEntry[variables.size()];
		for(int i=0; i<localEntries.length; i++){
			localEntries[i] = variables.get(i).localEntry;
		}
		return localEntries;
	}

	public boolean isEmpty() {
		return instructions.isEmpty();
	}

	public List<XInstruction> getInstructionList() {
		return instructions;
	}

	public void addTryHandler(XTryHandle tryHandle) {
		tryHandles.add(tryHandle);
	}

	public int getTryHandlerIndex(XTryHandle handle) {
		return tryHandles.indexOf(handle);
	}

	public void addVariable(XVariable var) {
		variables.add(var);
	}
	
}
