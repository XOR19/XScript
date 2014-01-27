package xscript.runtime.instruction;


public class XInstructionSubDouble extends XInstructionMathDouble {

	@Override
	public double calc(double left, double right) {
		return left-right;
	}

	@Override
	public String name() {
		return "sub";
	}

}
