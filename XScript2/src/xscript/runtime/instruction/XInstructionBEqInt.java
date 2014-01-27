package xscript.runtime.instruction;


public class XInstructionBEqInt extends XInstructionBMathInt {

	@Override
	public boolean calc(int left, int right) {
		return left>=right;
	}

	@Override
	public String name() {
		return "beq";
	}
	
}
