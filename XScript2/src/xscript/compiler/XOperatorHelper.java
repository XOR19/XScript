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
import xscript.runtime.instruction.XInstructionModDouble;
import xscript.runtime.instruction.XInstructionModFloat;
import xscript.runtime.instruction.XInstructionModInt;
import xscript.runtime.instruction.XInstructionModLong;
import xscript.runtime.instruction.XInstructionMulDouble;
import xscript.runtime.instruction.XInstructionMulFloat;
import xscript.runtime.instruction.XInstructionMulInt;
import xscript.runtime.instruction.XInstructionMulLong;
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
