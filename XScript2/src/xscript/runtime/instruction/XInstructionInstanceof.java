package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionInstanceof extends XInstruction {

	private XClassPtr xClass;
	
	public XInstructionInstanceof(XClassPtr xClass){
		this.xClass = xClass;
	}
	
	public XInstructionInstanceof(XInputStream inputStream) throws IOException{
		xClass = XClassPtr.load(inputStream);
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		long value = methodExecutor.oPop();
		XObject obj = vm.getObjectProvider().getObject(value);
		if(obj!=null){
			XGenericClass genericClass = obj.getXClass();
			methodExecutor.zPush(genericClass.canCastTo(xClass.getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor)));
		}else{
			methodExecutor.zPush(false);
		}
	}
	
	@Override
	public void resolve(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		xClass.getXClass(vm);
	}

	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		xClass.save(outputStream);
	}

	@Override
	public String getSource() {
		return "is "+xClass;
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return 1;
	}
	
	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
