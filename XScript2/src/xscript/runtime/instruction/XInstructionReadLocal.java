package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionReadLocal extends XInstruction {

	private final int local;
	private int prim=-1;
	
	public XInstructionReadLocal(int local){
		this.local = local;
	}
	
	public XInstructionReadLocal(XInputStream inputStream) throws IOException{
		local = inputStream.readUnsignedShort();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		if(prim==-1){
			prim = XPrimitive.getPrimitiveID(methodExecutor.getLocalType(local).getXClass(vm));
		}
		methodExecutor.push(methodExecutor.getLocal(local), prim);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(local);
	}

	@Override
	public String getSource() {
		return "rl "+local;
	}
	
	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		int primitive = mi.getLocalPrimitveID(local);
		return primitive==XPrimitive.OBJECT?0:primitive==XPrimitive.DOUBLE || primitive==XPrimitive.LONG?2:1;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return mi.getLocalPrimitveID(local)==XPrimitive.OBJECT?1:0;
	}

}
