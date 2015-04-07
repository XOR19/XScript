package xscript.compiler.optimizers;

import xscript.XOpcode;
import xscript.compiler.inst.XInst;
import xscript.compiler.inst.XInst1B;

public class XOptimizerCombinePop extends XForeachOptimizer {

	@Override
	public boolean optimizeInst(XInst inst) {
		if (inst.jumps != null || inst.next == null || isPop(inst)) {
			XInst next = inst.next.getInst();
			if (next!=null && next.getRefCount() == 1 && isPop(next)) {
				int numInst = inst.getStackRemove() + next.getStackRemove();
				next.delete();
				if (numInst == 0) {
					inst.delete();
				} else if (numInst == 1 && inst.getOpcode() != XOpcode.POP_1) {
					inst.replace(new XInst(inst.line, XOpcode.POP_1));
				} else if (numInst > 1) {
					if (inst.getOpcode() == XOpcode.POP) {
						((XInst1B) inst).i = numInst;
					} else {
						inst.replace(new XInst1B(inst.line, XOpcode.POP, numInst));
					}
				}
			}
		}
		return false;
	}

	private boolean isPop(XInst inst){
		return inst.getOpcode() == XOpcode.POP_1 || inst.getOpcode() == XOpcode.POP;
	}
	
}
