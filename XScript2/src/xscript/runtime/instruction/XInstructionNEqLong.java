package xscript.runtime.instruction;


public class XInstructionNEqLong extends XInstructionBMathLong {

	@Override
	public boolean calc(long left, long right) {
		return left!=right;
	}

	@Override
	public String name() {
		return "neq";
	}

}
