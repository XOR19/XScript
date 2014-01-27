package xscript.runtime.instruction;


public class XInstructionAddFloat extends XInstructionMathFloat {

	@Override
	public float calc(float left, float right) {
		return left+right;
	}

	@Override
	public String name() {
		return "add";
	}
	
}
