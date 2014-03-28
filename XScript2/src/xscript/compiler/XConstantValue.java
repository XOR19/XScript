package xscript.compiler;

public class XConstantValue {
	
	private final Object value;
	
	public XConstantValue(byte value){
		this.value = value;
	}
	
	public XConstantValue(short value){
		this.value = value;
	}
	
	public XConstantValue(int value){
		this.value = value;
	}
	
	public XConstantValue(long value){
		this.value = value;
	}
	
	public XConstantValue(float value){
		this.value = value;
	}
	
	public XConstantValue(double value){
		this.value = value;
	}
	
	public XConstantValue(boolean value){
		this.value = value;
	}
	
	public XConstantValue(String value){
		this.value = value;
	}
	
	public XConstantValue(char value){
		this.value = value;
	}
	
	public byte getByte(){
		if(value instanceof Byte){
			return (Byte)value;
		}else if(value instanceof Character){
			return (byte)(char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" not compatible with byte");
	}
	
	public short getShort(){
		if(value instanceof Byte){
			return (byte)(Byte)value;
		}else if(value instanceof Short){
			return (Short)value;
		}else if(value instanceof Character){
			return (short)(char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" can't be cast to short");
	}
	
	public int getInt(){
		if(value instanceof Byte){
			return (byte)(Byte)value;
		}else if(value instanceof Short){
			return (short)(Short)value;
		}else if(value instanceof Integer){
			return (Integer)value;
		}else if(value instanceof Character){
			return (char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" can't be cast to int");
	}
	
	public long getLong(){
		if(value instanceof Byte){
			return (byte)(Byte)value;
		}else if(value instanceof Short){
			return (short)(Short)value;
		}else if(value instanceof Integer){
			return (int)(Integer)value;
		}else if(value instanceof Long){
			return (Long)value;
		}else if(value instanceof Character){
			return (char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" can't be cast to long");
	}
	
	public float getFloat(){
		if(value instanceof Byte){
			return (byte)(Byte)value;
		}else if(value instanceof Short){
			return (short)(Short)value;
		}else if(value instanceof Integer){
			return (int)(Integer)value;
		}else if(value instanceof Long){
			return (long)(Long)value;
		}else if(value instanceof Float){
			return (Float)value;
		}else if(value instanceof Character){
			return (char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" can't be cast to float");
	}
	
	public double getDouble(){
		if(value instanceof Byte){
			return (byte)(Byte)value;
		}else if(value instanceof Short){
			return (short)(Short)value;
		}else if(value instanceof Integer){
			return (int)(Integer)value;
		}else if(value instanceof Long){
			return (long)(Long)value;
		}else if(value instanceof Float){
			return (float)(Float)value;
		}else if(value instanceof Double){
			return (Double)value;
		}else if(value instanceof Character){
			return (char)(Character)value;
		}
		throw new RuntimeException(getTypeName()+" can't be cast to double");
	}
	
	public boolean getBool(){
		if(value instanceof Boolean)
			return (Boolean)value;
		throw new RuntimeException(getTypeName()+" can't be cast to bool");
	}
	
	public String getString(){
		if(value instanceof String)
			return (String)value;
		throw new RuntimeException(getTypeName()+" can't be cast to xscript.lang.String");
	}
	
	public char getChar(){
		if(value instanceof Character)
			return (Character)value;
		throw new RuntimeException(getTypeName()+" can't be cast to char");
	}
	
	public Class<?> getType(){
		if(value==null)
			return null;
		return value.getClass();
	}
	
	public XConstantValue add(XConstantValue other){
		if(isString()){
			return new XConstantValue((String)value+other);
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()+other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()+other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()+other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()+other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue sub(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()-other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()-other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()-other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()-other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue mul(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()*other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()*other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()*other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()*other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue div(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()/other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()/other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()/other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()/other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue mod(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()%other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()%other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()%other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()%other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue neg(){
		int numberid = numberid();
		if(numberid==1){
			return new XConstantValue(-getInt());
		}else if(numberid==2){
			return new XConstantValue(-getLong());
		}else if(numberid==3){
			return new XConstantValue(-getFloat());
		}else if(numberid==4){
			return new XConstantValue(-getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public XConstantValue shl(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()<<other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()<<other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue shr(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()>>other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()>>other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue and(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() && other.getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue band(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() & other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt() & other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong() & other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue or(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() || other.getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue bor(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() | other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt() | other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong() | other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue xor(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() ^ other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt() ^ other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong() ^ other.getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue not(){
		if(isBool()){
			return new XConstantValue(!getBool());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public XConstantValue bnot(){
		if(isBool()){
			return new XConstantValue(!getBool());
		}
		int numberid = numberid();
		if(numberid==1){
			return new XConstantValue(~getInt());
		}else if(numberid==2){
			return new XConstantValue(~getLong());
		}
		throw new RuntimeException(getTypeName()+" not compatible");
	}
	
	public XConstantValue pow(XConstantValue other) {
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue((int)Math.pow(getInt(), other.getInt()));
		}else if(numberid==2){
			return new XConstantValue((long)Math.pow(getLong(), other.getLong()));
		}else if(numberid==3){
			return new XConstantValue((float)Math.pow(getFloat(), other.getFloat()));
		}else if(numberid==4){
			return new XConstantValue(Math.pow(getDouble(), other.getDouble()));
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue eq(XConstantValue other) {
		if(isBool()){
			return new XConstantValue(getBool() == other.getBool());
		}else if(isString()){
			return new XConstantValue(getString().equals(other.getString()));
		}else if(isChar()){
			return new XConstantValue(getChar() == other.getChar());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()==other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()==other.getLong());
		}else if(numberid==3){
			return new XConstantValue(getFloat()==other.getFloat());
		}else if(numberid==4){
			return new XConstantValue(getDouble()==other.getDouble());
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue comp(XConstantValue other) {
		if(isChar()){
			return new XConstantValue(getChar() == other.getChar()?0:getChar()>other.getChar()?1:-1);
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()==other.getInt()?0:getInt()>other.getInt()?1:-1);
		}else if(numberid==2){
			return new XConstantValue(getLong()==other.getLong()?0:getLong()>other.getLong()?1:-1);
		}else if(numberid==3){
			return new XConstantValue(getFloat()==other.getFloat()?0:getFloat()>other.getFloat()?1:-1);
		}else if(numberid==4){
			return new XConstantValue(getDouble()==other.getDouble()?0:getDouble()>other.getDouble()?1:-1);
		}
		throw new RuntimeException(getTypeName()+" not compatible with "+other.getTypeName());
	}
	
	public XConstantValue castTo(Class<?> c) {
		Class<?> t = getType();
		if(c==Boolean.class){
			return new XConstantValue(getBool());
		}else if(c==Character.class){
			if(t==Integer.class){
				return new XConstantValue((char)(int)(Integer)value);
			}
			return new XConstantValue(getChar());
		}else if(c==Byte.class){
			return new XConstantValue(getInt());
		}else if(c==Short.class){
			return new XConstantValue(getInt());
		}else if(c==Integer.class){
			if(t==Long.class){
				return new XConstantValue((int)(long)(Long)value);
			}else if(t==Float.class){
				return new XConstantValue((int)(float)(Float)value);
			}else if(t==Double.class){
				return new XConstantValue((int)(double)(Double)value);
			}else if(t==Character.class){
				return new XConstantValue((int)(char)(Character)value);
			}
			return new XConstantValue(getInt());
		}else if(c==Long.class){
			if(t==Float.class){
				return new XConstantValue((long)(float)(Float)value);
			}else if(t==Double.class){
				return new XConstantValue((long)(double)(Double)value);
			}
			return new XConstantValue(getLong());
		}else if(c==Float.class){
			if(t==Double.class){
				return new XConstantValue((long)(double)(Double)value);
			}
			return new XConstantValue(getFloat());
		}else if(c==Double.class){
			return new XConstantValue(getDouble());
		}
		return null;
	}
	
	private int numberid(){
		if(value instanceof Character){
			return 0;
		}else if(value instanceof Byte){
			return 1;
		}else if(value instanceof Short){
			return 1;
		}else if(value instanceof Integer){
			return 1;
		}else if(value instanceof Long){
			return 2;
		}else if(value instanceof Float){
			return 3;
		}else if(value instanceof Double){
			return 4;
		}
		return -1;
	}
	
	private static int compNID(int nid1, int nid2){
		if(nid1>0 && nid2>0){
			if(nid1<nid2)
				return nid2;
			return nid1;
		}else if(nid1>0 && nid2==0){
			return nid1;
		}else if(nid1==0 && nid2>0){
			return nid2;
		}
		return 0;
	}
	
	public boolean isBool(){
		return value instanceof Boolean;
	}
	
	public boolean isString(){
		return value instanceof String;
	}
	
	public boolean isChar(){
		return value instanceof Character;
	}

	@Override
	public String toString() {
		return value==null?"null":value.toString();
	}
	
	public String getTypeName(){
		Class<?> c = getType();
		if(c==null){
			return "null";
		}else if(c==Boolean.class){
			return "bool";
		}else if(c==Character.class){
			return "char";
		}else if(c==Integer.class){
			return "int";
		}else if(c==Long.class){
			return "long";
		}else if(c==Float.class){
			return "float";
		}else if(c==Double.class){
			return "double";
		}else if(c==String.class){
			return "xscript.lang.String";
		}else{
			throw new AssertionError();
		}
	}
	
}
