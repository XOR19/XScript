package xscript.runtime.instruction;


public class XInstructionMulInt extends XInstructionMathInt {

	@Override
	public int calc(int left, int right) {
		return left*right;
	}

	@Override
	public String name() {
		return "mul";
	}

}
