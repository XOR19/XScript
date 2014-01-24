package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XChecks;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionCheckCast extends XInstruction {

	private XClassPtr xClass;
	
	public XInstructionCheckCast(XClassPtr xClass){
		this.xClass = xClass;
	}
	
	public XInstructionCheckCast(XInputStream inputStream) throws IOException{
		xClass = XClassPtr.load(inputStream);
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long value = methodExecutor.oPop();
		methodExecutor.oPush(value);
		XObject obj = vm.getObjectProvider().getObject(value);
		if(obj!=null){
			XGenericClass genericClass = obj.getXClass();
			XChecks.checkCast(genericClass, xClass.getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor));
		}
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		xClass.save(outputStream);
	}

	@Override
	public String getSource() {
		return "cc "+xClass;
	}

	
}
