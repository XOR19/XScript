package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XType;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.method.XMethod;

public class XMethodCompiler extends XMethod {

	private XMethodDecl xMethodDecl;
	
	private List<XClass> classes;
	
	private XImportHelper importHelper;
	
	public XMethodCompiler(XClass declaringClass, int modifier, String name,
			XClassPtr returnType, xscript.runtime.XAnnotation[] annotations,
			XClassPtr[] params, xscript.runtime.XAnnotation[][] paramAnnotations,
			XClassPtr[] mThrows, XGenericInfo[] genericInfos, XMethodDecl xMethodDecl, 
			XImportHelper importHelper) {
		super(declaringClass, modifier, name, returnType, annotations, params,
				paramAnnotations, mThrows, genericInfos);
		this.xMethodDecl = xMethodDecl;
		this.importHelper = importHelper;
	}

	public void gen(){
		if(xMethodDecl.block==null || xscript.runtime.XModifier.isAbstract(modifier) || xscript.runtime.XModifier.isNative(modifier))
			return;
		classes = new ArrayList<XClass>();
		XStatementCompiler statementCompiler = new XStatementCompiler(null, null, this);
		xMethodDecl.block.accept(statementCompiler);
		classes = null;
		XCodeGen codeGen = statementCompiler.getCodeGen();
		System.out.println(getName());
		codeGen.generateFinalCode();
		instructions = codeGen.getInstructions();
		lineEntries = codeGen.getLineEntries();
		catchEntries = codeGen.getCatchEntries();
		localEntries = codeGen.getLocalEntries();
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		Object[] o = new Object[args.length+1];
		o[0] = this;
		System.arraycopy(args, 0, o, 1, args.length);
		((XClassCompiler)getDeclaringClass()).compilerError(level, "method."+key, lineDesk, o);
	} 
	
	public void addClass(XClass c, XLineDesk line){
		if(childs.containsKey(c.getSimpleName())){
			compilerError(XMessageLevel.ERROR, "class.duplicated", line, c.getSimpleName());
		}else{
			childs.put(c.getSimpleName(), c);
			classes.add(c);
		}
	}
	
	public XClassPtr getGenericClass(XType type, boolean doError){
		return importHelper.getGenericClass((XClassCompiler)getDeclaringClass(), type, genericInfos, doError);
	}

	public XImportHelper getImportHelper() {
		return importHelper;
	}

	public XClassPtr getGenericReturnType() {
		return returnType;
	}

	public XMethodDecl getMethodDecl() {
		return xMethodDecl;
	}

	public XClassPtr getDeclaringClassGen() {
		XClass c = getDeclaringClass();
		String name = c.getName();
		XClassPtr generics[] = new XClassPtr[c.getGenericParams()];
		for(int i=0; i<generics.length; i++){
			generics[i] = new XClassPtrClassGeneric(name, c.getGenericName(i));
		}
		return new XClassPtrGeneric(name, generics);
	}
	
}
