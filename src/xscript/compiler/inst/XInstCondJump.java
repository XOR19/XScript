package xscript.compiler.inst;

import xscript.XOpcode;
import xscript.compiler.XDataOutput;
import xscript.compiler.XJumpTarget;

public class XInstCondJump extends XInst {

	public XInstCondJump(int line, XOpcode opcode, XJumpTarget target) {
		super(line, opcode, new XInstRef(), new XInstRef(target.target));
	}

	@Override
	public void toCode(XDataOutput dataOutput) {
		super.toCode(dataOutput);
		dataOutput.writeShort(jumps.get(0).getInst().resolved);
	}

	@Override
	public String toString() {
		return super.toString() + " " + jumps.get(0).getInst().resolved;
	}

	@Override
	public int getSize() {
		return 3;
	}

}
