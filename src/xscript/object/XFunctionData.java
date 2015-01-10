package xscript.object;

public class XFunctionData {

	final XFunction function;
	
	final String[] paramNames;
	
	public XFunctionData(XFunction function, String...paramNames){
		this.function = function;
		this.paramNames = paramNames;
	}
	
}
