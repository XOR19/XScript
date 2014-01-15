package xscript.compiler.tree;

import xscript.compiler.tree.XTree.XAnnotation;
import xscript.compiler.tree.XTree.XArrayInitialize;
import xscript.compiler.tree.XTree.XAssert;
import xscript.compiler.tree.XTree.XBlock;
import xscript.compiler.tree.XTree.XBreak;
import xscript.compiler.tree.XTree.XCase;
import xscript.compiler.tree.XTree.XCast;
import xscript.compiler.tree.XTree.XCatch;
import xscript.compiler.tree.XTree.XClassDecl;
import xscript.compiler.tree.XTree.XClassFile;
import xscript.compiler.tree.XTree.XCompiledPart;
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
import xscript.compiler.tree.XTree.XInstanceof;
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

public interface XVisitor {

	public void visitTopLevel(XClassFile xClassFile);

	public void visitImport(XImport xImport);

	public void visitClassDecl(XClassDecl xClassDef);

	public void visitAnnotation(XAnnotation xAnnotation);

	public void visitModifier(XModifier xModifier);

	public void visitError(XError xError);

	public void visitIdent(XIdent xIdent);

	public void visitType(XType xType);

	public void visitTypeParam(XTypeParam xTypeParam);

	public void visitVarDecl(XVarDecl xVarDecl);

	public void visitMethodDecl(XMethodDecl xMethodDecl);

	public void visitBlock(XBlock xBlock);

	public void visitBreak(XBreak xBreak);

	public void visitContinue(XContinue xContinue);

	public void visitDo(XDo xDo);

	public void visitWhile(XWhile xWhile);

	public void visitFor(XFor xFor);

	public void visitIf(XIf xIf);

	public void visitReturn(XReturn xReturn);

	public void visitThrow(XThrow xThrow);

	public void visitVarDecls(XVarDecls xVarDecls);

	public void visitGroup(XGroup xGroup);

	public void visitSynchronized(XSynchronized xSynchroized);

	public void visitConstant(XConstant xConstant);

	public void visitMethodCall(XMethodCall xMethodCall);

	public void visitNew(XNew xNew);

	public void visitOperator(XOperatorStatement xOperatorStatement);

	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix);

	public void visitIndex(XIndex xIndex);

	public void visitIfOperator(XIfOperator xIfOperator);

	public void visitCast(XCast xCast);

	public void visitLambda(XLambda xLambda);

	public void visitTry(XTry xTry);

	public void visitCatch(XCatch xCatch);

	public void visitNewArray(XNewArray xNewArray);

	public void visitArrayInitialize(XArrayInitialize xArrayInitialize);

	public void visitForeach(XForeach xForeach);

	public void visitLable(XLable xLable);

	public void visitSwitch(XSwitch xSwitch);

	public void visitCase(XCase xCase);

	public void visitThis(XThis xThis);

	public void visitSuper(XSuper xSuper);

	public void visitInstanceof(XInstanceof xInstanceof);

	public void visitAssert(XAssert xAssert);

	public void visitCompiled(XCompiledPart xCompiledPart);

}
