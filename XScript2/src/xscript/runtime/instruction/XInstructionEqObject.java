package xscript.runtime.instruction;


public class XInstructionEqObject extends XInstructionBMathObject {

	@Override
	public boolean calc(long left, long right) {
		return left==right;
	}

	@Override
	public String name() {
		return "eq";
	}

}
