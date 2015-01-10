package xscript.object;

public interface XConstPool {

	public int getIntP(int index);
	
	public long getLongP(int index);
	
	public float getFloatP(int index);
	
	public double getDoubleP(int index);
	
	public String getStringP(int index);

	public byte[] getBytes(int index);
	
}
