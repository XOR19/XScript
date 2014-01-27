package xscript.runtime.instruction;


public class XInstructionOrBool extends XInstructionBMathBool {

	@Override
	public boolean calc(boolean left, boolean right) {
		return left||right;
	}

	@Override
	public String name() {
		return "or";
	}

}
