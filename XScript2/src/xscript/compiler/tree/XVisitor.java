package xscript.compiler.tree;

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

public interface XVisitor {

	public void visitTopLevel(XTreeClassFile xClassFile);

	public void visitImport(XTreeImport xImport);

	public void visitClassDecl(XTreeClassDecl xClassDef);

	public void visitAnnotation(XTreeAnnotation xAnnotation);

	public void visitModifier(XTreeModifier xModifier);

	public void visitError(XTreeError xError);

	public void visitIdent(XTreeIdent xIdent);

	public void visitType(XTreeType xType);

	public void visitTypeParam(XTreeTypeParam xTypeParam);

	public void visitVarDecl(XTreeVarDecl xVarDecl);

	public void visitMethodDecl(XTreeMethodDecl xMethodDecl);

	public void visitBlock(XTreeBlock xBlock);

	public void visitBreak(XTreeBreak xBreak);

	public void visitContinue(XTreeContinue xContinue);

	public void visitDo(XTreeDo xDo);

	public void visitWhile(XTreeWhile xWhile);

	public void visitFor(XTreeFor xFor);

	public void visitIf(XTreeIf xIf);

	public void visitReturn(XTreeReturn xReturn);

	public void visitThrow(XTreeThrow xThrow);

	public void visitVarDecls(XTreeVarDecls xVarDecls);

	public void visitGroup(XTreeGroup xGroup);

	public void visitSynchronized(XTreeSynchronized xSynchroized);

	public void visitConstant(XTreeConstant xConstant);

	public void visitMethodCall(XTreeMethodCall xMethodCall);

	public void visitNew(XTreeNew xNew);

	public void visitOperator(XTreeOperatorStatement xOperatorStatement);

	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix xOperatorPrefixSuffix);

	public void visitIndex(XTreeIndex xIndex);

	public void visitIfOperator(XTreeIfOperator xIfOperator);

	public void visitCast(XTreeCast xCast);

	public void visitLambda(XTreeLambda xLambda);

	public void visitTry(XTreeTry xTry);

	public void visitCatch(XTreeCatch xCatch);

	public void visitNewArray(XTreeNewArray xNewArray);

	public void visitArrayInitialize(XTreeArrayInitialize xArrayInitialize);

	public void visitForeach(XTreeForeach xForeach);

	public void visitLable(XTreeLable xLable);

	public void visitSwitch(XTreeSwitch xSwitch);

	public void visitCase(XTreeCase xCase);

	public void visitThis(XTreeThis xThis);

	public void visitSuper(XTreeSuper xSuper);

	public void visitInstanceof(XTreeInstanceof xInstanceof);

	public void visitAssert(XTreeAssert xAssert);

	public void visitCompiled(XTreeCompiledPart xCompiledPart);

}
