package xscript.runtime.instruction;


public class XInstructionMulDouble extends XInstructionMathDouble {

	@Override
	public double calc(double left, double right) {
		return left*right;
	}

	@Override
	public String name() {
		return "mul";
	}

}
