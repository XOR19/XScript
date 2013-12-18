package xscript.compiler;

public class XConstantValue {
	
	private final Object value;
	
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
	
	public int getInt(){
		return (Integer)value;
	}
	
	public long getLong(){
		if(value instanceof Integer){
			return (Integer)value;
		}
		return (Long)value;
	}
	
	public float getFloat(){
		if(value instanceof Integer){
			return (Integer)value;
		}else if(value instanceof Long){
			return (Long)value;
		}
		return (Float)value;
	}
	
	public double getDouble(){
		if(value instanceof Integer){
			return (Integer)value;
		}else if(value instanceof Long){
			return (Long)value;
		}else if(value instanceof Float){
			return (Float)value;
		}
		return (Double)value;
	}
	
	public boolean getBool(){
		return (Boolean)value;
	}
	
	public String getString(){
		return value.toString();
	}
	
	public Character getChar(){
		return (Character)value;
	}
	
	public Class<?> getType(){
		return value.getClass();
	}
	
	public XConstantValue add(XConstantValue other){
		if(isBool()){
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
		throw new ClassCastException();
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
		throw new ClassCastException();
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
		throw new ClassCastException();
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
		throw new ClassCastException();
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
		throw new ClassCastException();
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
		throw new ClassCastException();
	}
	
	public XConstantValue shl(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()<<other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()<<other.getLong());
		}
		throw new ClassCastException();
	}
	
	public XConstantValue shr(XConstantValue other){
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt()>>other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong()>>other.getLong());
		}
		throw new ClassCastException();
	}
	
	public XConstantValue and(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() & other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt() & other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong() & other.getLong());
		}
		throw new ClassCastException();
	}
	
	public XConstantValue or(XConstantValue other){
		if(isBool()){
			return new XConstantValue(getBool() | other.getBool());
		}
		int numberid = compNID(numberid(), other.numberid());
		if(numberid==1){
			return new XConstantValue(getInt() | other.getInt());
		}else if(numberid==2){
			return new XConstantValue(getLong() | other.getLong());
		}
		throw new ClassCastException();
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
		throw new ClassCastException();
	}
	
	private int numberid(){
		if(value instanceof Integer){
			return 1;
		}else if(value instanceof Long){
			return 2;
		}else if(value instanceof Float){
			return 3;
		}else if(value instanceof Double){
			return 4;
		}
		return 0;
	}
	
	private static int compNID(int nid1, int nid2){
		if(nid1>0 && nid2>0){
			if(nid1<nid2)
				return nid2;
			return nid1;
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
	
}
