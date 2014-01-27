package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionLoadConstClass extends XInstruction {

	private final XClassPtr xClass;
	private XClass c;
	
	public XInstructionLoadConstClass(XClassPtr xClass){
		this.xClass = xClass;
	}
	
	public XInstructionLoadConstClass(XInputStream inputStream) throws IOException{
		xClass = XClassPtr.load(inputStream);
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		methodExecutor.oPush(c.getClassObject());
	}

	@Override
	public void resolve(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		c = xClass.getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor).getXClass();
	}
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		xClass.save(outputStream);
	}

	@Override
	public String getSource() {
		return "lcc "+xClass;
	}

	@Override
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return 1;
	}
	
}
