package xscript.runtime.instruction;


public class XInstructionNEqBool extends XInstructionBMathBool {

	@Override
	public boolean calc(boolean left, boolean right) {
		return left!=right;
	}

	@Override
	public String name() {
		return "neq";
	}

}
