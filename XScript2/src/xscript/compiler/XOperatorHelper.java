package xscript.compiler;

import java.util.EnumMap;

import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionAddDouble;
import xscript.runtime.instruction.XInstructionAddFloat;
import xscript.runtime.instruction.XInstructionAddInt;
import xscript.runtime.instruction.XInstructionAddLong;
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
import xscript.runtime.instruction.XInstructionSubDouble;
import xscript.runtime.instruction.XInstructionSubFloat;
import xscript.runtime.instruction.XInstructionSubInt;
import xscript.runtime.instruction.XInstructionSubLong;

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
		
		instructions.put(XOperator.POS, new Class[]{null, XInstructionDumyDelete.class, XInstructionDumyDelete.class, XInstructionDumyDelete.class, XInstructionDumyDelete.class});
		
		instructions.put(XOperator.EQ, new Class[]{XInstructionEqBool.class, XInstructionEqInt.class, XInstructionEqLong.class, XInstructionEqFloat.class, XInstructionEqDouble.class});
		instructions.put(XOperator.NEQ, new Class[]{XInstructionNEqBool.class, XInstructionNEqInt.class, XInstructionNEqLong.class, XInstructionNEqFloat.class, XInstructionNEqDouble.class});
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
