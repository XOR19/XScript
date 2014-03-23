package xscript.runtime.threads;

import xscript.runtime.XVirtualMachine;

public interface XThreadErroredListener {

	public void onThreadErrored(XVirtualMachine virtualMachine, XThread thread);

}
