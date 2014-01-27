package xscript.runtime.instruction;


public class XInstructionShlInt extends XInstructionMathInt {

	@Override
	public int calc(int left, int right) {
		return left<<right;
	}

	@Override
	public String name() {
		return "shl";
	}

}
