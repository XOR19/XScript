package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XVisitor;
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

public class XTreeCompiler implements XVisitor {

	private XMessageList messages;
	
	private List<String> directImport = new ArrayList<String>();
	private List<String> indirectImport = new ArrayList<String>();
	private List<String> directStaticImport = new ArrayList<String>();
	private List<String> indirectStaticImport = new ArrayList<String>();
	
	private boolean importsAllowed = true;
	
	public XTreeCompiler(XMessageList messages){
		this.messages = messages;
	}
	
	protected void compilerMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		messages.postMessage(level, "compiler."+key, lineDesk, args);
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
		for(XTree def:xClassFile.defs){
			importsAllowed &= def instanceof XImport || def==null;
			visitTree(def);
		}
	}

	@Override
	public void visitImport(XImport xImport) {
		if(importsAllowed){
			if(xImport.indirect){
				if(xImport.staticImport){
					indirectStaticImport.add(xImport.iimport);
				}else{
					indirectImport.add(xImport.iimport);
				}
			}else{
				if(xImport.staticImport){
					directStaticImport.add(xImport.iimport);
				}else{
					directImport.add(xImport.iimport);
				}
			}
		}else{
			compilerMessage(XMessageLevel.ERROR, "wrong.import", xImport.line);
		}
	}

	@Override
	public void visitClassDecl(XClassDecl xClassDef) {
		
	}

	@Override
	public void visitAnnotation(XAnnotation xAnnotation) {
		
	}

	@Override
	public void visitModifier(XModifier xModifier) {
		compilerMessage(XMessageLevel.ERROR, "unexpected", xModifier.line);
	}

	@Override
	public void visitError(XError xError) {
		compilerMessage(XMessageLevel.ERROR, "unexpected", xError.line);
	}

	@Override
	public void visitIdent(XIdent xIdent) {
		
	}

	@Override
	public void visitType(XType xType) {
		compilerMessage(XMessageLevel.ERROR, "unexpected", xType.line);
	}

	@Override
	public void visitTypeParam(XTypeParam xTypeParam) {
		compilerMessage(XMessageLevel.ERROR, "unexpected", xTypeParam.line);
	}

	@Override
	public void visitVarDecl(XVarDecl xVarDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitBlock(XBlock xBlock) {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void visitGroup(XGroup xGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visitThrow(XSynchroized xSynchroized) {
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

}
