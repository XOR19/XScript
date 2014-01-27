package xscript.runtime.instruction;


public class XInstructionUShrLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return left>>>right;
	}

	@Override
	public String name() {
		return "ushr";
	}

}
