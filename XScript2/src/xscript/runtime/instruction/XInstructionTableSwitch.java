package xscript.runtime.instruction;

import java.io.IOException;
import java.util.Arrays;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionTableSwitch extends XInstruction {

	private final int min;
	private final int def;
	private final int[] locArray;
	
	public XInstructionTableSwitch(int min, int def, int[] locArray) {
		this.min = min;
		this.def = def;
		this.locArray = locArray;
	}

	public XInstructionTableSwitch(XInputStream inputStream) throws IOException {
		min = inputStream.readInt();
		def = inputStream.readInt();
		locArray = new int[inputStream.readUnsignedShort()];
		for(int i=0; i<locArray.length; i++){
			locArray[i] = inputStream.readInt();
		}
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int value = methodExecutor.iPop();
		value -= min;
		if(value<0 || value>=locArray.length){
			methodExecutor.setProgramPointer(def);
		}else{
			methodExecutor.setProgramPointer(locArray[value]);
		}
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeInt(min);
		outputStream.writeInt(def);
		outputStream.writeShort(locArray.length);
		for(int i=0; i<locArray.length; i++)
			outputStream.writeInt(locArray[i]);
	}

	@Override
	public String getSource() {
		return "tableswitch "+min+" "+def+":"+Arrays.toString(locArray);
	}

}
