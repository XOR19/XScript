package xscript.runtime.instruction;


public class XInstructionModInt extends XInstructionMathInt {

	@Override
	public int calc(int left, int right) {
		return left%right;
	}

	@Override
	public String name() {
		return "mod";
	}

}
