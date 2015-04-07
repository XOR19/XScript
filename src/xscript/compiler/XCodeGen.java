package xscript.compiler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import xscript.XFlags;
import xscript.XOpcode;
import xscript.compiler.inst.XInst;
import xscript.compiler.inst.XInst1B;
import xscript.compiler.inst.XInst1S;
import xscript.compiler.inst.XInst1Sh;
import xscript.compiler.inst.XInstCall;
import xscript.compiler.inst.XInstCondJump;
import xscript.compiler.inst.XInstJump;
import xscript.compiler.inst.XInstLine;
import xscript.compiler.inst.XInstRef;
import xscript.compiler.inst.XInstVarDecl;
import xscript.compiler.inst.XInstVarLookup;
import xscript.compiler.optimizers.XOptimizer;
import xscript.compiler.optimizers.XOptimizerCombinePop;
import xscript.compiler.optimizers.XOptimizerDeleteDeadCode;
import xscript.compiler.tree.XTree;

public class XCodeGen {

	protected XInstRef first;

	protected XInstRef last;

	protected int stackstart;

	protected List<XJumpTarget> jumps = new LinkedList<XJumpTarget>();

	protected List<XInst> flatten;

	public XCodeGen() {
	}

	public XCodeGen(int stackstart) {
		this.stackstart = stackstart;
	}

	public void addInstruction(XTree t, XOpcode opcode) {
		addInstruction(new XInst(t.position.position.line, opcode));
	}

	public void addInstruction(XTree t, XOpcode opcode, int i) {
		addInstruction(new XInst1Sh(t.position.position.line, opcode, i));
	}

	public void addInstructionB(XTree t, XOpcode opcode, int i) {
		addInstruction(new XInst1B(t.position.position.line, opcode, i));
	}

	public void addInstruction(XTree t, XOpcode opcode, XJumpTarget target) {
		if (opcode == XOpcode.JUMP) {
			setNext(t, target);
		} else {
			target.addJump();
			addInstruction(new XInstCondJump(t.position.position.line, opcode, target));
		}
	}

	public void addInstruction(XTree t, XOpcode opcode, String s) {
		addInstruction(new XInst1S(t.position.position.line, opcode, s));
	}

	public void addInstruction(XTree t, XVar var) {
		addInstruction(new XInstVarDecl(t.position.position.line, var));
	}

	public void addInstruction2(XTree t, XOpcode opcode, XVar var) {
		addInstruction(new XInstVarLookup(t.position.position.line, opcode, var));
	}

	public void addInstruction(XTree t, String[] kws, int unpackList, int unpackMap, int params) {
		addInstruction(new XInstCall(t.position.position.line, kws, unpackList, unpackMap, params));
	}

	public void addTarget(XTree t, XJumpTarget target) {
		jumps.add(target);
	}

	public void setNext(XTree t, XJumpTarget target) {
		if (last != null && last.getInst().next != null) {
			target.addJump();
			last.getInst().next.setInst(target.target);
		}
	}

	private void fillJumps(XInst instruction) {
		for (XJumpTarget target : jumps) {
			target.target.setInst(instruction);
		}
		jumps.clear();
	}

	public void addInstruction(XInst instruction) {
		if (last == null) {
			first = new XInstRef(instruction);
			last = new XInstRef();
		} else if (last.getInst().next != null && last.getInst().next.getInst() == null) {
			last.getInst().next.setInst(instruction);
		}
		last.setInst(instruction);
		fillJumps(last.getInst());
	}

	public void addInstructions(XCodeGen codeGen) {
		if (last == null) {
			first = codeGen.first;
			last = codeGen.last;
			if(codeGen.first!=null)
				fillJumps(codeGen.first.getInst());
		} else if (last.getInst().next != null && last.getInst().next.getInst() == null) {
			last.getInst().next.setInst(codeGen.first.getInst());
			last.setInst(codeGen.last.getInst());
			if(codeGen.first!=null)
				fillJumps(codeGen.first.getInst());
			codeGen.dispose();
		}
		jumps = codeGen.jumps;
	}

	public void dispose() {
		if (first != null) {
			first.dispose();
			first = null;
		}
		if (last != null) {
			last.dispose();
			last = null;
		}
	}

	private void addLines() {

		LinkedList<XInstRef> insts = new LinkedList<XInstRef>();

		insts.add(first);

		while (!insts.isEmpty()) {
			XInstRef inst = insts.removeFirst();
			for (XInstRef ref : inst.getInst()) {
				XInst next = ref.getInst();
				if(next==null)
					continue;
				if (!(next instanceof XInstLine) && next.line != inst.getInst().line) {
					ref.setInst(next.getLineInst().getInst());
				}
				insts.addLast(ref);
			}
		}

	}

	private void flatten() {
		flatten = new ArrayList<XInst>();
		LinkedList<XInstRef> jumpsToDo = new LinkedList<XInstRef>();
		jumpsToDo.add(first);
		while (!jumpsToDo.isEmpty()) {
			XInstRef next = jumpsToDo.removeFirst();
			while (next != null) {
				XInst inst = next.getInst();
				if(inst==null)
					break;
				if (inst.placed) {
					XInst jump;
					next.setInst(jump = new XInstJump(inst.line));
					jump.next.setInst(inst);
					flatten.add(jump);
					jump.placed = true;
					next = null;
				} else {
					flatten.add(inst);
					if (inst.jumps != null) {
						for (XInstRef j : inst.jumps)
							jumpsToDo.add(j);
					}
					inst.placed = true;
					next = inst.next;
				}
			}
		}
	}

	private void resolve() {
		int byteOff = 0;
		for (XInst inst : flatten) {
			inst.resolved = byteOff;
			byteOff += inst.getSize();
		}
	}

	private void compileSubparts() {
		if(first==null)
			return;
		LinkedList<XInstRef> insts = new LinkedList<XInstRef>();
		insts.add(first);
		while (!insts.isEmpty()) {
			XInstRef inst = insts.removeFirst();
			inst.getInst().compileSubparts();
			for (XInstRef ref : inst.getInst()) {
				if(ref.getInst()!=null)
					insts.addLast(ref);
			}
		}
	}

	private void checkStackSize() {
		LinkedList<XInst> list = new LinkedList<XInst>();
		XInst inst = first.getInst();
		inst.savedStackSize = stackstart;
		list.add(inst);
		while (!list.isEmpty()) {
			inst = list.removeFirst();
			int stacksize = inst.savedStackSize;
			stacksize -= inst.getStackRemove();
			if (stacksize < 0) {
				if (flatten == null)
					flatten();
				int i = 0;
				int pp = 0;
				for (XInst instr : flatten) {
					System.out.println(i + ":\t" + instr.savedStackSize + "\t" + instr.toString());
					if (instr == inst) {
						pp = i;
					}
					i++;
				}
				throw new AssertionError("stacksize smaller than 0 at " + pp);
			}
			stacksize += inst.getStackAdd();
			inst.stackSizeDone = true;
			for (XInstRef next : inst) {
				inst = next.getInst();
				if(inst==null)
					continue;
				if (inst.stackSizeDone) {
					if (inst.savedStackSize != stacksize) {
						if (flatten == null)
							flatten();
						int i = 0;
						int pp = 0;
						for (XInst instr : flatten) {
							System.out.println(i + ":\t" + instr.savedStackSize + "\t" + instr.toString());
							if (instr == inst) {
								pp = i;
							}
							i++;
						}
						throw new AssertionError("StackSize diff at:" + pp);
					}
				} else {
					inst.savedStackSize = stacksize;
					list.addLast(inst);
				}
			}
		}
	}

	public void generateFinalCode(XCompilerOptions options) {
		List<XOptimizer> optimizers = new ArrayList<XOptimizer>();
		//optimizers.add(new XOptimizerDeleteDeadCode());
		optimizers.add(new XOptimizerCombinePop());
		compileSubparts();
		boolean didSomething;
		do {
			didSomething = false;
			for (XOptimizer optimizer : optimizers) {
				didSomething |= optimizer.optimize(first.getInst());
			}
		} while (didSomething);
		/*
		 * boolean didSomething; do{ didSomething = deleteDeadCode();
		 * didSomething |= makeEasy(); }while(didSomething);
		 */

		checkStackSize();

		if (!options.removeLines) {
			addLines();
		}
		flatten();
		resolve();

		if (XFlags.DEBUG) {
			System.out.println("=========================================");
			System.out.println("               GEN                       ");
			System.out.println("=========================================");
			int i = 0;
			for (XInst instr : flatten) {
				System.out.println(instr.savedStackSize + "\t" + instr.toString());
			}
		}
	}

	public XInst[] getInstructions() {
		return flatten.toArray(new XInst[flatten.size()]);
	}

	public boolean isEmpty() {
		return first == null;
	}

	public List<XInst> getInstructionList() {
		return flatten;
	}

	public void getCode(XDataOutput dataOutput, XCompilerOptions options) {
		generateFinalCode(options);
		for (XInst i : flatten) {
			i.toCode(dataOutput);
		}
	}

}
