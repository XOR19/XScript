package xscript.runtime.instruction;


public class XInstructionPowInt extends XInstructionMathInt {

	@Override
	public int calc(int left, int right) {
		return (int) Math.pow(left, right);
	}

	@Override
	public String name() {
		return "pow";
	}

}
