package xscript.compiler.optimizers;

import java.util.LinkedList;

import xscript.compiler.inst.XInst;
import xscript.compiler.inst.XInstRef;

public abstract class XForeachOptimizer implements XOptimizer {

	@Override
	public boolean optimize(XInst first) {
		LinkedList<XInstRef> list = new LinkedList<XInstRef>();
		XInstRef firstRef = new XInstRef(first);
		boolean didSomething = false;
		
		list.add(firstRef);
		
		while(!list.isEmpty()){
			XInstRef inst = list.removeFirst();
			didSomething |= optimizeInst(inst.getInst());
			for(XInstRef next:inst.getInst()){
				if(next.getInst()!=null)
					list.addLast(next);
			}
		}
		
		firstRef.dispose();
		
		return didSomething;
	}
	
	public abstract boolean optimizeInst(XInst inst);
	
}
