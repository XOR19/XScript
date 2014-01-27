package xscript.runtime.instruction;


public class XInstructionMulFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return left*right;
	}

	@Override
	public String name() {
		return "mul";
	}

}
