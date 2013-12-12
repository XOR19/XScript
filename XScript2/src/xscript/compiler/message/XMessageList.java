package xscript.compiler.message;

import xscript.compiler.token.XLineDesk;


public interface XMessageList {

	public void postMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object[] args);

}
