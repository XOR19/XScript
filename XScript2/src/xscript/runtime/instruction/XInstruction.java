package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.threads.XMethodExecutor;
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
	
	@Override
	public String toString(){
		return getSource();
	}
	
	public static XInstruction load(XInputStream inputStream) throws IOException {
		int instruction = inputStream.readUnsignedByte();
		switch(instruction){
		case 0: return new XInstructionAddDouble(inputStream);
		case 1: return new XInstructionAddFloat(inputStream);
		case 2: return new XInstructionAddInt(inputStream);
		case 3: return new XInstructionAddLong(inputStream);
		case 4: return new XInstructionAndBool(inputStream);
		case 5: return new XInstructionAndInt(inputStream);
		case 6: return new XInstructionAndLong(inputStream);
		case 7: return new XInstructionI2B(inputStream);
		case 8: return new XInstructionBEqDouble(inputStream);
		case 9: return new XInstructionBEqFloat(inputStream);
		case 10: return new XInstructionBEqInt(inputStream);
		case 11: return new XInstructionBEqLong(inputStream);
		case 12: return new XInstructionBigDouble(inputStream);
		case 13: return new XInstructionBigFloat(inputStream);
		case 14: return new XInstructionBigInt(inputStream);
		case 15: return new XInstructionBigLong(inputStream);
		case 16: return new XInstructionBinSwitch(inputStream);
		case 17: return new XInstructionCheckCast(inputStream);
		case 18: return new XInstructionCheckNull(inputStream);
		case 19: return new XInstructionCompBool(inputStream);
		case 20: return new XInstructionCompDouble(inputStream);
		case 21: return new XInstructionCompFloat(inputStream);
		case 22: return new XInstructionCompInt(inputStream);
		case 23: return new XInstructionCompLong(inputStream);
		case 24: return new XInstructionD2F(inputStream);
		case 25: return new XInstructionD2I(inputStream);
		case 26: return new XInstructionD2L(inputStream);
		case 27: return new XInstructionDivDouble(inputStream);
		case 28: return new XInstructionDivFloat(inputStream);
		case 29: return new XInstructionDivInt(inputStream);
		case 30: return new XInstructionDivLong(inputStream);
		case 31: return new XInstructionDup(inputStream);
		case 32: return new XInstructionEqBool(inputStream);
		case 33: return new XInstructionEqDouble(inputStream);
		case 34: return new XInstructionEqFloat(inputStream);
		case 35: return new XInstructionEqInt(inputStream);
		case 36: return new XInstructionEqLong(inputStream);
		case 37: return new XInstructionEqObject(inputStream);
		case 38: return new XInstructionF2D(inputStream);
		case 39: return new XInstructionF2I(inputStream);
		case 40: return new XInstructionF2L(inputStream);
		case 41: return new XInstructionGetField(inputStream);
		case 42: return new XInstructionGetLocalField(inputStream);
		case 43: return new XInstructionGetStaticField(inputStream);
		case 44: return new XInstructionI2D(inputStream);
		case 45: return new XInstructionI2F(inputStream);
		case 46: return new XInstructionI2L(inputStream);
		case 47: return new XInstructionIf(inputStream);
		case 48: return new XInstructionInstanceof(inputStream);
		case 49: return new XInstructionInvokeDynamic(inputStream);
		case 50: return new XInstructionInvokeSpecial(inputStream);
		case 51: return new XInstructionInvokeStatic(inputStream);
		case 52: return new XInstructionJump(inputStream);
		case 53: return new XInstructionL2D(inputStream);
		case 54: return new XInstructionL2F(inputStream);
		case 55: return new XInstructionL2I(inputStream);
		case 56: return new XInstructionLoadConstBool(inputStream);
		case 57: return new XInstructionLoadConstDouble(inputStream);
		case 58: return new XInstructionLoadConstFloat(inputStream);
		case 59: return new XInstructionLoadConstInt(inputStream);
		case 60: return new XInstructionLoadConstLong(inputStream);
		case 61: return new XInstructionLoadConstNull(inputStream);
		case 62: return new XInstructionLoadConstString(inputStream);
		case 63: return new XInstructionModDouble(inputStream);
		case 64: return new XInstructionModFloat(inputStream);
		case 65: return new XInstructionModInt(inputStream);
		case 66: return new XInstructionModLong(inputStream);
		case 67: return new XInstructionMonitorEnter(inputStream);
		case 68: return new XInstructionMonitorExit(inputStream);
		case 69: return new XInstructionMulDouble(inputStream);
		case 70: return new XInstructionMulFloat(inputStream);
		case 71: return new XInstructionMulInt(inputStream);
		case 72: return new XInstructionMulLong(inputStream);
		case 73: return new XInstructionNegDouble(inputStream);
		case 74: return new XInstructionNegFloat(inputStream);
		case 75: return new XInstructionNegInt(inputStream);
		case 76: return new XInstructionNegLong(inputStream);
		case 77: return new XInstructionNEqBool(inputStream);
		case 78: return new XInstructionNEqDouble(inputStream);
		case 79: return new XInstructionNEqFloat(inputStream);
		case 80: return new XInstructionNEqInt(inputStream);
		case 81: return new XInstructionNEqLong(inputStream);
		case 82: return new XInstructionNEqObject(inputStream);
		case 83: return new XInstructionNew(inputStream);
		case 84: return new XInstructionNewArray(inputStream);
		case 85: return new XInstructionNIf(inputStream);
		case 86: return new XInstructionNop(inputStream);
		case 87: return new XInstructionNotBool(inputStream);
		case 88: return new XInstructionNotInt(inputStream);
		case 89: return new XInstructionNotLong(inputStream);
		case 90: return new XInstructionODup(inputStream);
		case 91: return new XInstructionOPop(inputStream);
		case 92: return new XInstructionOrBool(inputStream);
		case 93: return new XInstructionOrInt(inputStream);
		case 94: return new XInstructionOrLong(inputStream);
		case 95: return new XInstructionOToTop(inputStream);
		case 96: return new XInstructionPop(inputStream);
		case 97: return new XInstructionPowDouble(inputStream);
		case 98: return new XInstructionPowFloat(inputStream);
		case 99: return new XInstructionPowInt(inputStream);
		case 100: return new XInstructionPowLong(inputStream);
		case 101: return new XInstructionReadLocal(inputStream);
		case 102: return new XInstructionReturn(inputStream);
		case 103: return new XInstructionI2S(inputStream);
		case 104: return new XInstructionSEqDouble(inputStream);
		case 105: return new XInstructionSEqFloat(inputStream);
		case 106: return new XInstructionSEqInt(inputStream);
		case 107: return new XInstructionSEqLong(inputStream);
		case 108: return new XInstructionSetField(inputStream);
		case 109: return new XInstructionSetLocalField(inputStream);
		case 110: return new XInstructionSetStaticField(inputStream);
		case 111: return new XInstructionShlInt(inputStream);
		case 112: return new XInstructionShlLong(inputStream);
		case 113: return new XInstructionShrInt(inputStream);
		case 114: return new XInstructionShrLong(inputStream);
		case 115: return new XInstructionSmaDouble(inputStream);
		case 116: return new XInstructionSmaFloat(inputStream);
		case 117: return new XInstructionSmaInt(inputStream);
		case 118: return new XInstructionSmaLong(inputStream);
		case 119: return new XInstructionSubDouble(inputStream);
		case 120: return new XInstructionSubFloat(inputStream);
		case 121: return new XInstructionSubInt(inputStream);
		case 122: return new XInstructionSubLong(inputStream);
		case 123: return new XInstructionTableSwitch(inputStream);
		case 124: return new XInstructionThrow(inputStream);
		case 125: return new XInstructionToTop(inputStream);
		case 126: return new XInstructionVarJump(inputStream);
		case 127: return new XInstructionWriteLocal(inputStream);
		case 128: return new XInstructionXorInt(inputStream);
		case 129: return new XInstructionXorLong(inputStream);
		case 130: return new XInstructionSetReturn(inputStream);
		case 131: return new XInstructionSwap(inputStream);
		case 132: return new XInstructionOSwap(inputStream);
		case 133: return new XInstructionStackSizeSave(inputStream);
		case 134: return new XInstructionLoadConstClass(inputStream);
		case 135: return new XInstructionInvokeConstructor(inputStream);
		case 136: return new XInstructionStringSwitch(inputStream);
		}
		throw new XRuntimeException("Unknow instruction %s", instruction);
	}
	
	public static void save(XOutputStream outputStream, XInstruction instruction) throws IOException{
		Class<? extends XInstruction> c = instruction.getClass();
		for(int i=0; i<instructions.length; i++){
			if(instructions[i] == c){
				outputStream.writeByte(i);
				instruction.save(outputStream);
				return;
			}
		}
		throw new XRuntimeException("Unknown instruction type %s", instruction.getClass());
	}
	
}
