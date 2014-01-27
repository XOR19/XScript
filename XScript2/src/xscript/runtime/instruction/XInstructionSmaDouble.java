package xscript.runtime.instruction;


public class XInstructionSmaDouble extends XInstructionBMathDouble {

	@Override
	public boolean calc(double left, double right) {
		return left<right;
	}

	@Override
	public String name() {
		return "sma";
	}

}
