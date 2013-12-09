package xscript.compiler;

import java.util.Arrays;

public class XMessagePrinter implements XMessageList {

	@Override
	public void postMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object[] args) {
		System.err.println(level+":"+lineDesk+":"+key+":"+Arrays.toString(args));
	}

}
