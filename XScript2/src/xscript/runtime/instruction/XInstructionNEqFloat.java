package xscript.runtime.instruction;


public class XInstructionNEqFloat extends XInstructionBMathFloat {

	@Override
	public boolean calc(float left, float right) {
		return left!=right;
	}

	@Override
	public String name() {
		return "neq";
	}

}
