package xscript.compiler.message;

import java.util.Arrays;

import xscript.compiler.token.XLineDesk;

public class XMessagePrinter implements XMessageList {

	@Override
	public void postMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object[] args) {
		System.err.println(level+":"+lineDesk+":"+key+":"+Arrays.toString(args));
	}

}
