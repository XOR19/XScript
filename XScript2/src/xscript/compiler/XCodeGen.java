package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.compiler.dumyinstruction.XInstructionDumy;
import xscript.compiler.dumyinstruction.XInstructionDumyDelete;
import xscript.compiler.dumyinstruction.XInstructionDumyJump;
import xscript.compiler.dumyinstruction.XInstructionDumyStringSwitch;
import xscript.compiler.dumyinstruction.XInstructionDumySwitch;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionBinSwitch;
import xscript.runtime.instruction.XInstructionIf;
import xscript.runtime.instruction.XInstructionJump;
import xscript.runtime.instruction.XInstructionNIf;
import xscript.runtime.instruction.XInstructionStringSwitch;
import xscript.runtime.instruction.XInstructionTableSwitch;
import xscript.runtime.instruction.XInstructionVarJump;
import xscript.runtime.method.XCatchEntry;
import xscript.runtime.method.XLineEntry;
import xscript.runtime.method.XLocalEntry;
import xscript.runtime.threads.XMethodInfo;

public class XCodeGen{

	protected List<XInstruction> instructions = new ArrayList<XInstruction>();
	protected List<Integer> lines = new ArrayList<Integer>();
	protected List<XTryHandle> tryHandles = new ArrayList<XTryHandle>();
	protected List<XVariable> variables = new ArrayList<XVariable>();
	private int maxStackSize;
	private int maxObjectStackSize;
	
	public void addInstruction(int pos, XInstruction instruction, int line){
		instructions.add(pos, instruction);
		lines.add(pos, line);
	}
	
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
						delete(inst);
						i.remove();
						i2.remove();
					}
				}else{
					if(inst.getClass() == XInstructionDumyJump.class || inst.getClass() == XInstructionVarJump.class){
						wasJump = true;
						XInstruction next = null;
						if(i.hasNext()){
							next = i.next();
							i.previous();
							i.previous();
							i.next();
						}
						if(inst instanceof XInstructionDumy && ((XInstructionDumy) inst).pointingTo(next)){
							deleted = true;
							delete(inst);
							i.remove();
							i2.remove();
							wasJump = false;
						}
					}else if(inst.getClass() == XInstructionDumySwitch.class || inst.getClass() == XInstructionDumyStringSwitch.class){
						wasJump = true;
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
	
	private void delete(XInstruction instruction){
		ListIterator<XInstruction> i = instructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				((XInstructionDumy) inst).deleteInstruction(this, instructions, instruction);
			}
		}
		for(XTryHandle tryHandle:tryHandles){
			tryHandle.deleteInstruction(this, instructions, instruction);
		}
		for(XVariable variable:variables){
			variable.deleteInstruction(this, instructions, instruction);
		}
	}
	
	private void replace(XInstruction instruction, XInstruction with){
		ListIterator<XInstruction> i = instructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				((XInstructionDumy) inst).replace(this, instruction, with, instructions);
			}
		}
		for(XTryHandle tryHandle:tryHandles){
			tryHandle.replace(this, instruction, with, instructions);
		}
		for(XVariable variable:variables){
			variable.replace(this, instruction, with, instructions);
		}
	}
	
	private void makeEasy(){
		
	}
	
	private void calculateMaxStackSize(XMethodCompiler mc){
		int[][] sizes = new int[instructions.size()][];
		tryWay(mc.getDeclaringClass().getVirtualMachine(), new XCodeGenMethodInfo(mc), 0, 0, 0, sizes);
		maxStackSize = 0;
		maxObjectStackSize = 0;
		for(int[] size:sizes){
			if(maxStackSize<size[0]){
				maxStackSize = size[0];
			}
			if(maxObjectStackSize<size[1]){
				maxObjectStackSize = size[1];
			}
		}
	}
	
	private void tryWay(XVirtualMachine vm, XCodeGenMethodInfo cgmi, int programPointer, int stackSize, int objectStackSize, int[][] sizes){
		while(instructions.size()>programPointer){
			cgmi.setProgramPointer(programPointer);
			XInstruction inst = instructions.get(programPointer);
			int[] size = sizes[programPointer];
			stackSize += inst.getStackChange(vm, cgmi);
			objectStackSize += inst.getObjectStackChange(vm, cgmi);
			if(stackSize<0 || objectStackSize<0){
				System.out.println(instructions);
				throw new AssertionError("stacksize smaller than 0 at "+programPointer);
			}
			if(size==null){
				size = new int[]{stackSize, objectStackSize};
				sizes[programPointer] = size;
			}else{
				if(size[0] != stackSize || size[1] != objectStackSize){
					throw new AssertionError();
				}
				return;
			}
			programPointer++;
			if(inst instanceof XInstructionIf || inst instanceof XInstructionNIf){
				tryWay(vm, cgmi, ((XInstructionJump) inst).target, stackSize, objectStackSize, sizes);
			}else if(inst instanceof XInstructionBinSwitch){
				for(int i:((XInstructionBinSwitch) inst).locArray){
					tryWay(vm, cgmi, i, stackSize, objectStackSize, sizes);
				}
				programPointer = ((XInstructionBinSwitch) inst).def;
			}else if(inst instanceof XInstructionTableSwitch){
				for(int i:((XInstructionTableSwitch) inst).locArray){
					tryWay(vm, cgmi, i, stackSize, objectStackSize, sizes);
				}
				programPointer = ((XInstructionTableSwitch) inst).def;
			}else if(inst instanceof XInstructionStringSwitch){
				for(int i:((XInstructionStringSwitch) inst).locArray){
					tryWay(vm, cgmi, i, stackSize, objectStackSize, sizes);
				}
				programPointer = ((XInstructionStringSwitch) inst).def;
			}else if(inst instanceof XInstructionJump){
				programPointer = ((XInstructionJump) inst).target;
			}
		}
		if(stackSize!=0 || objectStackSize!=0){
			System.out.println(instructions);
			System.out.println(stackSize+", "+objectStackSize);
			throw new AssertionError();
		}
	}
	
	private class XCodeGenMethodInfo implements XMethodInfo{

		private XMethodCompiler mc;
		private int programPointer = 0;
		
		public XCodeGenMethodInfo(XMethodCompiler mc) {
			this.mc = mc;
		}

		public void setProgramPointer(int programPointer) {
			this.programPointer = programPointer;
		}

		@Override
		public int getLocalPrimitveID(int local) {
			for(XVariable var:variables){
				if(var.id==local && var.localEntry.isIn(programPointer)){
					return var.type.getPrimitiveID();
				}
			}
			throw new AssertionError("local not found "+local);
		}

		@Override
		public int getMethodReturnPrimitveID() {
			return mc.getReturnTypePrimitive();
		}
		
	}
	
	public void generateFinalCode(XMethodCompiler mc){
		deleteDumies();
		deleteDeadCode();
		makeEasy();
		resolve();
		replace();
		calculateMaxStackSize(mc);
		System.out.println("gen:"+mc.getName()+instructions);
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

	public int getMaxStackSize(){
		return maxStackSize;
	}
	
	public int getMaxObjectStackSize(){
		return maxObjectStackSize;
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
