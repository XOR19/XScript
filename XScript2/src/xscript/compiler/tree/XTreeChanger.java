package xscript.compiler.tree;

import java.util.List;

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
import xscript.compiler.tree.XTree.XSuper;
import xscript.compiler.tree.XTree.XSwitch;
import xscript.compiler.tree.XTree.XSynchronized;
import xscript.compiler.tree.XTree.XThis;
import xscript.compiler.tree.XTree.XThrow;
import xscript.compiler.tree.XTree.XTry;
import xscript.compiler.tree.XTree.XType;
import xscript.compiler.tree.XTree.XTypeParam;
import xscript.compiler.tree.XTree.XVarDecl;
import xscript.compiler.tree.XTree.XVarDecls;
import xscript.compiler.tree.XTree.XWhile;

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
				tree.set(i, (T)visitTree(tree.get(i)));
			}
		}
		return tree;
	}
	
	@Override
	public void visitTopLevel(XClassFile xClassFile) {
		xClassFile.packAnnotations = visitTree(xClassFile.packAnnotations);
		xClassFile.packID = visitTree(xClassFile.packID);
		xClassFile.defs = visitTree(xClassFile.defs);
	}

	@Override
	public void visitImport(XImport xImport) {}

	@Override
	public void visitClassDecl(XClassDecl xClassDef) {
		xClassDef.modifier = visitTree(xClassDef.modifier);
		xClassDef.typeParam = visitTree(xClassDef.typeParam);
		xClassDef.superClasses = visitTree(xClassDef.superClasses);
		xClassDef.defs = visitTree(xClassDef.defs);
	}

	@Override
	public void visitAnnotation(XAnnotation xAnnotation) {}

	@Override
	public void visitModifier(XModifier xModifier) {
		xModifier.annotations = visitTree(xModifier.annotations);
	}

	@Override
	public void visitError(XError xError) {}

	@Override
	public void visitIdent(XIdent xIdent) {}

	@Override
	public void visitType(XType xType) {
		xType.name = visitTree(xType.name);
		xType.typeParam = visitTree(xType.typeParam);
	}

	@Override
	public void visitTypeParam(XTypeParam xTypeParam) {
		xTypeParam.extend = visitTree(xTypeParam.extend);
	}

	@Override
	public void visitVarDecl(XVarDecl xVarDecl) {
		xVarDecl.modifier = visitTree(xVarDecl.modifier);
		xVarDecl.type = visitTree(xVarDecl.type);
		xVarDecl.init = visitTree(xVarDecl.init);
	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		xMethodDecl.modifier = visitTree(xMethodDecl.modifier);
		xMethodDecl.typeParam = visitTree(xMethodDecl.typeParam);
		xMethodDecl.returnType = visitTree(xMethodDecl.returnType);
		xMethodDecl.paramTypes = visitTree(xMethodDecl.paramTypes);
		xMethodDecl.throwList = visitTree(xMethodDecl.throwList);
		xMethodDecl.block = visitTree(xMethodDecl.block);
		xMethodDecl.superConstructors = visitTree(xMethodDecl.superConstructors);
	}

	@Override
	public void visitBlock(XBlock xBlock) {
		xBlock.statements = visitTree(xBlock.statements);
	}

	@Override
	public void visitBreak(XBreak xBreak) {}

	@Override
	public void visitContinue(XContinue xContinue) {}

	@Override
	public void visitDo(XDo xDo) {
		xDo.doWhile = visitTree(xDo.doWhile);
		xDo.block = visitTree(xDo.block);
	}

	@Override
	public void visitWhile(XWhile xWhile) {
		xWhile.doWhile = visitTree(xWhile.doWhile);
		xWhile.block = visitTree(xWhile.block);
	}

	@Override
	public void visitFor(XFor xFor) {
		xFor.init = visitTree(xFor.init);
		xFor.doWhile = visitTree(xFor.doWhile);
		xFor.inc = visitTree(xFor.inc);
		xFor.block = visitTree(xFor.block);
	}

	@Override
	public void visitIf(XIf xIf) {
		xIf.iif = visitTree(xIf.iif);
		xIf.block = visitTree(xIf.block);
		xIf.block2 = visitTree(xIf.block2);
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		xReturn.statement = visitTree(xReturn.statement);
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		xThrow.statement = visitTree(xThrow.statement);
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		xVarDecls.varDecls = visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XGroup xGroup) {
		xGroup.statement = visitTree(xGroup.statement);
	}

	@Override
	public void visitSynchronized(XSynchronized xSynchroized) {
		xSynchroized.ident = visitTree(xSynchroized.ident);
		xSynchroized.block = visitTree(xSynchroized.block);
	}

	@Override
	public void visitConstant(XConstant xConstant) {}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		xMethodCall.method = visitTree(xMethodCall.method);
		xMethodCall.params = visitTree(xMethodCall.params);
		xMethodCall.typeParam = visitTree(xMethodCall.typeParam);
	}

	@Override
	public void visitNew(XNew xNew) {
		xNew.type = visitTree(xNew.type);
		xNew.params = visitTree(xNew.params);
		xNew.classDecl = visitTree(xNew.classDecl);
	}

	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		xOperatorStatement.left = visitTree(xOperatorStatement.left);
		xOperatorStatement.right = visitTree(xOperatorStatement.right);
	}

	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		xOperatorPrefixSuffix.statement = visitTree(xOperatorPrefixSuffix.statement);
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		xIndex.array = visitTree(xIndex.array);
		xIndex.index = visitTree(xIndex.index);
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		xIfOperator.left = visitTree(xIfOperator.left);
		xIfOperator.statement = visitTree(xIfOperator.statement);
		xIfOperator.right = visitTree(xIfOperator.right);
	}

	@Override
	public void visitCast(XCast xCast) {
		xCast.type = visitTree(xCast.type);
		xCast.statement = visitTree(xCast.statement);
	}

	@Override
	public void visitLambda(XLambda xLambda) {
		xLambda.params = visitTree(xLambda.params);
		xLambda.statement = visitTree(xLambda.statement);
	}

	@Override
	public void visitTry(XTry xTry) {
		xTry.resource = visitTree(xTry.resource);
		xTry.block = visitTree(xTry.block);
		xTry.catchs = visitTree(xTry.catchs);
		xTry.finallyBlock = visitTree(xTry.finallyBlock);
	}

	@Override
	public void visitCatch(XCatch xCatch) {
		xCatch.modifier = visitTree(xCatch.modifier);
		xCatch.types = visitTree(xCatch.types);
		xCatch.block = visitTree(xCatch.block);
	}

	@Override
	public void visitNewArray(XNewArray xNewArray) {
		xNewArray.type = visitTree(xNewArray.type);
		xNewArray.arraySizes = visitTree(xNewArray.arraySizes);
		xNewArray.arrayInitialize = visitTree(xNewArray.arrayInitialize);
	}

	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		xArrayInitialize.statements = visitTree(xArrayInitialize.statements);
	}

	@Override
	public void visitForeach(XForeach xForeach) {
		xForeach.var = visitTree(xForeach.var);
		xForeach.in = visitTree(xForeach.in);
		xForeach.block = visitTree(xForeach.block);
	}

	@Override
	public void visitLable(XLable xLable) {
		xLable.statement = visitTree(xLable.statement);
	}

	@Override
	public void visitSwitch(XSwitch xSwitch) {
		xSwitch.statement = visitTree(xSwitch.statement);
		xSwitch.cases = visitTree(xSwitch.cases);
	}

	@Override
	public void visitCase(XCase xCase) {
		xCase.key = visitTree(xCase.key);
		xCase.block = visitTree(xCase.block);
	}

	@Override
	public void visitThis(XThis xThis) {
		
	}

	@Override
	public void visitSuper(XSuper xSuper) {
		
	}

}
