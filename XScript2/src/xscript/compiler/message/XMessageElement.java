package xscript.compiler.message;

import xscript.compiler.token.XLineDesk;


public class XMessageElement {

	private static XMessageFormatter defaultFormatter = new XMessageFormatter();
	
	public XMessageLevel level;
	public String className;
	public XLineDesk lineDesk;
	public String key;
	public Object[] args;
	
	public XMessageElement(XMessageLevel level, String className, XLineDesk lineDesk, String key, Object[] args) {
		this.level = level;
		this.className = className;
		this.lineDesk = lineDesk;
		this.key = key;
		this.args = args;
	}
	
	@Override
	public String toString(){
		return defaultFormatter.format(this);
	}
	
}
