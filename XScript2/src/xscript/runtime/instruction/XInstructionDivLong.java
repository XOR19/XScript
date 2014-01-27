package xscript.runtime.instruction;


public class XInstructionDivLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return left/right;
	}

	@Override
	public String name() {
		return "div";
	}
	
}
