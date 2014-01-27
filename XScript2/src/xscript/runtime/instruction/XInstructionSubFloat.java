package xscript.runtime.instruction;


public class XInstructionSubFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return left-right;
	}

	@Override
	public String name() {
		return "sub";
	}

}
