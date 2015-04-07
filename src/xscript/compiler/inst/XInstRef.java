package xscript.compiler.inst;

public class XInstRef {

	private Object instOrRef;

	public XInstRef() {

	}

	public XInstRef(XInst inst) {
		this.instOrRef = inst;
		inst.addRef(this);
	}

	public XInstRef(XInstRef ref) {
		this.instOrRef = ref;
	}

	public void setInst(XInst inst) {
		if (instOrRef instanceof XInst) {
			((XInst) instOrRef).removeRef(this);
		}
		this.instOrRef = inst;
		inst.addRef(this);
	}

	public void setInst(XInstRef ref) {
		if (instOrRef instanceof XInst) {
			((XInst) instOrRef).removeRef(this);
		}
		this.instOrRef = ref;
	}

	public void dispose() {
		if (instOrRef instanceof XInst) {
			((XInst) instOrRef).removeRef(this);
		}
		this.instOrRef = null;
	}

	public XInst getInst() {
		return instOrRef instanceof XInstRef ? ((XInstRef) instOrRef).getInst() : (XInst) instOrRef;
	}

}
