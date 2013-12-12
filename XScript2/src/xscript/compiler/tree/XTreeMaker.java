package xscript.compiler.tree;

import xscript.compiler.message.XMessageList;

public interface XTreeMaker {
	
	public XTree makeTree(String source, XMessageList messages);
	
}
