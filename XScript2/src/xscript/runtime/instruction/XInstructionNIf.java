package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;

public class XInstructionNIf extends XInstructionJump {

	public XInstructionNIf(int target) {
		super(target);
	}
	
	public XInstructionNIf(XInputStream inputStream) throws IOException {
		super(inputStream);
	}

	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		if(!methodExecutor.zPop()){
			super.run(vm, thread, methodExecutor);
		}
	}

	@Override
	public String getSource() {
		return "nif "+super.getSource();
	}

	@Override
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi) {
		return -1;
	}
	
}
