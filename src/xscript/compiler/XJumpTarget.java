package xscript.compiler;

import xscript.compiler.inst.XInstRef;

public class XJumpTarget {

	public final XInstRef target = new XInstRef();
	private int jumps;
	
	public void addJump(){
		jumps++;
	}
	
	public int jumps() {
		return jumps;
	}

}
