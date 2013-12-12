package xscript.compiler.tree;

import java.util.List;

import xscript.compiler.tree.XTree.XAnnotation;
import xscript.compiler.tree.XTree.XArrayInitialize;
import xscript.compiler.tree.XTree.XBlock;
import xscript.compiler.tree.XTree.XBreak;
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
import xscript.compiler.tree.XTree.XLambda;
import xscript.compiler.tree.XTree.XMethodCall;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XModifier;
import xscript.compiler.tree.XTree.XNew;
import xscript.compiler.tree.XTree.XNewArray;
import xscript.compiler.tree.XTree.XOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XOperatorStatement;
import xscript.compiler.tree.XTree.XReturn;
import xscript.compiler.tree.XTree.XSynchroized;
import xscript.compiler.tree.XTree.XThrow;
import xscript.compiler.tree.XTree.XTry;
import xscript.compiler.tree.XTree.XType;
import xscript.compiler.tree.XTree.XTypeParam;
import xscript.compiler.tree.XTree.XVarDecl;
import xscript.compiler.tree.XTree.XVarDecls;
import xscript.compiler.tree.XTree.XWhile;

public class XTreePrinter implements XVisitor {

	private String spaces = "";
	private String add = "";
	
	private void enter(){
		spaces += " ";
	}
	
	private void leave(){
		spaces = spaces.substring(0, spaces.length()-1);
	}
	
	private void println(String s){
		if(add==null){
			System.out.println(spaces+s);
		}else{
			System.out.println(add+s);
			add=null;	
		}
	}
	
	private void print(String s){
		if(add==null)
			add=spaces;
		add += s;
	}
	
	private void accept(String name, List<? extends XTree> tree){
		if(tree!=null && !tree.isEmpty()){
			print(name+"=[");
			enter();
			for(XTree t:tree){
				if(t!=null){
					println(t.getTag()+"{");
					enter();
					t.accept(this);
					leave();
					print("}");
				}
			}
			leave();
			println("]");
		}
	}
	
	private void accept(String name, XTree tree){
		if(tree!=null){
			println(name+"="+tree.getTag()+"{");
			enter();
			tree.accept(this);
			leave();
			println("}");
		}
	}
	
	@Override
	public void visitTopLevel(XClassFile xClassFile) {
		accept("packAnnotations",xClassFile.packAnnotations);
		accept("packID",xClassFile.packID);
		accept("defs",xClassFile.defs);
	}

	@Override
	public void visitImport(XImport xImport) {
		println("import "+(xImport.staticImport?"static ":"")+xImport.iimport+(xImport.indirect?".*":""));
	}

	@Override
	public void visitClassDecl(XClassDecl xClassDef) {
		accept("modifier",xClassDef.modifier);
		println("name: "+xClassDef.name);
		accept("typeParam",xClassDef.typeParam);
		accept("superClasses",xClassDef.superClasses);
		accept("defs",xClassDef.defs);
	}

	@Override
	public void visitAnnotation(XAnnotation xAnnotation) {
		
	}

	@Override
	public void visitModifier(XModifier xModifier) {
		println(xscript.runtime.XModifier.toString(xModifier.modifier));
		accept("annotations", xModifier.annotations);
	}

	@Override
	public void visitError(XError xError) {}

	@Override
	public void visitIdent(XIdent xIdent) {
		println(xIdent.name);
	}

	@Override
	public void visitType(XType xType) {
		accept("type", xType.name);
		println("array:"+xType.array);
		accept("typeParam", xType.typeParam);
	}

	@Override
	public void visitTypeParam(XTypeParam xTypeParam) {
		accept("name",xTypeParam.name);
		println("isSuper:"+xTypeParam.isSuper);
		accept("extend", xTypeParam.extend);
	}

	@Override
	public void visitVarDecl(XVarDecl xVarDecl) {
		accept("modifier", xVarDecl.modifier);
		accept("type", xVarDecl.type);
		println("name:"+xVarDecl.name);
		accept("init", xVarDecl.init);
	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		accept("modifier", xMethodDecl.modifier);
		accept("retrun", xMethodDecl.returnType);
		println("name:"+xMethodDecl.name);
		accept("params",xMethodDecl.paramTypes);
		accept("throws", xMethodDecl.throwList);
		accept("block", xMethodDecl.block);
		accept("superConstructors", xMethodDecl.superConstructors);
		println("varargs:"+xMethodDecl.varargs);
	}

	@Override
	public void visitBlock(XBlock xBlock) {
		accept("statements", xBlock.statements);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		println("lable:"+xBreak.lable);
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		println("lable:"+xContinue.lable);
	}

	@Override
	public void visitDo(XDo xDo) {
		accept("while", xDo.doWhile);
		accept("block", xDo.block);
	}

	@Override
	public void visitWhile(XWhile xWhile) {
		accept("while", xWhile.doWhile);
		accept("block", xWhile.block);
	}

	@Override
	public void visitFor(XFor xFor) {
		accept("init", xFor.init);
		accept("while", xFor.doWhile);
		accept("inc", xFor.inc);
		accept("block", xFor.block);
	}

	@Override
	public void visitIf(XIf xIf) {
		accept("if", xIf.iif);
		accept("block", xIf.block);
		accept("block2", xIf.block2);
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		accept("statement", xReturn.statement);
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		accept("statement", xThrow.statement);
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		accept("varDecls", xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XGroup xGroup) {
		accept("group", xGroup.statement);
	}

	@Override
	public void visitThrow(XSynchroized xSynchroized) {
		accept("ident", xSynchroized.ident);
		accept("block", xSynchroized.block);
	}

	@Override
	public void visitConstant(XConstant xConstant) {
		println("value:"+xConstant.value);
	}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		accept("method",xMethodCall.method);
		accept("params", xMethodCall.params);
	}

	@Override
	public void visitNew(XNew xNew) {
		accept("className", xNew.type);
		accept("params", xNew.params);
		accept("classDecl", xNew.classDecl);
	}

	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		accept("left", xOperatorStatement.left);
		println("operator:"+xOperatorStatement.operator);
		accept("right", xOperatorStatement.right);
	}

	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		println("prefix:"+xOperatorPrefixSuffix.prefix);
		accept("statement", xOperatorPrefixSuffix.statement);
		println("suffix:"+xOperatorPrefixSuffix.suffix);
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		accept("array", xIndex.array);
		accept("index", xIndex.index);
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		accept("eq", xIfOperator.left);
		accept("then", xIfOperator.statement);
		accept("else", xIfOperator.right);
	}

	@Override
	public void visitCast(XCast xCast) {
		accept("type", xCast.type);
		accept("statement", xCast.statement);
	}

	@Override
	public void visitLambda(XLambda xLambda) {
		accept("params", xLambda.params);
		accept("statement", xLambda.statement);
	}

	@Override
	public void visitTry(XTry xTry) {
		accept("resource", xTry.resource);
		accept("block", xTry.block);
		accept("catch", xTry.catchs);
		accept("finally", xTry.finallyBlock);
	}

	@Override
	public void visitCatch(XCatch xCatch) {
		accept("modifier", xCatch.modifier);
		accept("type", xCatch.types);
		println("param:"+xCatch.varName);
		accept("block", xCatch.block);
	}

	@Override
	public void visitNewArray(XNewArray xNewArray) {
		accept("className", xNewArray.type);
		accept("params", xNewArray.arraySizes);
		accept("arrayInitialize", xNewArray.arrayInitialize);
	}

	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		accept("arrayInitialize", xArrayInitialize);
	}

	@Override
	public void visitForeach(XForeach xForeach) {
		accept("var", xForeach.var);
		accept("in", xForeach.in);
		accept("block", xForeach.block);
	}

}
