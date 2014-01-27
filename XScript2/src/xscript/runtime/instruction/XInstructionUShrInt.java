package xscript.runtime.instruction;


public class XInstructionUShrInt extends XInstructionMathInt {

	@Override
	public int calc(int left, int right) {
		return left>>>right;
	}

	@Override
	public String name() {
		return "ushr";
	}

}