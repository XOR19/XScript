package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
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
import xscript.compiler.tree.XVisitor;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionCheckCast;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.instruction.XInstructionReturn;
import xscript.runtime.instruction.XInstructionThrow;

public class XStatementCompiler implements XVisitor {

	private XMethodCompiler methodCompiler;
	
	private List<XInstruction> instructions;
	
	private XConstantValue constant;
	
	private XClassPtr returnExpected;
	
	private XClassPtr returnType;
	
	private XStatementCompiler parent;
	
	private HashMap<String, XVariable> vars;
	
	private List<XInstructionDumyJump> breaks;
	
	private List<XInstructionDumyJump> continues;
	
	private boolean lableUsed;
	
	private String lable;
	
	public XStatementCompiler(XClassPtr returnExpected, XStatementCompiler parent, XMethodCompiler methodCompiler){
		this.returnExpected = returnExpected;
		this.parent = parent;
		this.methodCompiler = methodCompiler;
	}
	
	private XStatementCompiler visitTree(XTree tree, XClassPtr returnExpected){
		if(tree!=null){
			XStatementCompiler statementCompiler = new XStatementCompiler(returnExpected, this, methodCompiler);
			tree.accept(statementCompiler);
			if(statementCompiler.returnType==null){
				statementCompiler.setReturn(null, tree);
			}
			return statementCompiler;
		}
		return null;
	}
	
	private List<XInstruction> visitTree(List<? extends XTree> tree){
		List<XInstruction> instructions = new ArrayList<XInstruction>();
		if(tree!=null){
			for(XTree t:tree){
				if(t!=null){
					XStatementCompiler statementCompiler = new XStatementCompiler(null, this, methodCompiler);
					t.accept(statementCompiler);
					if(!statementCompiler.isConstant())
						instructions.addAll(statementCompiler.getInstructions());
				}
			}
		}
		return instructions;
	}
	
	public XVariable getVariable(String name){
		if(vars!=null){
			XVariable var = vars.get(name);
			if(var!=null)
				return var;
		}
		if(parent==null)
			return null;
		return parent.getVariable(name);
	}
	
	public void addVariable(XVariable var){
		if(vars==null){
			parent.addVariable(var);
		}else{
			vars.put(var.name, var);
		}
	}
	
	public int getVarCount(){
		int count=0;
		if(parent!=null)
			count+=parent.getVarCount();
		if(vars!=null)
			count+=vars.size();
		return count;
	}
	
	
	public boolean isConstant(){
		return constant!=null;
	}
	
	public List<XInstruction> getInstructions() {
		if(instructions==null){
			instructions = new ArrayList<XInstruction>();
			if(constant!=null){
				instructions.add(makeConstLoad(constant));
			}
		}
		return instructions;
	}

	private void addInstruction(XInstruction instruction) {
		if(instructions==null){
			getInstructions();
		}
		instructions.add(instruction);
	}
	
	private void addInstructions(List<XInstruction> instructions) {
		if(this.instructions==null){
			getInstructions();
		}
		this.instructions.addAll(instructions);
	}
	
	private void addBreak(XInstructionDumyJump instruction, XBreak xBreak, boolean any){
		if(breaks!=null){
			any |= true;
			if(xBreak.lable==null || (lable!=null && lable.equals(xBreak.lable))){
				if(xBreak.lable!=null)
					lableUsed = true;
				breaks.add(instruction);
				return;
			}
		}
		if(parent!=null){
			addBreak(instruction, xBreak, any);
			return;
		}
		if(any)
			compilerError(XMessageLevel.ERROR, "break.lablenotfound", xBreak.line, xBreak.lable);
		compilerError(XMessageLevel.ERROR, "break.nobreakpoint", xBreak.line);
	}
	
	private void addContinue(XInstructionDumyJump instruction, XContinue xContinue, boolean any){
		if(continues!=null){
			any |= true;
			if(xContinue.lable==null || (lable!=null && lable.equals(xContinue.lable))){
				if(xContinue.lable!=null)
					lableUsed = true;
				continues.add(instruction);
				return;
			}
		}
		if(parent!=null){
			addContinue(instruction, xContinue, any);
			return;
		}
		if(any)
			compilerError(XMessageLevel.ERROR, "continue.lablenotfound", xContinue.line, xContinue.lable);
		compilerError(XMessageLevel.ERROR, "continue.nocontinuepoint", xContinue.line);
	}
	
	private XInstruction makeConstLoad(XConstantValue constant){
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		methodCompiler.compilerError(level, key, lineDesk, args);
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
		XCompiler compiler = (XCompiler) methodCompiler.getDeclaringClass().getVirtualMachine();
		String fullName = methodCompiler.getName()+"."+xClassDef.name;
		XClassCompiler classCompiler = new XClassCompiler(compiler, xClassDef.name, new XMessageClass(compiler, fullName), methodCompiler.getImportHelper());
		methodCompiler.addClass(classCompiler, xClassDef.line);
		xClassDef.accept(classCompiler);
		classCompiler.gen();
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
		XVariable var = getVariable(xIdent.name);
		if(var==null){
			//TODO
		}else{
			addInstruction(new XInstructionReadLocal(var.id));
			setReturn(var.type, xIdent);
		}
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
		XVariable var = new XVariable();
		var.varDecl = xVarDecl;
		var.modifier = xVarDecl.modifier==null?0:xVarDecl.modifier.modifier;
		var.type = methodCompiler.getGenericClass(xVarDecl.type);
		var.name = xVarDecl.name;
		var.id = getVarCount();
		if(getVariable(var.name)!=null){
			compilerError(XMessageLevel.ERROR, "variable.duplicated", xVarDecl.line, var.name);
		}else{
			addVariable(var);
		}
		//TODO
	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		shouldNeverCalled();
	}

	@Override
	public void visitBlock(XBlock xBlock) {
		vars = new HashMap<String, XVariable>();
		if(parent==null){
			XMethodDecl decl = methodCompiler.getMethodDecl();
			visitTree(decl.paramTypes);
		}
		instructions = visitTree(xBlock.statements);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addBreak(jump, xBreak, false);
		addInstruction(jump);
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addContinue(jump, xContinue, false);
		addInstruction(jump);
	}

	@Override
	public void visitDo(XDo xDo) {
		XStatementCompiler c1 = visitTree(xDo.block, null);
		XStatementCompiler c2 = visitTree(xDo.doWhile, new XClassPtrClass("bool"));
		
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
		XStatementCompiler c = visitTree(xIf.iif, new XClassPtrClass("bool"));
		XStatementCompiler b = visitTree(xIf.block, null);
		XStatementCompiler b2 = visitTree(xIf.block2, null);
		if(c.isConstant()){
			//TODO
		}else{
			addInstructions(c.getInstructions());
			XInstructionDumyIf iff = new XInstructionDumyIf();
			addInstruction(iff);
			addInstructions(b.getInstructions());
			if(b2==null || b2.isConstant() || b2.getInstructions()==null){
				addInstruction(iff.target = new XInstructionDumyDelete());
			}else{
				XInstructionDumyJump elsee = new XInstructionDumyJump();
				addInstruction(elsee);
				List<XInstruction> inst = b2.getInstructions();
				iff.target = inst.get(0);
				addInstructions(inst);
				addInstruction(elsee.target = new XInstructionDumyDelete());
			}
		}
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		if(xReturn.statement==null){
			System.out.println(methodCompiler);
			System.out.println(methodCompiler.getGenericReturnType());
			System.out.println(methodCompiler.getDeclaringClass());
			System.out.println(methodCompiler.getDeclaringClass().getVirtualMachine());
			XClass c= methodCompiler.getGenericReturnType().getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
			if(XPrimitive.getPrimitiveID(c)!=XPrimitive.VOID){
				compilerError(XMessageLevel.ERROR, "nonevoidreturn", xReturn.line);
			}
		}else{
			XStatementCompiler c = visitTree(xReturn.statement, methodCompiler.getGenericReturnType());
			addInstructions(c.getInstructions());
		}
		addInstruction(new XInstructionReturn());
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		XStatementCompiler c = visitTree(xThrow.statement, new XClassPtrClass("xscript.lang.Throwable"));
		addInstructions(c.getInstructions());
		addInstruction(new XInstructionThrow());
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XGroup xGroup) {
		xGroup.statement.accept(this);
	}

	@Override
	public void visitSynchronized(XSynchronized xSynchroized) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstant(XConstant xConstant) {
		constant = xConstant.value;
		String name=null;
		switch(xConstant.getTag()){
		case CHARLITERAL:
			name = "char";
			break;
		case DOUBLELITERAL:
			name = "double";
			break;
		case TRUE:
		case FALSE:
			name = "bool";
			break;
		case FLOATLITERAL:
			name = "float";
			break;
		case INTLITERAL:
			name = "int";
			break;
		case LONGLITERAL:
			name = "long";
			break;
		case NULL:
			setReturn(XClassPtrAny.instance, xConstant);
			return;
		case STRINGLITERAL:
			name = "xscript.lang.String";
			break;
		default:
			shouldNeverCalled();
		}
		setReturn(new XClassPtrClass(name), xConstant);
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
		XStatementCompiler sc = visitTree(xIfOperator.left, new XClassPtrClass("bool"));
		XStatementCompiler sct = visitTree(xIfOperator.statement, returnExpected);
		XStatementCompiler scf = visitTree(xIfOperator.right, returnExpected);
		addInstructions(sc.getInstructions());
		XInstructionDumyIf iif = new XInstructionDumyIf();
		addInstruction(iif);
		addInstructions(sct.getInstructions());
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addInstruction(jump);
		List<XInstruction> inst = scf.getInstructions();
		iif.target = inst.get(0);
		addInstructions(inst);
		addInstruction(jump.target = new XInstructionDumyDelete());
		//TODO
		setReturn(returnType, xIfOperator);
	}

	@Override
	public void visitCast(XCast xCast) {
		XStatementCompiler a = visitTree(xCast.statement, XClassPtrAny.instance);
		addInstructions(a.getInstructions());
		//TODO
		XClassPtr castTo = methodCompiler.getGenericClass(xCast.type);
		addInstruction(new XInstructionCheckCast(castTo));
		setReturn(castTo, xCast);
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
		shouldNeverCalled();
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
		lable = xLable.name;
		xLable.statement.accept(this);
		if(!lableUsed){
			compilerError(XMessageLevel.WARNING, "lable.unused", xLable.line, xLable.name);
		}
	}

	@Override
	public void visitSwitch(XSwitch xSwitch) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCase(XCase xCase) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitThis(XThis xThis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSuper(XSuper xSuper) {
		// TODO Auto-generated method stub
		
	}

	private void setReturn(XClassPtr returnType, XTree tree){
		this.returnType = returnType;
	}
	
	private void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
}
