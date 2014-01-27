package xscript.runtime.instruction;


public class XInstructionAddLong extends XInstructionMathLong {

	@Override
	public long calc(long left, long right) {
		return left+right;
	}

	@Override
	public String name() {
		return "add";
	}
	
}
