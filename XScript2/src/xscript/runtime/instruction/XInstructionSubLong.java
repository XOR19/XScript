package xscript.runtime.instruction;


public class XInstructionSubLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return left-right;
	}

	@Override
	public String name() {
		return "sub";
	}

}
