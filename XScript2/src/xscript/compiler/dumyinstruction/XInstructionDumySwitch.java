package xscript.compiler.dumyinstruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import xscript.compiler.XCodeGen;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionBinSwitch;
import xscript.runtime.instruction.XInstructionTableSwitch;

public class XInstructionDumySwitch extends XInstructionDumy {

	public HashMap<Integer, XInstruction> table = new HashMap<Integer, XInstruction>();

	public HashMap<Integer, Integer> resolved = new HashMap<Integer, Integer>();
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		int max=Integer.MIN_VALUE;
		int min=Integer.MAX_VALUE;
		for(Integer i:table.keySet()){
			if(i!=null){
				if(i>max){
					max = i;
				}
				if(i<min){
					min = i;
				}
			}
		}
		int size = resolved.size();
		int diff = max-min+1;
		if(size*2+30<diff){
			int[] binSwitch = new int[size-1];
			int[] locArray = new int[size-1];
			int n = 0;
			for(Integer i:resolved.keySet()){
				if(i!=null){
					binSwitch[n++] = i;
				}
			}
			Arrays.sort(binSwitch);
			for(int i=0; i<binSwitch.length; i++){
				locArray[i] = resolved.get(binSwitch[i]);
			}
			int def = resolved.get(null);
			return new XInstructionBinSwitch(def, locArray, binSwitch);
		}else{
			int[] locArray = new int[diff];
			int def = resolved.get(null);
			for(int i=0; i<diff; i++){
				int key = i+min;
				if(resolved.containsKey(key)){
					locArray[i] = resolved.get(key);
				}else{
					locArray[i] = def;
				}
			}
			return new XInstructionTableSwitch(min, def, locArray);
		}
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
	
	@Override
	public void deleteInstruction(XCodeGen compiler, List<XInstruction> instructions, XInstruction instruction) {
		for(Entry<Integer, XInstruction> e:table.entrySet()){
			XInstruction target = e.getValue();
			if(instruction==target){
				int pos = instructions.indexOf(instruction);
				if(pos==-1)
					throw new AssertionError();
				pos++;
				if(pos<instructions.size()){
					target = instructions.get(pos);
				}else{
					target = null;
				}
				e.setValue(target);
			}
		}
	}

	@Override
	public void resolve(XCodeGen xCodeGen, List<XInstruction> instructions) {
		for(Entry<Integer, XInstruction> e:table.entrySet()){
			resolved.put(e.getKey(), getLoc(e.getValue(), instructions));
		}
	}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return table.containsValue(inst);
	}

	@Override
	public void replace(XCodeGen compiler, XInstruction instruction, XInstruction with, List<XInstruction> instructions) {
		for(Entry<Integer, XInstruction> e : table.entrySet()){
			if(e.getValue()==instruction){
				e.setValue(with);
			}
		}
	}
	
}
