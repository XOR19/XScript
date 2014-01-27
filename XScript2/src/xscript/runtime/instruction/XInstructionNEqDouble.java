package xscript.runtime.instruction;


public class XInstructionNEqDouble extends XInstructionBMathDouble {

	@Override
	public boolean calc(double left, double right) {
		return left!=right;
	}

	@Override
	public String name() {
		return "neq";
	}

}
