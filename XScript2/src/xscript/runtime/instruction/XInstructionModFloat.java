package xscript.runtime.instruction;


public class XInstructionModFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return left%right;
	}

	@Override
	public String name() {
		return "mod";
	}

}
