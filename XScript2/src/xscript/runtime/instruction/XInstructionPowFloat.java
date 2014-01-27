package xscript.runtime.instruction;


public class XInstructionPowFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return (float) Math.pow(left, right);
	}

	@Override
	public String name() {
		return "pow";
	}

}
