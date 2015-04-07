package xscript.compiler.inst;

import xscript.XOpcode;
import xscript.compiler.XDataOutput;


public class XInstJump extends XInst {

	public XInstJump(int line) {
		super(line, XOpcode.JUMP);
	}

	@Override
	public void toCode(XDataOutput dataOutput) {
		super.toCode(dataOutput);
		dataOutput.writeShort(next.getInst().resolved);
	}

	@Override
	public int getSize() {
		return 3;
	}

	
	
}
