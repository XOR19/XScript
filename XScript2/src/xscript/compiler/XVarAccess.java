package xscript.compiler;

import xscript.compiler.tree.XTree;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.genericclass.XClassPtr;

public class XVarAccess {

	public XCodeGen codeGen;
	public String name;
	public String className;
	public XClassPtr declaringClass;
	public boolean isStatic;
	public boolean specialInvoke;
	public XVariable variable;
	public XTree tree;
	public XCodeGen index;
	public XPackage p;
	
}
