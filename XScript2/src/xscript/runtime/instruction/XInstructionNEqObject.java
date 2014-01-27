package xscript.runtime.instruction;


public class XInstructionNEqObject extends XInstructionBMathObject {

	@Override
	public boolean calc(long left, long right) {
		return left!=right;
	}

	@Override
	public String name() {
		return "neq";
	}

}
