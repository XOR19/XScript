package xscript.runtime.instruction;

import java.io.IOException;
import java.util.Arrays;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionStringSwitch extends XInstruction {

	public final int def;
	public final int[] locArray;
	private final String[] strSwitch;
	
	public XInstructionStringSwitch(int def, int[] locArray, String[] strSwitch) {
		this.def = def;
		this.locArray = locArray;
		this.strSwitch = strSwitch;
	}

	public XInstructionStringSwitch(XInputStream inputStream) throws IOException {
		def = inputStream.readInt();
		locArray = new int[inputStream.readUnsignedShort()];
		for(int i=0; i<locArray.length; i++){
			locArray[i] = inputStream.readInt();
		}
		strSwitch = new String[inputStream.readUnsignedShort()];
		for(int i=0; i<locArray.length; i++){
			strSwitch[i] = inputStream.readUTF();
		}
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long l = methodExecutor.oPop();
		XObject obj = vm.getObjectProvider().getObject(l);
		String value = vm.getObjectProvider().getString(obj);
		int min = 0;
		int max = strSwitch.length-1;
		int prevPos = -1;
		while(true){
			int pos = (min+max)/2;
			int comp = strSwitch[pos].compareTo(value);
			if(comp>0){
				min = pos;
			}else if(comp<0){
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
		outputStream.writeShort(strSwitch.length);
		for(int i=0; i<strSwitch.length; i++)
			outputStream.writeUTF(strSwitch[i]);
	}

	@Override
	public String getSource() {
		return "stringswitch "+def+":"+Arrays.toString(strSwitch)+Arrays.toString(locArray);
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
