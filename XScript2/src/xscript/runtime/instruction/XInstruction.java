package xscript.runtime.instruction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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
		instructions[7] = XInstructionB2I.class;
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
		instructions[103] = XInstructionS2I.class;
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
	}
	
	public abstract void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor);

	protected abstract void save(XOutputStream outputStream) throws IOException;

	public abstract String getSource();

	@Override
	public String toString(){
		return getSource();
	}
	
	public static XInstruction load(XInputStream inputStream) throws IOException {
		int instruction = inputStream.readUnsignedByte();
		Class<? extends XInstruction> c = instructions[instruction];
		if(c==null){
			throw new XRuntimeException("Unknow instruction %s", instruction);
		}
		try {
			return c.getConstructor(XInputStream.class).newInstance(inputStream);
		} catch (InvocationTargetException e) {
			Throwable e1 = e.getTargetException();
			if(e1 instanceof IOException){
				throw (IOException)e1;
			}else if(e1 instanceof RuntimeException){
				throw (RuntimeException)e1;
			}
			throw new XRuntimeException(e1, "Error while creating instruction %s", instruction);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XRuntimeException(e, "Error while creating instruction %s", instruction);
		} 
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
