package xscript.runtime.instruction;


public class XInstructionEqFloat extends XInstructionBMathFloat {

	@Override
	public boolean calc(float left, float right) {
		return left==right;
	}

	@Override
	public String name() {
		return "eq";
	}

}
