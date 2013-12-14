package xscript.compiler;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XLineDesk;

public class XMessageClass implements XMessageList{

	private XCompiler compiler;
	
	private String className;
	
	public XMessageClass(XCompiler compiler, String className) {
		this.compiler = compiler;
		this.className = className;
	}

	@Override
	public void postMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object[] args) {
		compiler.postMessage(level, className, key, lineDesk, args);
	}
	
}