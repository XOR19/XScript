package xscript.compiler;

import xscript.compiler.classtypes.XVarType;
import xscript.compiler.tree.XTree;
import xscript.runtime.clazz.XPackage;

public class XVarAccess {

	public XCodeGen codeGen;
	public String name;
	public String className;
	public XVarType declaringClass;
	public boolean isStatic;
	public boolean specialInvoke;
	public XVariable variable;
	public XTree tree;
	public XCodeGen index;
	public XPackage p;
	
}
