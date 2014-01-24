package xscript.compiler.tree;

import java.util.List;

import xscript.compiler.tree.XTree.XTreeAnnotation;
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

public class XTreeChanger implements XVisitor {

	protected <T extends XTree> T visitTree(T tree){
		if(tree!=null){
			tree.accept(this);
		}
		return tree;
	}
	
	protected <T extends XTree> List<T> visitTree(List<T> tree){
		if(tree!=null){
			for(int i=0; i<tree.size(); i++){
				T t = (T)visitTree(tree.get(i));
				if(t==null){
					tree.remove(i);
					i--;
				}else{
					tree.set(i, t);
				}
			}
		}
		return tree;
	}
	
	@Override
	public void visitTopLevel(XTreeClassFile xClassFile) {
		xClassFile.packAnnotations = visitTree(xClassFile.packAnnotations);
		xClassFile.packID = visitTree(xClassFile.packID);
		xClassFile.defs = visitTree(xClassFile.defs);
	}

	@Override
	public void visitImport(XTreeImport xImport) {}

	@Override
	public void visitClassDecl(XTreeClassDecl xClassDef) {
		xClassDef.modifier = visitTree(xClassDef.modifier);
		xClassDef.typeParam = visitTree(xClassDef.typeParam);
		xClassDef.superClasses = visitTree(xClassDef.superClasses);
		xClassDef.defs = visitTree(xClassDef.defs);
	}

	@Override
	public void visitAnnotation(XTreeAnnotation xAnnotation) {}

	@Override
	public void visitModifier(XTreeModifier xModifier) {
		xModifier.annotations = visitTree(xModifier.annotations);
	}

	@Override
	public void visitError(XTreeError xError) {}

	@Override
	public void visitIdent(XTreeIdent xIdent) {}

	@Override
	public void visitType(XTreeType xType) {
		xType.name = visitTree(xType.name);
		xType.typeParam = visitTree(xType.typeParam);
	}

	@Override
	public void visitTypeParam(XTreeTypeParam xTypeParam) {
		xTypeParam.extend = visitTree(xTypeParam.extend);
	}

	@Override
	public void visitVarDecl(XTreeVarDecl xVarDecl) {
		xVarDecl.modifier = visitTree(xVarDecl.modifier);
		xVarDecl.type = visitTree(xVarDecl.type);
		xVarDecl.init = visitTree(xVarDecl.init);
	}

	@Override
	public void visitMethodDecl(XTreeMethodDecl xMethodDecl) {
		xMethodDecl.modifier = visitTree(xMethodDecl.modifier);
		xMethodDecl.typeParam = visitTree(xMethodDecl.typeParam);
		xMethodDecl.returnType = visitTree(xMethodDecl.returnType);
		xMethodDecl.paramTypes = visitTree(xMethodDecl.paramTypes);
		xMethodDecl.throwList = visitTree(xMethodDecl.throwList);
		xMethodDecl.block = visitTree(xMethodDecl.block);
		xMethodDecl.superConstructors = visitTree(xMethodDecl.superConstructors);
	}

	@Override
	public void visitBlock(XTreeBlock xBlock) {
		xBlock.statements = visitTree(xBlock.statements);
	}

	@Override
	public void visitBreak(XTreeBreak xBreak) {}

	@Override
	public void visitContinue(XTreeContinue xContinue) {}

	@Override
	public void visitDo(XTreeDo xDo) {
		xDo.doWhile = visitTree(xDo.doWhile);
		xDo.block = visitTree(xDo.block);
	}

	@Override
	public void visitWhile(XTreeWhile xWhile) {
		xWhile.doWhile = visitTree(xWhile.doWhile);
		xWhile.block = visitTree(xWhile.block);
	}

	@Override
	public void visitFor(XTreeFor xFor) {
		xFor.init = visitTree(xFor.init);
		xFor.doWhile = visitTree(xFor.doWhile);
		xFor.inc = visitTree(xFor.inc);
		xFor.block = visitTree(xFor.block);
	}

	@Override
	public void visitIf(XTreeIf xIf) {
		xIf.iif = visitTree(xIf.iif);
		xIf.block = visitTree(xIf.block);
		xIf.block2 = visitTree(xIf.block2);
	}

	@Override
	public void visitReturn(XTreeReturn xReturn) {
		xReturn.statement = visitTree(xReturn.statement);
	}

	@Override
	public void visitThrow(XTreeThrow xThrow) {
		xThrow.statement = visitTree(xThrow.statement);
	}

	@Override
	public void visitVarDecls(XTreeVarDecls xVarDecls) {
		xVarDecls.varDecls = visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XTreeGroup xGroup) {
		xGroup.statement = visitTree(xGroup.statement);
	}

	@Override
	public void visitSynchronized(XTreeSynchronized xSynchroized) {
		xSynchroized.ident = visitTree(xSynchroized.ident);
		xSynchroized.block = visitTree(xSynchroized.block);
	}

	@Override
	public void visitConstant(XTreeConstant xConstant) {}

	@Override
	public void visitMethodCall(XTreeMethodCall xMethodCall) {
		xMethodCall.method = visitTree(xMethodCall.method);
		xMethodCall.params = visitTree(xMethodCall.params);
		xMethodCall.typeParam = visitTree(xMethodCall.typeParam);
	}

	@Override
	public void visitNew(XTreeNew xNew) {
		xNew.type = visitTree(xNew.type);
		xNew.params = visitTree(xNew.params);
		xNew.classDecl = visitTree(xNew.classDecl);
	}

	@Override
	public void visitOperator(XTreeOperatorStatement xOperatorStatement) {
		xOperatorStatement.left = visitTree(xOperatorStatement.left);
		xOperatorStatement.right = visitTree(xOperatorStatement.right);
	}

	@Override
	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix xOperatorPrefixSuffix) {
		xOperatorPrefixSuffix.statement = visitTree(xOperatorPrefixSuffix.statement);
	}

	@Override
	public void visitIndex(XTreeIndex xIndex) {
		xIndex.array = visitTree(xIndex.array);
		xIndex.index = visitTree(xIndex.index);
	}

	@Override
	public void visitIfOperator(XTreeIfOperator xIfOperator) {
		xIfOperator.left = visitTree(xIfOperator.left);
		xIfOperator.statement = visitTree(xIfOperator.statement);
		xIfOperator.right = visitTree(xIfOperator.right);
	}

	@Override
	public void visitCast(XTreeCast xCast) {
		xCast.type = visitTree(xCast.type);
		xCast.statement = visitTree(xCast.statement);
	}

	@Override
	public void visitLambda(XTreeLambda xLambda) {
		xLambda.params = visitTree(xLambda.params);
		xLambda.statement = visitTree(xLambda.statement);
	}

	@Override
	public void visitTry(XTreeTry xTry) {
		xTry.resource = visitTree(xTry.resource);
		xTry.block = visitTree(xTry.block);
		xTry.catchs = visitTree(xTry.catchs);
		xTry.finallyBlock = visitTree(xTry.finallyBlock);
	}

	@Override
	public void visitCatch(XTreeCatch xCatch) {
		xCatch.modifier = visitTree(xCatch.modifier);
		xCatch.types = visitTree(xCatch.types);
		xCatch.block = visitTree(xCatch.block);
	}

	@Override
	public void visitNewArray(XTreeNewArray xNewArray) {
		xNewArray.type = visitTree(xNewArray.type);
		xNewArray.arraySizes = visitTree(xNewArray.arraySizes);
		xNewArray.arrayInitialize = visitTree(xNewArray.arrayInitialize);
	}

	@Override
	public void visitArrayInitialize(XTreeArrayInitialize xArrayInitialize) {
		xArrayInitialize.statements = visitTree(xArrayInitialize.statements);
	}

	@Override
	public void visitForeach(XTreeForeach xForeach) {
		xForeach.var = visitTree(xForeach.var);
		xForeach.in = visitTree(xForeach.in);
		xForeach.block = visitTree(xForeach.block);
	}

	@Override
	public void visitLable(XTreeLable xLable) {
		xLable.statement = visitTree(xLable.statement);
	}

	@Override
	public void visitSwitch(XTreeSwitch xSwitch) {
		xSwitch.statement = visitTree(xSwitch.statement);
		xSwitch.cases = visitTree(xSwitch.cases);
	}

	@Override
	public void visitCase(XTreeCase xCase) {
		xCase.key = visitTree(xCase.key);
		xCase.block = visitTree(xCase.block);
	}

	@Override
	public void visitThis(XTreeThis xThis) {
		
	}

	@Override
	public void visitSuper(XTreeSuper xSuper) {
		
	}

	@Override
	public void visitInstanceof(XTreeInstanceof xInstanceof) {
		xInstanceof.statement = visitTree(xInstanceof.statement);
		xInstanceof.type = visitTree(xInstanceof.type);
	}

	@Override
	public void visitAssert(XTreeAssert xAssert) {
		xAssert.statement = visitTree(xAssert.statement);
	}

	@Override
	public void visitCompiled(XTreeCompiledPart xCompiledPart) {
		
	}

}
