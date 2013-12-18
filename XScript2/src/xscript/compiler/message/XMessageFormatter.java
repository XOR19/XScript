package xscript.compiler.message;

import java.util.Arrays;

public class XMessageFormatter {

	public String format(XMessageElement messageElement){
		return messageElement.level +":"+messageElement.className+":"+messageElement.lineDesk+":"+messageElement.key+":"+Arrays.toString(messageElement.args);
	}
	
}
