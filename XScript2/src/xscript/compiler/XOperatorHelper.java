package xscript.compiler;

import java.util.EnumMap;

import xscript.compiler.dumyinstruction.XInstructionDumyDelete;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionAddDouble;
import xscript.runtime.instruction.XInstructionAddFloat;
import xscript.runtime.instruction.XInstructionAddInt;
import xscript.runtime.instruction.XInstructionAddLong;
import xscript.runtime.instruction.XInstructionAndBool;
import xscript.runtime.instruction.XInstructionAndInt;
import xscript.runtime.instruction.XInstructionAndLong;
import xscript.runtime.instruction.XInstructionBEqDouble;
import xscript.runtime.instruction.XInstructionBEqFloat;
import xscript.runtime.instruction.XInstructionBEqInt;
import xscript.runtime.instruction.XInstructionBEqLong;
import xscript.runtime.instruction.XInstructionBigDouble;
import xscript.runtime.instruction.XInstructionBigFloat;
import xscript.runtime.instruction.XInstructionBigInt;
import xscript.runtime.instruction.XInstructionBigLong;
import xscript.runtime.instruction.XInstructionCompBool;
import xscript.runtime.instruction.XInstructionCompDouble;
import xscript.runtime.instruction.XInstructionCompFloat;
import xscript.runtime.instruction.XInstructionCompInt;
import xscript.runtime.instruction.XInstructionCompLong;
import xscript.runtime.instruction.XInstructionDivDouble;
import xscript.runtime.instruction.XInstructionDivFloat;
import xscript.runtime.instruction.XInstructionDivInt;
import xscript.runtime.instruction.XInstructionDivLong;
import xscript.runtime.instruction.XInstructionEqBool;
import xscript.runtime.instruction.XInstructionEqDouble;
import xscript.runtime.instruction.XInstructionEqFloat;
import xscript.runtime.instruction.XInstructionEqInt;
import xscript.runtime.instruction.XInstructionEqLong;
import xscript.runtime.instruction.XInstructionModDouble;
import xscript.runtime.instruction.XInstructionModFloat;
import xscript.runtime.instruction.XInstructionModInt;
import xscript.runtime.instruction.XInstructionModLong;
import xscript.runtime.instruction.XInstructionMulDouble;
import xscript.runtime.instruction.XInstructionMulFloat;
import xscript.runtime.instruction.XInstructionMulInt;
import xscript.runtime.instruction.XInstructionMulLong;
import xscript.runtime.instruction.XInstructionNEqBool;
import xscript.runtime.instruction.XInstructionNEqDouble;
import xscript.runtime.instruction.XInstructionNEqFloat;
import xscript.runtime.instruction.XInstructionNEqInt;
import xscript.runtime.instruction.XInstructionNEqLong;
import xscript.runtime.instruction.XInstructionNegDouble;
import xscript.runtime.instruction.XInstructionNegFloat;
import xscript.runtime.instruction.XInstructionNegInt;
import xscript.runtime.instruction.XInstructionNegLong;
import xscript.runtime.instruction.XInstructionNotBool;
import xscript.runtime.instruction.XInstructionNotInt;
import xscript.runtime.instruction.XInstructionNotLong;
import xscript.runtime.instruction.XInstructionOrBool;
import xscript.runtime.instruction.XInstructionOrInt;
import xscript.runtime.instruction.XInstructionOrLong;
import xscript.runtime.instruction.XInstructionPowDouble;
import xscript.runtime.instruction.XInstructionPowFloat;
import xscript.runtime.instruction.XInstructionPowInt;
import xscript.runtime.instruction.XInstructionPowLong;
import xscript.runtime.instruction.XInstructionSEqDouble;
import xscript.runtime.instruction.XInstructionSEqFloat;
import xscript.runtime.instruction.XInstructionSEqInt;
import xscript.runtime.instruction.XInstructionSEqLong;
import xscript.runtime.instruction.XInstructionShlInt;
import xscript.runtime.instruction.XInstructionShlLong;
import xscript.runtime.instruction.XInstructionShrInt;
import xscript.runtime.instruction.XInstructionShrLong;
import xscript.runtime.instruction.XInstructionSmaDouble;
import xscript.runtime.instruction.XInstructionSmaFloat;
import xscript.runtime.instruction.XInstructionSmaInt;
import xscript.runtime.instruction.XInstructionSmaLong;
import xscript.runtime.instruction.XInstructionSubDouble;
import xscript.runtime.instruction.XInstructionSubFloat;
import xscript.runtime.instruction.XInstructionSubInt;
import xscript.runtime.instruction.XInstructionSubLong;
import xscript.runtime.instruction.XInstructionXorInt;
import xscript.runtime.instruction.XInstructionXorLong;

public class XOperatorHelper {
	
	@SuppressWarnings("rawtypes")
	public static final EnumMap<XOperator, Class[]> instructions = new EnumMap<XOperator, Class[]>(XOperator.class);
	
	public static final int BOOLINST = 0;
	public static final int INTINST = 1;
	public static final int LONGINST = 2;
	public static final int FLOATINST = 3;
	public static final int DOUBLEINST = 4;
	
	static{
		instructions.put(XOperator.ADD, new Class[]{null, XInstructionAddInt.class, XInstructionAddLong.class, XInstructionAddFloat.class, XInstructionAddDouble.class});
		instructions.put(XOperator.SUB, new Class[]{null, XInstructionSubInt.class, XInstructionSubLong.class, XInstructionSubFloat.class, XInstructionSubDouble.class});
		instructions.put(XOperator.MUL, new Class[]{null, XInstructionMulInt.class, XInstructionMulLong.class, XInstructionMulFloat.class, XInstructionMulDouble.class});
		instructions.put(XOperator.DIV, new Class[]{null, XInstructionDivInt.class, XInstructionDivLong.class, XInstructionDivFloat.class, XInstructionDivDouble.class});
		instructions.put(XOperator.MOD, new Class[]{null, XInstructionModInt.class, XInstructionModLong.class, XInstructionModFloat.class, XInstructionModDouble.class});
		instructions.put(XOperator.POW, new Class[]{null, XInstructionPowInt.class, XInstructionPowLong.class, XInstructionPowFloat.class, XInstructionPowDouble.class});
		instructions.put(XOperator.SHR, new Class[]{null, XInstructionShrInt.class, XInstructionShrLong.class, null, null});
		instructions.put(XOperator.SHL, new Class[]{null, XInstructionShlInt.class, XInstructionShlLong.class, null, null});
		instructions.put(XOperator.POS, new Class[]{null, XInstructionDumyDelete.class, XInstructionDumyDelete.class, XInstructionDumyDelete.class, XInstructionDumyDelete.class});
		instructions.put(XOperator.NEG, new Class[]{null, XInstructionNegInt.class, XInstructionNegLong.class, XInstructionNegFloat.class, XInstructionNegDouble.class});
		instructions.put(XOperator.BOR, new Class[]{XInstructionOrBool.class, XInstructionOrInt.class, XInstructionOrLong.class, null, null});
		instructions.put(XOperator.BAND, new Class[]{XInstructionAndBool.class, XInstructionAndInt.class, XInstructionAndLong.class, null, null});
		instructions.put(XOperator.XOR, new Class[]{XInstructionNEqBool.class, XInstructionXorInt.class, XInstructionXorLong.class, null, null});
		instructions.put(XOperator.NOT, new Class[]{XInstructionNotBool.class, null, null, null, null});
		instructions.put(XOperator.BNOT, new Class[]{null, XInstructionNotInt.class, XInstructionNotLong.class, null, null});
		instructions.put(XOperator.EQ, new Class[]{XInstructionEqBool.class, XInstructionEqInt.class, XInstructionEqLong.class, XInstructionEqFloat.class, XInstructionEqDouble.class});
		instructions.put(XOperator.REQ, new Class[]{XInstructionEqBool.class, XInstructionEqInt.class, XInstructionEqLong.class, XInstructionEqFloat.class, XInstructionEqDouble.class});
		instructions.put(XOperator.NEQ, new Class[]{XInstructionNEqBool.class, XInstructionNEqInt.class, XInstructionNEqLong.class, XInstructionNEqFloat.class, XInstructionNEqDouble.class});
		instructions.put(XOperator.RNEQ, new Class[]{XInstructionNEqBool.class, XInstructionNEqInt.class, XInstructionNEqLong.class, XInstructionNEqFloat.class, XInstructionNEqDouble.class});
		instructions.put(XOperator.BIG, new Class[]{null, XInstructionBigInt.class, XInstructionBigLong.class, XInstructionBigFloat.class, XInstructionBigDouble.class});
		instructions.put(XOperator.BEQ, new Class[]{null, XInstructionBEqInt.class, XInstructionBEqLong.class, XInstructionBEqFloat.class, XInstructionBEqDouble.class});
		instructions.put(XOperator.SMA, new Class[]{null, XInstructionSmaInt.class, XInstructionSmaLong.class, XInstructionSmaFloat.class, XInstructionSmaDouble.class});
		instructions.put(XOperator.SEQ, new Class[]{null, XInstructionSEqInt.class, XInstructionSEqLong.class, XInstructionSEqFloat.class, XInstructionSEqDouble.class});
		instructions.put(XOperator.COMP, new Class[]{XInstructionCompBool.class, XInstructionCompInt.class, XInstructionCompLong.class, XInstructionCompFloat.class, XInstructionCompDouble.class});
		instructions.put(XOperator.LETADD, new Class[]{null, XInstructionAddInt.class, XInstructionAddLong.class, XInstructionAddFloat.class, XInstructionAddDouble.class});
		instructions.put(XOperator.LETSUB, new Class[]{null, XInstructionSubInt.class, XInstructionSubLong.class, XInstructionSubFloat.class, XInstructionSubDouble.class});
		instructions.put(XOperator.LETMUL, new Class[]{null, XInstructionMulInt.class, XInstructionMulLong.class, XInstructionMulFloat.class, XInstructionMulDouble.class});
		instructions.put(XOperator.LETDIV, new Class[]{null, XInstructionDivInt.class, XInstructionDivLong.class, XInstructionDivFloat.class, XInstructionDivDouble.class});
		instructions.put(XOperator.LETMOD, new Class[]{null, XInstructionModInt.class, XInstructionModLong.class, XInstructionModFloat.class, XInstructionModDouble.class});
		instructions.put(XOperator.LETSHR, new Class[]{null, XInstructionShrInt.class, XInstructionShrLong.class, null, null});
		instructions.put(XOperator.LETSHL, new Class[]{null, XInstructionShlInt.class, XInstructionShlLong.class, null, null});
		instructions.put(XOperator.LETOR, new Class[]{XInstructionOrBool.class, XInstructionOrInt.class, XInstructionOrLong.class, null, null});
		instructions.put(XOperator.LETAND, new Class[]{XInstructionAndBool.class, XInstructionAndInt.class, XInstructionAndLong.class, null, null});
		instructions.put(XOperator.LETXOR, new Class[]{XInstructionNEqBool.class, XInstructionXorInt.class, XInstructionXorLong.class, null, null});
		instructions.put(XOperator.INC, new Class[]{null, XInstructionAddInt.class, XInstructionAddLong.class, XInstructionAddFloat.class, XInstructionAddDouble.class});
		instructions.put(XOperator.DEC, new Class[]{null, XInstructionSubInt.class, XInstructionSubLong.class, XInstructionSubFloat.class, XInstructionSubDouble.class});
		instructions.put(XOperator.INCS, new Class[]{null, XInstructionAddInt.class, XInstructionAddLong.class, XInstructionAddFloat.class, XInstructionAddDouble.class});
		instructions.put(XOperator.DECS, new Class[]{null, XInstructionSubInt.class, XInstructionSubLong.class, XInstructionSubFloat.class, XInstructionSubDouble.class});
		
	}
	
	public static XInstruction makeInstructionForOperator(XOperator operator, int type){
		@SuppressWarnings("unchecked")
		Class<? extends XInstruction>[] ca = instructions.get(operator);
		if(ca==null)
			return null;
		Class<? extends XInstruction> c = ca[type];
		if(c==null)
			return null;
		try {
			return c.newInstance();
		} catch (Exception e) {
		} 
		return null;
	}
	
}
