package xscript.compiler;

import xscript.compiler.tree.XTree.XVarDecl;
import xscript.runtime.genericclass.XClassPtr;


public class XVariable {

	public XVarDecl varDecl;
	
	public int modifier;
	
	public XClassPtr type;
	
	public String name;

	public int id;
	
}
