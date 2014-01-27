package xscript.runtime.instruction;


public class XInstructionSEqInt extends XInstructionBMathInt {

	@Override
	public boolean calc(int left, int right) {
		return left<=right;
	}

	@Override
	public String name() {
		return "seq";
	}

}
