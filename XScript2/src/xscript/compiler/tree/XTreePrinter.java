package xscript.compiler.tree;

import java.util.List;

import xscript.compiler.tree.XTree.XTreeAnnotation;
import xscript.compiler.tree.XTree.XTreeAnnotationEntry;
import xscript.compiler.tree.XTree.XTreeArrayInitialize;
import xscript.compiler.tree.XTree.XTreeAssert;
import xscript.compiler.tree.XTree.XTreeBlock;
import xscript.compiler.tree.XTree.XTreeBreak;
import xscript.compiler.tree.XTree.XTreeCase;
import xscript.compiler.tree.XTree.XTreeCast;
import xscript.compiler.tree.XTree.XTreeCatch;
import xscript.compiler.tree.XTree.XTreeClassDecl;
import xscript.compiler.tree.XTree.XTreeClassFile;
import xscript.compiler.tree.XTree.XTreeCompiledPart;
import xscript.compiler.tree.XTree.XTreeConstant;
import xscript.compiler.tree.XTree.XTreeContinue;
import xscript.compiler.tree.XTree.XTreeDo;
import xscript.compiler.tree.XTree.XTreeError;
import xscript.compiler.tree.XTree.XTreeFor;
import xscript.compiler.tree.XTree.XTreeForeach;
import xscript.compiler.tree.XTree.XTreeGroup;
import xscript.compiler.tree.XTree.XTreeIdent;
import xscript.compiler.tree.XTree.XTreeIf;
import xscript.compiler.tree.XTree.XTreeIfOperator;
import xscript.compiler.tree.XTree.XTreeImport;
import xscript.compiler.tree.XTree.XTreeIndex;
import xscript.compiler.tree.XTree.XTreeInstanceof;
import xscript.compiler.tree.XTree.XTreeLable;
import xscript.compiler.tree.XTree.XTreeLambda;
import xscript.compiler.tree.XTree.XTreeMethodCall;
import xscript.compiler.tree.XTree.XTreeMethodDecl;
import xscript.compiler.tree.XTree.XTreeModifier;
import xscript.compiler.tree.XTree.XTreeNew;
import xscript.compiler.tree.XTree.XTreeNewArray;
import xscript.compiler.tree.XTree.XTreeOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XTreeOperatorStatement;
import xscript.compiler.tree.XTree.XTreeReturn;
import xscript.compiler.tree.XTree.XTreeSuper;
import xscript.compiler.tree.XTree.XTreeSwitch;
import xscript.compiler.tree.XTree.XTreeSynchronized;
import xscript.compiler.tree.XTree.XTreeThis;
import xscript.compiler.tree.XTree.XTreeThrow;
import xscript.compiler.tree.XTree.XTreeTry;
import xscript.compiler.tree.XTree.XTreeType;
import xscript.compiler.tree.XTree.XTreeTypeParam;
import xscript.compiler.tree.XTree.XTreeVarDecl;
import xscript.compiler.tree.XTree.XTreeVarDecls;
import xscript.compiler.tree.XTree.XTreeWhile;

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
	public void visitTopLevel(XTreeClassFile xClassFile) {
		accept("packAnnotations",xClassFile.packAnnotations);
		accept("packID",xClassFile.packID);
		accept("defs",xClassFile.defs);
	}

	@Override
	public void visitImport(XTreeImport xImport) {
		println("import "+(xImport.staticImport?"static ":"")+xImport.iimport+(xImport.indirect?".*":""));
	}

	@Override
	public void visitClassDecl(XTreeClassDecl xClassDef) {
		accept("modifier",xClassDef.modifier);
		println("name: "+xClassDef.name);
		accept("typeParam",xClassDef.typeParam);
		accept("superClasses",xClassDef.superClasses);
		accept("defs",xClassDef.defs);
	}

	@Override
	public void visitAnnotation(XTreeAnnotation xAnnotation) {
		accept("annotation", xAnnotation.annotation);
		accept("entries", xAnnotation.entries);
	}

	@Override
	public void visitModifier(XTreeModifier xModifier) {
		println(xscript.runtime.XModifier.toString(xModifier.modifier));
		accept("annotations", xModifier.annotations);
	}

	@Override
	public void visitError(XTreeError xError) {}

	@Override
	public void visitIdent(XTreeIdent xIdent) {
		println(xIdent.name);
	}

	@Override
	public void visitType(XTreeType xType) {
		accept("type", xType.name);
		println("array:"+xType.array);
		accept("typeParam", xType.typeParam);
	}

	@Override
	public void visitTypeParam(XTreeTypeParam xTypeParam) {
		println("name:"+xTypeParam.name);
		println("isSuper:"+xTypeParam.isSuper);
		accept("extend", xTypeParam.extend);
	}

	@Override
	public void visitVarDecl(XTreeVarDecl xVarDecl) {
		accept("modifier", xVarDecl.modifier);
		accept("type", xVarDecl.type);
		println("name:"+xVarDecl.name);
		accept("init", xVarDecl.init);
	}

	@Override
	public void visitMethodDecl(XTreeMethodDecl xMethodDecl) {
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
	public void visitBlock(XTreeBlock xBlock) {
		accept("statements", xBlock.statements);
	}

	@Override
	public void visitBreak(XTreeBreak xBreak) {
		println("lable:"+xBreak.lable);
	}

	@Override
	public void visitContinue(XTreeContinue xContinue) {
		println("lable:"+xContinue.lable);
	}

	@Override
	public void visitDo(XTreeDo xDo) {
		accept("while", xDo.doWhile);
		accept("block", xDo.block);
	}

	@Override
	public void visitWhile(XTreeWhile xWhile) {
		accept("while", xWhile.doWhile);
		accept("block", xWhile.block);
	}

	@Override
	public void visitFor(XTreeFor xFor) {
		accept("init", xFor.init);
		accept("while", xFor.doWhile);
		accept("inc", xFor.inc);
		accept("block", xFor.block);
	}

	@Override
	public void visitIf(XTreeIf xIf) {
		accept("if", xIf.iif);
		accept("block", xIf.block);
		accept("block2", xIf.block2);
	}

	@Override
	public void visitReturn(XTreeReturn xReturn) {
		accept("statement", xReturn.statement);
	}

	@Override
	public void visitThrow(XTreeThrow xThrow) {
		accept("statement", xThrow.statement);
	}

	@Override
	public void visitVarDecls(XTreeVarDecls xVarDecls) {
		accept("varDecls", xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XTreeGroup xGroup) {
		accept("group", xGroup.statement);
	}

	@Override
	public void visitSynchronized(XTreeSynchronized xSynchroized) {
		accept("ident", xSynchroized.ident);
		accept("block", xSynchroized.block);
	}

	@Override
	public void visitConstant(XTreeConstant xConstant) {
		println("value:"+xConstant.value);
	}

	@Override
	public void visitMethodCall(XTreeMethodCall xMethodCall) {
		accept("method",xMethodCall.method);
		accept("params", xMethodCall.params);
		accept("typeParam", xMethodCall.typeParam);
	}

	@Override
	public void visitNew(XTreeNew xNew) {
		accept("className", xNew.type);
		accept("params", xNew.params);
		accept("classDecl", xNew.classDecl);
		accept("element", xNew.element);
	}

	@Override
	public void visitOperator(XTreeOperatorStatement xOperatorStatement) {
		accept("left", xOperatorStatement.left);
		println("operator:"+xOperatorStatement.operator);
		accept("right", xOperatorStatement.right);
	}

	@Override
	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix xOperatorPrefixSuffix) {
		println("prefix:"+xOperatorPrefixSuffix.prefix);
		accept("statement", xOperatorPrefixSuffix.statement);
		println("suffix:"+xOperatorPrefixSuffix.suffix);
	}

	@Override
	public void visitIndex(XTreeIndex xIndex) {
		accept("array", xIndex.array);
		accept("index", xIndex.index);
	}

	@Override
	public void visitIfOperator(XTreeIfOperator xIfOperator) {
		accept("eq", xIfOperator.left);
		accept("then", xIfOperator.statement);
		accept("else", xIfOperator.right);
	}

	@Override
	public void visitCast(XTreeCast xCast) {
		accept("type", xCast.type);
		accept("statement", xCast.statement);
	}

	@Override
	public void visitLambda(XTreeLambda xLambda) {
		accept("params", xLambda.params);
		accept("statement", xLambda.statement);
	}

	@Override
	public void visitTry(XTreeTry xTry) {
		accept("resource", xTry.resource);
		accept("block", xTry.block);
		accept("catch", xTry.catchs);
		accept("finally", xTry.finallyBlock);
	}

	@Override
	public void visitCatch(XTreeCatch xCatch) {
		accept("modifier", xCatch.modifier);
		accept("type", xCatch.types);
		println("param:"+xCatch.varName);
		accept("block", xCatch.block);
	}

	@Override
	public void visitNewArray(XTreeNewArray xNewArray) {
		accept("className", xNewArray.type);
		accept("params", xNewArray.arraySizes);
		accept("arrayInitialize", xNewArray.arrayInitialize);
	}

	@Override
	public void visitArrayInitialize(XTreeArrayInitialize xArrayInitialize) {
		accept("arrayInitialize", xArrayInitialize);
	}

	@Override
	public void visitForeach(XTreeForeach xForeach) {
		accept("var", xForeach.var);
		accept("in", xForeach.in);
		accept("block", xForeach.block);
	}

	@Override
	public void visitLable(XTreeLable xLable) {
		println("lable:"+xLable.name);
		accept("statement", xLable.statement);
	}

	@Override
	public void visitSwitch(XTreeSwitch xSwitch) {
		accept("statement", xSwitch.statement);
		accept("cases", xSwitch.cases);
	}

	@Override
	public void visitCase(XTreeCase xCase) {
		accept("key", xCase.key);
		accept("block", xCase.block);
	}

	@Override
	public void visitThis(XTreeThis xThis) {}

	@Override
	public void visitSuper(XTreeSuper xSuper) {}

	@Override
	public void visitInstanceof(XTreeInstanceof xInstanceof) {
		accept("statement", xInstanceof.statement);
		accept("type", xInstanceof.type);
	}

	@Override
	public void visitAssert(XTreeAssert xAssert) {
		accept("assert", xAssert.statement);
	}

	@Override
	public void visitCompiled(XTreeCompiledPart xCompiledPart) {
		
	}

	@Override
	public void visitAnnotationEntry(XTreeAnnotationEntry xTreeAnnotationEntry) {
		accept("name", xTreeAnnotationEntry.name);
		accept("value", xTreeAnnotationEntry.value);
	}

}
