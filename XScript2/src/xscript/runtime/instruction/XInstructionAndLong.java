package xscript.runtime.instruction;


public class XInstructionAndLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return left&right;
	}

	@Override
	public String name() {
		return "and";
	}
	
}
