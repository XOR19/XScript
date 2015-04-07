package xscript.compiler.optimizers;

import java.util.List;
import java.util.ListIterator;

import xscript.XOpcode;
import xscript.compiler.inst.XInst;

public class XOptimizerRemoveUnusedStackLine implements XOptimizer {

	@Override
	public boolean optimize(List<XInst> insts) {
		ListIterator<XInst> i = insts.listIterator();
		boolean b = false;
		while (i.hasNext()) {
			XInst inst = i.next();
			if(canRemoveIfUnusedOutput(inst.getOpcode())){
				
			}
		}
		return b;
	}

	private boolean get
	
	private static boolean canRemoveIfUnusedOutput(XOpcode opcode){
		switch(opcode){
		case DUP:
		case GETBOTTOM1:
		case GETBOTTOM2:
		case GETTOP1:
		case GETTOP2:
		case GET_CLOSURE:
		case GET_GLOBAL:
		case INSTANCEOF:
		case ISDERIVEDOF:
		case LOADB:
		case LOADD:
		case LOADD_0:
		case LOADD_1:
		case LOADD_2:
		case LOADD_M1:
		case LOADF:
		case LOADI:
		case LOADI_0:
		case LOADI_1:
		case LOADI_2:
		case LOADI_M1:
		case LOADL:
		case LOADN:
		case LOADS:
		case LOADT:
		case LOADT_E:
		case LOAD_FALSE:
		case LOAD_TRUE:
		case MAKE_CLASS:
		case MAKE_FUNC:
		case MAKE_LIST:
		case MAKE_MAP:
		case MAKE_METH:
		case MAKE_TUPLE:
		case NOP:
		case NOT_SAME:
		case POP:
		case POP_1:
		case SAME:
		case SUPER:
		case SWAP:
		case TYPEOF:
			return true;
		default:
			return false;
		}
	}
	
}
