package xscript.compiler.dumyinstruction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import xscript.compiler.XCodeGen;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionStringSwitch;

public class XInstructionDumyStringSwitch extends XInstructionDumy {

	public HashMap<String, XInstruction> table = new HashMap<String, XInstruction>();

	public HashMap<String, Integer> resolved = new HashMap<String, Integer>();
	
	@Override
	public XInstruction replaceWith(XCodeGen compiler, List<XInstruction> instructions) {
		int size = resolved.size();
		String[] strSwitch = new String[size-1];
		int[] locArray = new int[size-1];
		int n = 0;
		for(String i:resolved.keySet()){
			if(i!=null){
				strSwitch[n++] = i;
			}
		}
		Arrays.sort(strSwitch);
		for(int i=0; i<strSwitch.length; i++){
			locArray[i] = resolved.get(strSwitch[i]);
		}
		int def = resolved.get(null);
		return new XInstructionStringSwitch(def, locArray, strSwitch);
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
		for(Entry<String, XInstruction> e:table.entrySet()){
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
		for(Entry<String, XInstruction> e:table.entrySet()){
			resolved.put(e.getKey(), getLoc(e.getValue(), instructions));
		}
	}

	@Override
	public boolean pointingTo(XInstruction inst) {
		return table.containsValue(inst);
	}

	@Override
	public void replace(XCodeGen compiler, XInstruction instruction, XInstruction with, List<XInstruction> instructions) {
		for(Entry<String, XInstruction> e : table.entrySet()){
			if(e.getValue()==instruction){
				e.setValue(with);
			}
		}
	}
	
}
