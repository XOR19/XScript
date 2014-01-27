package xscript.runtime.instruction;

import java.io.IOException;
import java.util.Arrays;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionBinSwitch extends XInstruction {

	public final int def;
	public final int[] locArray;
	private final int[] binSwitch;
	
	public XInstructionBinSwitch(int def, int[] locArray, int[] binSwitch) {
		this.def = def;
		this.locArray = locArray;
		this.binSwitch = binSwitch;
	}

	public XInstructionBinSwitch(XInputStream inputStream) throws IOException {
		def = inputStream.readInt();
		locArray = new int[inputStream.readUnsignedShort()];
		for(int i=0; i<locArray.length; i++){
			locArray[i] = inputStream.readInt();
		}
		binSwitch = new int[inputStream.readUnsignedShort()];
		for(int i=0; i<locArray.length; i++){
			binSwitch[i] = inputStream.readInt();
		}
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int value = methodExecutor.iPop();
		int min = 0;
		int max = binSwitch.length-1;
		int prevPos = -1;
		while(true){
			int pos = (min+max)/2;
			if(binSwitch[pos]>value){
				min = pos;
			}else if(binSwitch[pos]<value){
				max = pos;
			}else{
				methodExecutor.setProgramPointer(locArray[pos]);
				break;
			}
			if(prevPos==pos){
				methodExecutor.setProgramPointer(def);
				break;
			}
			prevPos = pos;
		}
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeInt(def);
		outputStream.writeShort(locArray.length);
		for(int i=0; i<locArray.length; i++)
			outputStream.writeInt(locArray[i]);
		outputStream.writeShort(binSwitch.length);
		for(int i=0; i<binSwitch.length; i++)
			outputStream.writeInt(binSwitch[i]);
	}

	@Override
	public String getSource() {
		return "binswitch "+def+":"+Arrays.toString(binSwitch)+Arrays.toString(locArray);
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
