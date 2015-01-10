package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.Arrays;

public class XModule implements XConstPool {

	private int[] ints;
	
	private long[] longs;
	
	private float[] floats;
	
	private double[] doubles;
	
	private String[] strings;
	
	private byte[][] bytes;
	
	public XModule(ObjectInput in) throws IOException{
		int size = in.readUnsignedShort();
		ints = new int[size];
		for(int i=0; i<size; i++){
			ints[i] = in.readInt();
		}
		size = in.readUnsignedShort();
		longs = new long[size];
		for(int i=0; i<size; i++){
			longs[i] = in.readLong();
		}
		size = in.readUnsignedShort();
		floats = new float[size];
		for(int i=0; i<size; i++){
			floats[i] = in.readFloat();
		}
		size = in.readUnsignedShort();
		doubles = new double[size];
		for(int i=0; i<size; i++){
			doubles[i] = in.readDouble();
		}
		size = in.readUnsignedShort();
		strings = new String[size];
		for(int i=0; i<size; i++){
			strings[i] = in.readUTF();
		}
		size = in.readUnsignedShort();
		bytes = new byte[size][];
		for(int i=0; i<size; i++){
			int size2 = in.readUnsignedShort();
			byte[] b = new byte[size2];
			int readed = size2==0?0:in.read(b);
			if(readed!=size2){
				throw new IOException("Expected "+size2+" bytes but got "+readed);
			}
			bytes[i] = b;
		}
	}
	
	@Override
	public int getIntP(int index) {
		return ints[index];
	}

	@Override
	public long getLongP(int index) {
		return longs[index];
	}

	@Override
	public float getFloatP(int index) {
		return floats[index];
	}

	@Override
	public double getDoubleP(int index) {
		return doubles[index];
	}

	@Override
	public String getStringP(int index) {
		return strings[index];
	}

	@Override
	public byte[] getBytes(int index) {
		return bytes[index];
	}

	@Override
	public String toString() {
		return "XModule [ints=" + Arrays.toString(ints) + ", longs="
				+ Arrays.toString(longs) + ", floats="
				+ Arrays.toString(floats) + ", doubles="
				+ Arrays.toString(doubles) + ", strings="
				+ Arrays.toString(strings) + ", bytes="
				+ Arrays.deepToString(bytes) + "]";
	}
	
}
