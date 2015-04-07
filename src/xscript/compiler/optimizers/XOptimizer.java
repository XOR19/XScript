package xscript.compiler.optimizers;

import xscript.compiler.inst.XInst;

public interface XOptimizer {

	public boolean optimize(XInst first);
	
}
