package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XChecks;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionWriteLocal extends XInstruction {

	private final int local;
	private XClassPtr localType;
	private int prim;
	
	public XInstructionWriteLocal(int local){
		this.local = local;
	}
	
	public XInstructionWriteLocal(XInputStream inputStream) throws IOException{
		local = inputStream.readUnsignedShort();
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		if(localType==null){
			localType = methodExecutor.getLocalType(local);
			prim = XPrimitive.getPrimitiveID(localType.getXClass(vm));
		}
		long value = methodExecutor.pop(prim);
		if(prim==XPrimitive.OBJECT){
			XObject obj = vm.getObjectProvider().getObject(value);
			if(obj!=null)
				XChecks.checkCast(obj.getXClass(), localType.getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor));
		}
		methodExecutor.setLocal(local, value);
		methodExecutor.push(value, prim);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeShort(local);
	}

	@Override
	public String getSource() {
		return "wl "+local;
	}
	
}
