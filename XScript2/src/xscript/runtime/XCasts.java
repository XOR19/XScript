package xscript.runtime;

public class XCasts {

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
