package xscript.compiler.standart;

import xscript.compiler.message.XMessageList;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTreeMaker;

public class XStandartTreeMaker implements XTreeMaker {

	@Override
	public XTree makeTree(String source, XMessageList messages) {
		XLexer lexer = new XLexer(source, messages);
		XParser parser = new XParser(lexer, messages);
		return parser.makeTree();
	}
	
}
