package xscript.runtime.instruction;


public class XInstructionPowDouble extends XInstructionMathDouble {

	@Override
	public double calc(double left, double right) {
		return Math.pow(left, right);
	}

	@Override
	public String name() {
		return "pow";
	}

}
