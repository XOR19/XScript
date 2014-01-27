package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XMethodInfo;
import xscript.runtime.threads.XThread;


public abstract class XInstruction {
	
	@SuppressWarnings("unchecked")
	public static Class<? extends XInstruction>[] instructions = new Class[256];
	
	static{
		instructions[0] = XInstructionAddDouble.class;
		instructions[1] = XInstructionAddFloat.class;
		instructions[2] = XInstructionAddInt.class;
		instructions[3] = XInstructionAddLong.class;
		instructions[4] = XInstructionAndBool.class;
		instructions[5] = XInstructionAndInt.class;
		instructions[6] = XInstructionAndLong.class;
		instructions[7] = XInstructionI2B.class;
		instructions[8] = XInstructionBEqDouble.class;
		instructions[9] = XInstructionBEqFloat.class;
		instructions[10] = XInstructionBEqInt.class;
		instructions[11] = XInstructionBEqLong.class;
		instructions[12] = XInstructionBigDouble.class;
		instructions[13] = XInstructionBigFloat.class;
		instructions[14] = XInstructionBigInt.class;
		instructions[15] = XInstructionBigLong.class;
		instructions[16] = XInstructionBinSwitch.class;
		instructions[17] = XInstructionCheckCast.class;
		instructions[18] = XInstructionCheckNull.class;
		instructions[19] = XInstructionCompBool.class;
		instructions[20] = XInstructionCompDouble.class;
		instructions[21] = XInstructionCompFloat.class;
		instructions[22] = XInstructionCompInt.class;
		instructions[23] = XInstructionCompLong.class;
		instructions[24] = XInstructionD2F.class;
		instructions[25] = XInstructionD2I.class;
		instructions[26] = XInstructionD2L.class;
		instructions[27] = XInstructionDivDouble.class;
		instructions[28] = XInstructionDivFloat.class;
		instructions[29] = XInstructionDivInt.class;
		instructions[30] = XInstructionDivLong.class;
		instructions[31] = XInstructionDup.class;
		instructions[32] = XInstructionEqBool.class;
		instructions[33] = XInstructionEqDouble.class;
		instructions[34] = XInstructionEqFloat.class;
		instructions[35] = XInstructionEqInt.class;
		instructions[36] = XInstructionEqLong.class;
		instructions[37] = XInstructionEqObject.class;
		instructions[38] = XInstructionF2D.class;
		instructions[39] = XInstructionF2I.class;
		instructions[40] = XInstructionF2L.class;
		instructions[41] = XInstructionGetField.class;
		instructions[42] = XInstructionGetLocalField.class;
		instructions[43] = XInstructionGetStaticField.class;
		instructions[44] = XInstructionI2D.class;
		instructions[45] = XInstructionI2F.class;
		instructions[46] = XInstructionI2L.class;
		instructions[47] = XInstructionIf.class;
		instructions[48] = XInstructionInstanceof.class;
		instructions[49] = XInstructionInvokeDynamic.class;
		instructions[50] = XInstructionInvokeSpecial.class;
		instructions[51] = XInstructionInvokeStatic.class;
		instructions[52] = XInstructionJump.class;
		instructions[53] = XInstructionL2D.class;
		instructions[54] = XInstructionL2F.class;
		instructions[55] = XInstructionL2I.class;
		instructions[56] = XInstructionLoadConstBool.class;
		instructions[57] = XInstructionLoadConstDouble.class;
		instructions[58] = XInstructionLoadConstFloat.class;
		instructions[59] = XInstructionLoadConstInt.class;
		instructions[60] = XInstructionLoadConstLong.class;
		instructions[61] = XInstructionLoadConstNull.class;
		instructions[62] = XInstructionLoadConstString.class;
		instructions[63] = XInstructionModDouble.class;
		instructions[64] = XInstructionModFloat.class;
		instructions[65] = XInstructionModInt.class;
		instructions[66] = XInstructionModLong.class;
		instructions[67] = XInstructionMonitorEnter.class;
		instructions[68] = XInstructionMonitorExit.class;
		instructions[69] = XInstructionMulDouble.class;
		instructions[70] = XInstructionMulFloat.class;
		instructions[71] = XInstructionMulInt.class;
		instructions[72] = XInstructionMulLong.class;
		instructions[73] = XInstructionNegDouble.class;
		instructions[74] = XInstructionNegFloat.class;
		instructions[75] = XInstructionNegInt.class;
		instructions[76] = XInstructionNegLong.class;
		instructions[77] = XInstructionNEqBool.class;
		instructions[78] = XInstructionNEqDouble.class;
		instructions[79] = XInstructionNEqFloat.class;
		instructions[80] = XInstructionNEqInt.class;
		instructions[81] = XInstructionNEqLong.class;
		instructions[82] = XInstructionNEqObject.class;
		instructions[83] = XInstructionNew.class;
		instructions[84] = XInstructionNewArray.class;
		instructions[85] = XInstructionNIf.class;
		instructions[86] = XInstructionNop.class;
		instructions[87] = XInstructionNotBool.class;
		instructions[88] = XInstructionNotInt.class;
		instructions[89] = XInstructionNotLong.class;
		instructions[90] = XInstructionODup.class;
		instructions[91] = XInstructionOPop.class;
		instructions[92] = XInstructionOrBool.class;
		instructions[93] = XInstructionOrInt.class;
		instructions[94] = XInstructionOrLong.class;
		instructions[95] = XInstructionOToTop.class;
		instructions[96] = XInstructionPop.class;
		instructions[97] = XInstructionPowDouble.class;
		instructions[98] = XInstructionPowFloat.class;
		instructions[99] = XInstructionPowInt.class;
		instructions[100] = XInstructionPowLong.class;
		instructions[101] = XInstructionReadLocal.class;
		instructions[102] = XInstructionReturn.class;
		instructions[103] = XInstructionI2S.class;
		instructions[104] = XInstructionSEqDouble.class;
		instructions[105] = XInstructionSEqFloat.class;
		instructions[106] = XInstructionSEqInt.class;
		instructions[107] = XInstructionSEqLong.class;
		instructions[108] = XInstructionSetField.class;
		instructions[109] = XInstructionSetLocalField.class;
		instructions[110] = XInstructionSetStaticField.class;
		instructions[111] = XInstructionShlInt.class;
		instructions[112] = XInstructionShlLong.class;
		instructions[113] = XInstructionShrInt.class;
		instructions[114] = XInstructionShrLong.class;
		instructions[115] = XInstructionSmaDouble.class;
		instructions[116] = XInstructionSmaFloat.class;
		instructions[117] = XInstructionSmaInt.class;
		instructions[118] = XInstructionSmaLong.class;
		instructions[119] = XInstructionSubDouble.class;
		instructions[120] = XInstructionSubFloat.class;
		instructions[121] = XInstructionSubInt.class;
		instructions[122] = XInstructionSubLong.class;
		instructions[123] = XInstructionTableSwitch.class;
		instructions[124] = XInstructionThrow.class;
		instructions[125] = XInstructionToTop.class;
		instructions[126] = XInstructionVarJump.class;
		instructions[127] = XInstructionWriteLocal.class;
		instructions[128] = XInstructionXorInt.class;
		instructions[129] = XInstructionXorLong.class;
		instructions[130] = XInstructionSetReturn.class;
		instructions[131] = XInstructionSwap.class;
		instructions[132] = XInstructionOSwap.class;
		instructions[133] = XInstructionStackSizeSave.class;
		instructions[134] = XInstructionLoadConstClass.class;
		instructions[135] = XInstructionInvokeConstructor.class;
		instructions[136] = XInstructionStringSwitch.class;
	}
	
	public abstract void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor);

	public void resolve(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor){
		
	}
	
	protected abstract void save(XOutputStream outputStream) throws IOException;

	public abstract String getSource();
	
	public int getStackChange(XVirtualMachine vm, XMethodInfo mi){
		return 0;
	}
	
	public int getObjectStackChange(XVirtualMachine vm, XMethodInfo mi){
		return 0;
	}
	
	@Override
	public String toString(){
		return getSource();
	}
	
	public static XInstruction load(XInputStream inputStream) throws IOException {
		int instruction = inputStream.readUnsignedByte();
		switch(instruction){
		case 0: return new XInstructionAddDouble();
		case 1: return new XInstructionAddFloat();
		case 2: return new XInstructionAddInt();
		case 3: return new XInstructionAddLong();
		case 4: return new XInstructionAndBool();
		case 5: return new XInstructionAndInt();
		case 6: return new XInstructionAndLong();
		case 7: return new XInstructionI2B();
		case 8: return new XInstructionBEqDouble();
		case 9: return new XInstructionBEqFloat();
		case 10: return new XInstructionBEqInt();
		case 11: return new XInstructionBEqLong();
		case 12: return new XInstructionBigDouble();
		case 13: return new XInstructionBigFloat();
		case 14: return new XInstructionBigInt();
		case 15: return new XInstructionBigLong();
		case 16: return new XInstructionBinSwitch(inputStream);
		case 17: return new XInstructionCheckCast(inputStream);
		case 18: return new XInstructionCheckNull();
		case 19: return new XInstructionCompBool();
		case 20: return new XInstructionCompDouble();
		case 21: return new XInstructionCompFloat();
		case 22: return new XInstructionCompInt();
		case 23: return new XInstructionCompLong();
		case 24: return new XInstructionD2F();
		case 25: return new XInstructionD2I();
		case 26: return new XInstructionD2L();
		case 27: return new XInstructionDivDouble();
		case 28: return new XInstructionDivFloat();
		case 29: return new XInstructionDivInt();
		case 30: return new XInstructionDivLong();
		case 31: return new XInstructionDup();
		case 32: return new XInstructionEqBool();
		case 33: return new XInstructionEqDouble();
		case 34: return new XInstructionEqFloat();
		case 35: return new XInstructionEqInt();
		case 36: return new XInstructionEqLong();
		case 37: return new XInstructionEqObject();
		case 38: return new XInstructionF2D();
		case 39: return new XInstructionF2I();
		case 40: return new XInstructionF2L();
		case 41: return new XInstructionGetField(inputStream);
		case 42: return new XInstructionGetLocalField(inputStream);
		case 43: return new XInstructionGetStaticField(inputStream);
		case 44: return new XInstructionI2D();
		case 45: return new XInstructionI2F();
		case 46: return new XInstructionI2L();
		case 47: return new XInstructionIf(inputStream);
		case 48: return new XInstructionInstanceof(inputStream);
		case 49: return new XInstructionInvokeDynamic(inputStream);
		case 50: return new XInstructionInvokeSpecial(inputStream);
		case 51: return new XInstructionInvokeStatic(inputStream);
		case 52: return new XInstructionJump(inputStream);
		case 53: return new XInstructionL2D();
		case 54: return new XInstructionL2F();
		case 55: return new XInstructionL2I();
		case 56: return new XInstructionLoadConstBool(inputStream);
		case 57: return new XInstructionLoadConstDouble(inputStream);
		case 58: return new XInstructionLoadConstFloat(inputStream);
		case 59: return new XInstructionLoadConstInt(inputStream);
		case 60: return new XInstructionLoadConstLong(inputStream);
		case 61: return new XInstructionLoadConstNull(inputStream);
		case 62: return new XInstructionLoadConstString(inputStream);
		case 63: return new XInstructionModDouble();
		case 64: return new XInstructionModFloat();
		case 65: return new XInstructionModInt();
		case 66: return new XInstructionModLong();
		case 67: return new XInstructionMonitorEnter();
		case 68: return new XInstructionMonitorExit();
		case 69: return new XInstructionMulDouble();
		case 70: return new XInstructionMulFloat();
		case 71: return new XInstructionMulInt();
		case 72: return new XInstructionMulLong();
		case 73: return new XInstructionNegDouble();
		case 74: return new XInstructionNegFloat();
		case 75: return new XInstructionNegInt();
		case 76: return new XInstructionNegLong();
		case 77: return new XInstructionNEqBool();
		case 78: return new XInstructionNEqDouble();
		case 79: return new XInstructionNEqFloat();
		case 80: return new XInstructionNEqInt();
		case 81: return new XInstructionNEqLong();
		case 82: return new XInstructionNEqObject();
		case 83: return new XInstructionNew(inputStream);
		case 84: return new XInstructionNewArray(inputStream);
		case 85: return new XInstructionNIf(inputStream);
		case 86: return new XInstructionNop();
		case 87: return new XInstructionNotBool();
		case 88: return new XInstructionNotInt();
		case 89: return new XInstructionNotLong();
		case 90: return new XInstructionODup();
		case 91: return new XInstructionOPop();
		case 92: return new XInstructionOrBool();
		case 93: return new XInstructionOrInt();
		case 94: return new XInstructionOrLong();
		case 95: return new XInstructionOToTop(inputStream);
		case 96: return new XInstructionPop();
		case 97: return new XInstructionPowDouble();
		case 98: return new XInstructionPowFloat();
		case 99: return new XInstructionPowInt();
		case 100: return new XInstructionPowLong();
		case 101: return new XInstructionReadLocal(inputStream);
		case 102: return new XInstructionReturn(inputStream);
		case 103: return new XInstructionI2S();
		case 104: return new XInstructionSEqDouble();
		case 105: return new XInstructionSEqFloat();
		case 106: return new XInstructionSEqInt();
		case 107: return new XInstructionSEqLong();
		case 108: return new XInstructionSetField(inputStream);
		case 109: return new XInstructionSetLocalField(inputStream);
		case 110: return new XInstructionSetStaticField(inputStream);
		case 111: return new XInstructionShlInt();
		case 112: return new XInstructionShlLong();
		case 113: return new XInstructionShrInt();
		case 114: return new XInstructionShrLong();
		case 115: return new XInstructionSmaDouble();
		case 116: return new XInstructionSmaFloat();
		case 117: return new XInstructionSmaInt();
		case 118: return new XInstructionSmaLong();
		case 119: return new XInstructionSubDouble();
		case 120: return new XInstructionSubFloat();
		case 121: return new XInstructionSubInt();
		case 122: return new XInstructionSubLong();
		case 123: return new XInstructionTableSwitch(inputStream);
		case 124: return new XInstructionThrow();
		case 125: return new XInstructionToTop(inputStream);
		case 126: return new XInstructionVarJump();
		case 127: return new XInstructionWriteLocal(inputStream);
		case 128: return new XInstructionXorInt();
		case 129: return new XInstructionXorLong();
		case 130: return new XInstructionSetReturn(inputStream);
		case 131: return new XInstructionSwap();
		case 132: return new XInstructionOSwap();
		case 133: return new XInstructionStackSizeSave(inputStream);
		case 134: return new XInstructionLoadConstClass(inputStream);
		case 135: return new XInstructionInvokeConstructor(inputStream);
		case 136: return new XInstructionStringSwitch(inputStream);
		}
		throw new XRuntimeException("Unknow instruction %s", instruction);
	}
	
	public static void save(XOutputStream outputStream, XInstruction instruction) throws IOException{
		int id = getInstructionID(instruction);
		outputStream.writeByte(id);
		instruction.save(outputStream);
	}
	
	public static int getInstructionID(XInstruction instruction){
		return getInstructionID(instruction.getClass());
	}
	
	public static int getInstructionID(Class<? extends XInstruction> c){
		for(int i=0; i<instructions.length; i++){
			if(instructions[i] == c){
				return i;
			}
		}
		throw new XRuntimeException("Unknown instruction type %s", c);
	}
	
}
