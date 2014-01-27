package xscript.runtime.instruction;


public class XInstructionPowLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return (long) Math.pow(left, right);
	}

	@Override
	public String name() {
		return "pow";
	}

}

