package xscript.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionBinSwitch;
import xscript.runtime.instruction.XInstructionTableSwitch;

public class XInstructionDumySwitch extends XInstructionDumy {

	public HashMap<Integer, XInstruction> table = new HashMap<Integer, XInstruction>();

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
		int size = table.size();
		int diff = max-min+1;
		if(size+100<diff){
			int[] binSwitch = new int[size-1];
			int[] locArray = new int[size-1];
			int n = 0;
			for(Integer i:table.keySet()){
				if(i!=null){
					binSwitch[n++] = i;
				}
			}
			Arrays.sort(binSwitch);
			for(int i=0; i<binSwitch.length; i++){
				locArray[i] = getLoc(table.get(binSwitch[i]), instructions);
			}
			int def = getLoc(table.get(null), instructions);
			return new XInstructionBinSwitch(def, locArray, binSwitch);
		}else{
			int[] locArray = new int[diff];
			int def = getLoc(table.get(null), instructions);
			for(int i=0; i<diff; i++){
				int key = i+min;
				if(table.containsKey(key)){
					locArray[i] = getLoc(table.get(key), instructions);
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
				table.put(e.getKey(), target);
			}
		}
	}

}
