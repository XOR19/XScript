package xscript.runtime.clazz;

import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.object.XObjectProvider;

public class XWrapper {

	public static Object getJavaObject(XObjectProvider objectProvider, XGenericClass genericClass, long value){
		return getJavaObject(objectProvider, XPrimitive.getPrimitiveID(genericClass.getXClass()), value);
	}
	
	public static Object getJavaObject(XObjectProvider objectProvider, int primitive, long value){
		switch(primitive){
		case XPrimitive.OBJECT:
			XObject obj = objectProvider.getObject(value);
			if(obj.getXClass().getXClass().getName().equals("xscript.lang.String")){
				return objectProvider.getString(obj);
			}
			return obj;
		case XPrimitive.BOOL:
			return value!=0;
		case XPrimitive.BYTE:
			return (byte)value;
		case XPrimitive.CHAR:
			return (char)value;
		case XPrimitive.SHORT:
			return (short)value;
		case XPrimitive.INT:
			return (int)value;
		case XPrimitive.LONG:
			return value;
		case XPrimitive.FLOAT:
			return Float.intBitsToFloat((int)value);
		case XPrimitive.DOUBLE:
			return Double.longBitsToDouble(value);
		}
		return null;
	}
	
	public static long getXObject(XObjectProvider objectProvider, XGenericClass genericClass, Object value){
		return getXObject(objectProvider, XPrimitive.getPrimitiveID(genericClass.getXClass()), value);
	}
	
	public static long getXObject(XObjectProvider objectProvider, int primitive, Object value){
		switch(primitive){
		case XPrimitive.OBJECT:
			if(value instanceof String){
				return objectProvider.createString((String)value);
			}
			return objectProvider.getPointer((XObject)value);
		case XPrimitive.BOOL:
			return castToBoolean(value)?-1:0;
		case XPrimitive.BYTE:
			return castToByte(value);
		case XPrimitive.CHAR:
			return castToChar(value);
		case XPrimitive.SHORT:
			return castToShort(value);
		case XPrimitive.INT:
			return castToInt(value);
		case XPrimitive.LONG:
			return castToLong(value);
		case XPrimitive.FLOAT:
			return Float.floatToIntBits(castToFloat(value));
		case XPrimitive.DOUBLE:
			return Double.doubleToLongBits(castToDouble(value));
		}
		return 0;
	}
	
	public static boolean castToBoolean(Object obj){
		return (Boolean)obj;
	}
	
	public static char castToChar(Object obj){
		return (Character)obj;
	}
	
	public static byte castToByte(Object obj){
		return (Byte)obj;
	}
	
	public static short castToShort(Object obj){
		if(obj instanceof Byte){
			return (Byte)obj;
		}
		return (Short)obj;
	}
	
	public static int castToInt(Object obj){
		if(obj instanceof Byte){
			return (Byte)obj;
		}else if(obj instanceof Short){
			return (Short)obj;
		}
		return (Integer)obj;
	}
	
	public static long castToLong(Object obj){
		if(obj instanceof Byte){
			return (Byte)obj;
		}else if(obj instanceof Short){
			return (Short)obj;
		}else if(obj instanceof Integer){
			return (Integer)obj;
		}
		return (Long)obj;
	}
	
	public static float castToFloat(Object obj){
		if(obj instanceof Byte){
			return (Byte)obj;
		}else if(obj instanceof Short){
			return (Short)obj;
		}else if(obj instanceof Integer){
			return (Integer)obj;
		}else if(obj instanceof Long){
			return (Long)obj;
		}
		return (Float)obj;
	}
	
	public static double castToDouble(Object obj){
		if(obj instanceof Byte){
			return (Byte)obj;
		}else if(obj instanceof Short){
			return (Short)obj;
		}else if(obj instanceof Integer){
			return (Integer)obj;
		}else if(obj instanceof Long){
			return (Long)obj;
		}else if(obj instanceof Float){
			return (Float)obj;
		}
		return (Double)obj;
	}
	
}
