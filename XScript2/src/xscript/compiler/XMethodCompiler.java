package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTree.XAnnotation;
import xscript.compiler.tree.XTree.XArrayInitialize;
import xscript.compiler.tree.XTree.XBlock;
import xscript.compiler.tree.XTree.XBreak;
import xscript.compiler.tree.XTree.XCase;
import xscript.compiler.tree.XTree.XCast;
import xscript.compiler.tree.XTree.XCatch;
import xscript.compiler.tree.XTree.XClassDecl;
import xscript.compiler.tree.XTree.XClassFile;
import xscript.compiler.tree.XTree.XConstant;
import xscript.compiler.tree.XTree.XContinue;
import xscript.compiler.tree.XTree.XDo;
import xscript.compiler.tree.XTree.XError;
import xscript.compiler.tree.XTree.XFor;
import xscript.compiler.tree.XTree.XForeach;
import xscript.compiler.tree.XTree.XGroup;
import xscript.compiler.tree.XTree.XIdent;
import xscript.compiler.tree.XTree.XIf;
import xscript.compiler.tree.XTree.XIfOperator;
import xscript.compiler.tree.XTree.XImport;
import xscript.compiler.tree.XTree.XIndex;
import xscript.compiler.tree.XTree.XLable;
import xscript.compiler.tree.XTree.XLambda;
import xscript.compiler.tree.XTree.XMethodCall;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XModifier;
import xscript.compiler.tree.XTree.XNew;
import xscript.compiler.tree.XTree.XNewArray;
import xscript.compiler.tree.XTree.XOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XOperatorStatement;
import xscript.compiler.tree.XTree.XReturn;
import xscript.compiler.tree.XTree.XSwitch;
import xscript.compiler.tree.XTree.XSynchronized;
import xscript.compiler.tree.XTree.XThrow;
import xscript.compiler.tree.XTree.XTry;
import xscript.compiler.tree.XTree.XType;
import xscript.compiler.tree.XTree.XTypeParam;
import xscript.compiler.tree.XTree.XVarDecl;
import xscript.compiler.tree.XTree.XVarDecls;
import xscript.compiler.tree.XTree.XWhile;
import xscript.compiler.tree.XVisitor;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.method.XMethod;

public class XMethodCompiler extends XMethod implements XVisitor {

	private XMethodDecl xMethodDecl;
	
	private List<XClass> classes;
	
	private XImportHelper importHelper;
	
	private List<List<XVariable>> scopes;
	
	private XClassPtr instRetExpect;
	
	private XClassPtr instRetType;
	
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
		List<XVariable> var = new ArrayList<XVariable>();
		classes = new ArrayList<XClass>();
		scopes = new ArrayList<List<XVariable>>();
		
		scopes.add(var);
		visitTree(xMethodDecl.block.statements);
		scopes = null;
		classes = null;
	}
	
	protected void visitTree(XTree tree){
		if(tree!=null){
			tree.accept(this);
		}
	}
	
	protected void visitTree(List<? extends XTree> tree){
		if(tree!=null){
			for(XTree t:tree){
				visitTree(t);
			}
		}
	}
	
	@Override
	public void visitTopLevel(XClassFile xClassFile) {
		shouldNeverCalled();
	}

	@Override
	public void visitImport(XImport xImport) {
		shouldNeverCalled();
	}

	@Override
	public void visitClassDecl(XClassDecl xClassDef) {
		XCompiler compiler = (XCompiler) getDeclaringClass().getVirtualMachine();
		XClassCompiler classCompiler = new XClassCompiler(compiler, xClassDef.name, new XMessageClass(compiler, getName()+"."+xClassDef.name), importHelper);
		if(childs.containsKey(xClassDef.name)){
			
		}else{
			classes.add(classCompiler);
			childs.put(xClassDef.name, classCompiler);
		}
		xClassDef.accept(classCompiler);
	}

	@Override
	public void visitAnnotation(XAnnotation xAnnotation) {
		shouldNeverCalled();
	}

	@Override
	public void visitModifier(XModifier xModifier) {
		shouldNeverCalled();
	}

	@Override
	public void visitError(XError xError) {
		shouldNeverCalled();
	}

	@Override
	public void visitIdent(XIdent xIdent) {
		
	}

	@Override
	public void visitType(XType xType) {
		shouldNeverCalled();
	}

	@Override
	public void visitTypeParam(XTypeParam xTypeParam) {
		shouldNeverCalled();
	}

	@Override
	public void visitVarDecl(XVarDecl xVarDecl) {
		
	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		shouldNeverCalled();
	}

	@Override
	public void visitBlock(XBlock xBlock) {
		scopes.add(0, new ArrayList<XVariable>());
		visitTree(xBlock);
		scopes.remove(0);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDo(XDo xDo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitWhile(XWhile xWhile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitFor(XFor xFor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIf(XIf xIf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XGroup xGroup) {
		visitTree(xGroup.statement);
	}

	@Override
	public void visitSynchronized(XSynchronized xSynchroized) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstant(XConstant xConstant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNew(XNew xNew) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCast(XCast xCast) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLambda(XLambda xLambda) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTry(XTry xTry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCatch(XCatch xCatch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNewArray(XNewArray xNewArray) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitForeach(XForeach xForeach) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visitLable(XLable xLable) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void visitSwitch(XSwitch xSwitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCase(XCase xCase) {
		// TODO Auto-generated method stub
		
	}
	
	private void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
}
