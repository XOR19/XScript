package xscript.runtime.instruction;


public class XInstructionEqLong extends XInstructionBMathLong {

	@Override
	public boolean calc(long left, long right) {
		return left==right;
	}

	@Override
	public String name() {
		return "eq";
	}

}