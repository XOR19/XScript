package xscript.compiler.inst;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import xscript.XArrayList;
import xscript.XOpcode;
import xscript.compiler.XDataOutput;

public class XInst implements Iterable<XInstRef> {

	public XInstRef lineInst;

	protected XOpcode opcode;

	public int line;

	public final List<XInstRef> jumps;

	public int resolved = -1;

	public final XInstRef next;

	private final LinkedList<XInstRef> refs = new LinkedList<XInstRef>();

	public boolean placed;

	public int savedStackSize;
	
	public boolean stackSizeDone;
	
	public XInst(int line, XOpcode opcode) {
		this.line = line;
		this.opcode = opcode;
		this.jumps = null;
		next = new XInstRef();
	}

	public XInst(int line, XOpcode opcode, XInstRef next, List<XInstRef> jumps) {
		this.line = line;
		this.opcode = opcode;
		this.next = next;
		this.jumps = jumps;
	}

	public XInst(int line, XOpcode opcode, XInstRef next, XInstRef... jumps) {
		this.line = line;
		this.opcode = opcode;
		this.next = next;
		this.jumps = new XArrayList<XInstRef>(jumps);
	}

	public int getI() {
		return 0;
	}

	public int getStackChange() {
		return opcode.getStackChange(getI());
	}

	public int getStackRemove() {
		return opcode.getStackRemove(getI());
	}

	public int getStackAdd() {
		return opcode.getStackAdd(getI());
	}

	public void toCode(XDataOutput dataOutput) {
		dataOutput.writeByte(opcode.ordinal());
	}

	@Override
	public String toString() {
		return opcode.toString();
	}

	public XOpcode getOpcode() {
		return opcode;
	}

	public int getSize() {
		return 1;
	}

	public void compileSubparts() {

	}

	public XInstRef getLineInst() {
		if (lineInst == null) {
			lineInst = new XInstRef(new XInstLine(line));
			lineInst.getInst().next.setInst(this);
		}
		return lineInst;
	}

	@Override
	public Iterator<XInstRef> iterator() {
		return new It();
	}

	private class It implements Iterator<XInstRef> {

		private boolean readJumps = next == null;

		private Iterator<XInstRef> it = jumps == null ? null : jumps.iterator();

		@Override
		public boolean hasNext() {
			return !readJumps || (it == null ? false : it.hasNext());
		}

		@Override
		public XInstRef next() {
			if (readJumps) {
				return it.next();
			}
			readJumps = true;
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public void addRef(XInstRef ref) {
		refs.add(ref);
	}

	public void removeRef(XInstRef ref) {
		refs.remove(ref);
	}

	public void replace(XInst inst) {
		while (refs.isEmpty()) {
			refs.removeFirst().setInst(inst);
		}
		if (inst.lineInst == null) {
			inst.lineInst = lineInst;
		} else if (lineInst != null) {
			lineInst.getInst().replace(inst.lineInst.getInst());
			lineInst.dispose();
		}
		inst.next.setInst(next.getInst());
	}

	public int getRefCount() {
		return refs.size();
	}

	public void delete() {
		if (lineInst != null) {
			lineInst.getInst().delete();
		}
		XInst next = this.next.getInst();
		while (refs.isEmpty()) {
			refs.removeFirst().setInst(next);
		}
		this.next.dispose();

	}

}
