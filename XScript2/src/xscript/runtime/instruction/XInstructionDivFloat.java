package xscript.runtime.instruction;


public class XInstructionDivFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return left/right;
	}

	@Override
	public String name() {
		return "div";
	}

	
}
