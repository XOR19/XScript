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
import xscript.compiler.tree.XTree.XStatement;
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
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionCheckCast;
import xscript.runtime.instruction.XInstructionEqObject;
import xscript.runtime.instruction.XInstructionGetField;
import xscript.runtime.instruction.XInstructionGetLocalField;
import xscript.runtime.instruction.XInstructionGetStaticField;
import xscript.runtime.instruction.XInstructionInvokeDynamic;
import xscript.runtime.instruction.XInstructionInvokeSpecial;
import xscript.runtime.instruction.XInstructionInvokeStatic;
import xscript.runtime.instruction.XInstructionLoadConstBool;
import xscript.runtime.instruction.XInstructionLoadConstDouble;
import xscript.runtime.instruction.XInstructionLoadConstFloat;
import xscript.runtime.instruction.XInstructionLoadConstInt;
import xscript.runtime.instruction.XInstructionLoadConstLong;
import xscript.runtime.instruction.XInstructionLoadConstNull;
import xscript.runtime.instruction.XInstructionLoadConstString;
import xscript.runtime.instruction.XInstructionNEqObject;
import xscript.runtime.instruction.XInstructionNew;
import xscript.runtime.instruction.XInstructionNewArray;
import xscript.runtime.instruction.XInstructionODup;
import xscript.runtime.instruction.XInstructionOPop;
import xscript.runtime.instruction.XInstructionPop;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.instruction.XInstructionReturn;
import xscript.runtime.instruction.XInstructionSetField;
import xscript.runtime.instruction.XInstructionSetLocalField;
import xscript.runtime.instruction.XInstructionSetStaticField;
import xscript.runtime.instruction.XInstructionThrow;
import xscript.runtime.instruction.XInstructionWriteLocal;
import xscript.runtime.method.XMethod;

public class XStatementCompiler implements XVisitor {
	
	private XMethodCompiler methodCompiler;
	
	private XClassPtr returnExpected;
	
	private XClassPtr returnType;
	
	private XStatementCompiler parent;
	
	private HashMap<String, XVariable> vars;
	
	private List<XInstructionDumyJump> breaks;
	
	private List<XInstructionDumyJump> continues;
	
	private boolean lableUsed;
	
	private String lable;
	
	private XCodeGen codeGen;
	
	private XVarAccess varAccess;
	
	public XStatementCompiler(XClassPtr returnExpected, XStatementCompiler parent, XMethodCompiler methodCompiler){
		this.returnExpected = returnExpected;
		if(parent==null){
			vars = new HashMap<String, XVariable>();
		}
		this.parent = parent;
		this.methodCompiler = methodCompiler;
		codeGen = new XCodeGen();
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
	
	private XCodeGen visitTree(List<? extends XTree> tree){
		XCodeGen codeGen = new XCodeGen();
		if(tree!=null){
			for(XTree t:tree){
				if(t!=null){
					XStatementCompiler statementCompiler = new XStatementCompiler(null, this, methodCompiler);
					t.accept(statementCompiler);
					codeGen.addInstructions(statementCompiler.getCodeGen());
				}
			}
		}
		return codeGen;
	}
	
	private XVarAccess visitVarAccess(XTree tree){
		XStatementCompiler sc = visitTree(tree, XClassPtrAny.instance);
		XVarAccess varAccess = sc.getVarAccess();
		if(varAccess==null){
			compilerError(XMessageLevel.ERROR, "no.varAccess", tree.line);
		}
		return varAccess;
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
			var.id = getVarCount();
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

	public XCodeGen getCodeGen(){
		if(varAccess!=null){
			XField field = null;
			XVariable var = null;
			if(varAccess.declaringClass==null){
				var = getVariable(varAccess.name);
				if(var==null){
					field = methodCompiler.getDeclaringClass().getField(varAccess.name);
					if(field==null){
						compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
					}
				}
			}else{
				field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
				if(field==null){
					compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
				}
			}
			if(field==null){
				if(var!=null){
					addInstructions(varAccess.codeGen);
					addInstruction(new XInstructionReadLocal(var.id), varAccess.tree);
					setReturn(var.type, varAccess.tree);
				}
			}else{
				if(xscript.runtime.XModifier.isStatic(field.getModifier())){
					if(!varAccess.isStatic)
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(new XInstructionGetStaticField(field), varAccess.tree);
				}else{
					if(varAccess.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					addInstructions(varAccess.codeGen);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(new XInstructionGetLocalField(0, field), varAccess.tree);
						}else{
							addInstruction(new XInstructionGetField(field), varAccess.tree);
						}
					}else{
						addInstruction(new XInstructionGetLocalField(varAccess.variable.id, field), varAccess.tree);
					}
				}
				setReturn(field.getType(), varAccess.tree);
			}
		}
		return codeGen;
	}
	
	public XVarAccess getVarAccess(){
		return varAccess;
	}
	
	private void addInstruction(XInstruction instruction, XTree tree) {
		codeGen.addInstruction(instruction, tree.line.startLine);
	}
	
	private void addInstructions(XStatementCompiler statementCompiler) {
		codeGen.addInstructions(statementCompiler.getCodeGen());
	}
	
	private void addInstructions(XCodeGen codeGen) {
		if(codeGen!=null)
			this.codeGen.addInstructions(codeGen);
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
		varAccess = new XVarAccess();
		varAccess.codeGen = new XCodeGen();
		varAccess.name = xIdent.name;
		varAccess.tree = xIdent;
	}

	@Override
	public void visitType(XType xType) {
		varAccess = new XVarAccess();
		varAccess.codeGen = new XCodeGen();
		varAccess.name = null;
		varAccess.tree = xType;
		if(xType.array>0 || xType.typeParam!=null){
			varAccess.declaringClass = methodCompiler.getGenericClass(xType, true);
		}else{
			String rname = xType.name.name;
			int s=-1;
			String next = null;
			while(true){
				varAccess.declaringClass = methodCompiler.getGenericClass(xType, false);
				if(varAccess.declaringClass instanceof XClassPtrErrored)
					varAccess.declaringClass = null;
				if(varAccess.declaringClass!=null){
					if(s>-1){
						next = rname.substring(s+1);
					}
					break;
				}
				s = xType.name.name.lastIndexOf('.');
				if(s==-1){
					next = rname;
					break;
				}
				xType.name.name = xType.name.name.substring(0, s);
			}
			if(varAccess.declaringClass!=null){
				varAccess.isStatic = true;
			}
			xType.name.name = rname;
			if(next!=null){
				String[] na = next.split("\\.");
				varAccess.name = na[0];
				for(int i=1; i<na.length; i++){
					XField field = null;
					XVariable var = null;
					if(varAccess.declaringClass==null){
						var = getVariable(varAccess.name);
						if(var==null){
							field = methodCompiler.getDeclaringClass().getField(varAccess.name);
							if(field==null){
								compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
							}
						}
					}else{
						field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
						if(field==null){
							compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
						}
					}
					if(field==null){
						if(var!=null){
							varAccess.variable = var;
							varAccess.declaringClass = var.type;
						}
					}else{
						if(xscript.runtime.XModifier.isStatic(field.getModifier())){
							if(!varAccess.isStatic)
								compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
							varAccess.codeGen = new XCodeGen();
							varAccess.codeGen.addInstruction(new XInstructionGetStaticField(field), xType.line.startLine);
						}else{
							if(varAccess.isStatic)
								compilerError(XMessageLevel.WARNING, "field.is.static", varAccess.tree.line, varAccess.name);
							if(varAccess.variable==null){
								if(varAccess.declaringClass==null){
									varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, field), xType.line.startLine);
								}else{
									varAccess.codeGen.addInstruction(new XInstructionGetField(field), xType.line.startLine);
								}
							}else{
								varAccess.codeGen.addInstruction(new XInstructionGetLocalField(varAccess.variable.id, field), xType.line.startLine);
							}
						}
						varAccess.variable = null;
						varAccess.declaringClass = field.getType();
					}
					varAccess.name = na[i];
				}
			}
		}
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
		var.type = methodCompiler.getGenericClass(xVarDecl.type, true);
		var.name = xVarDecl.name;
		if(getVariable(var.name)!=null){
			compilerError(XMessageLevel.ERROR, "variable.duplicated", xVarDecl.line, var.name);
		}else{
			addVariable(var);
		}
		if(xVarDecl.init!=null){
			XStatementCompiler sc = visitTree(xVarDecl.init, var.type);
			addInstructions(sc);
			addInstruction(new XInstructionWriteLocal(var.id), xVarDecl);
		}
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
			if(!xscript.runtime.XModifier.isStatic(methodCompiler.getModifier())){
				XVariable variable = new XVariable();
				variable.modifier = xscript.runtime.XModifier.FINAL;
				variable.type = methodCompiler.getDeclaringClassGen();
				variable.name = "this";
				addVariable(variable);
			}
			visitTree(decl.paramTypes);
		}
		codeGen = visitTree(xBlock.statements);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addBreak(jump, xBreak, false);
		addInstruction(jump, xBreak);
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addContinue(jump, xContinue, false);
		addInstruction(jump, xContinue);
	}

	@Override
	public void visitDo(XDo xDo) {
		List<XInstructionDumyJump> ccontinues = continues = new ArrayList<XInstructionDumyJump>();
		List<XInstructionDumyJump> bbreaks = breaks = new ArrayList<XInstructionDumyJump>();
		XStatementCompiler c1 = visitTree(xDo.block, null);
		continues = breaks = null;
		XStatementCompiler c2 = visitTree(xDo.doWhile, new XClassPtrClass("bool"));
		XInstructionDumyIf iif = new XInstructionDumyIf();
		iif.target = getTarget(c1.getCodeGen(), xDo);
		addInstructions(c1);
		XInstruction continueTarget = getTarget(c2.getCodeGen(), xDo);
		addInstructions(c2);
		addInstruction(iif, xDo);
		XInstructionDumyDelete breakTarget = new XInstructionDumyDelete();
		addInstruction(breakTarget, xDo);
		for(XInstructionDumyJump ccontinue:ccontinues){
			ccontinue.target = continueTarget;
		}
		for(XInstructionDumyJump bbreak:bbreaks){
			bbreak.target = breakTarget;
		}
	}

	@Override
	public void visitWhile(XWhile xWhile) {
		XStatementCompiler c1 = visitTree(xWhile.doWhile, new XClassPtrClass("bool"));
		continues = new ArrayList<XInstructionDumyJump>();
		breaks = new ArrayList<XInstructionDumyJump>();
		XStatementCompiler c2 = visitTree(xWhile.block, null);
		XInstruction continueTarget = getTarget(c1.getCodeGen(), xWhile);
		addInstructions(c1);
		XInstructionDumyNIf iif = new XInstructionDumyNIf();
		addInstruction(iif, xWhile);
		addInstructions(c2);
		XInstructionDumyDelete breakTarget = new XInstructionDumyDelete();
		addInstruction(breakTarget, xWhile);
		iif.target = breakTarget;
		for(XInstructionDumyJump ccontinue:continues){
			ccontinue.target = continueTarget;
		}
		for(XInstructionDumyJump bbreak:breaks){
			bbreak.target = breakTarget;
		}
	}

	@Override
	public void visitFor(XFor xFor) {
		vars = new HashMap<String, XVariable>();
		XStatementCompiler c1 = visitTree(xFor.init, null);
		addInstructions(c1);
		c1 = visitTree(xFor.doWhile, new XClassPtrClass("bool"));
		XStatementCompiler c2 = visitTree(xFor.inc, null);
		continues = new ArrayList<XInstructionDumyJump>();
		breaks = new ArrayList<XInstructionDumyJump>();
		XStatementCompiler c3 = visitTree(xFor.block, null);
		XInstruction startTarget = getTarget(c1.getCodeGen(), xFor);
		addInstructions(c1);
		XInstructionDumyNIf iif = new XInstructionDumyNIf();
		addInstruction(iif, xFor);
		addInstructions(c3);
		XInstruction continueTarget = getTarget(c2.getCodeGen(), xFor);
		addInstructions(c2);
		XInstructionDumyJump jump = new XInstructionDumyJump();
		jump.target = startTarget;
		addInstruction(jump, xFor);
		XInstructionDumyDelete breakTarget = new XInstructionDumyDelete();
		addInstruction(breakTarget, xFor);
		iif.target = breakTarget;
		for(XInstructionDumyJump ccontinue:continues){
			ccontinue.target = continueTarget;
		}
		for(XInstructionDumyJump bbreak:breaks){
			bbreak.target = breakTarget;
		}
	}

	@Override
	public void visitIf(XIf xIf) {
		XStatementCompiler c = visitTree(xIf.iif, new XClassPtrClass("bool"));
		XStatementCompiler b = visitTree(xIf.block, null);
		XStatementCompiler b2 = visitTree(xIf.block2, null);
		addInstructions(c);
		XInstructionDumyNIf iff = new XInstructionDumyNIf();
		addInstruction(iff, xIf);
		addInstructions(b);
		if(b2==null || b2.getCodeGen().isEmpty()){
			addInstruction(iff.target = new XInstructionDumyDelete(), xIf);
		}else{
			XInstructionDumyJump elsee = new XInstructionDumyJump();
			addInstruction(elsee, xIf);
			iff.target = getTarget(b2.getCodeGen(), xIf);
			addInstruction(elsee.target = new XInstructionDumyDelete(), xIf);
		}
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		if(xReturn.statement==null){
			XClass c= methodCompiler.getGenericReturnType().getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
			if(XPrimitive.getPrimitiveID(c)!=XPrimitive.VOID){
				compilerError(XMessageLevel.ERROR, "nonevoidreturn", xReturn.line);
			}
		}else{
			XStatementCompiler c = visitTree(xReturn.statement, methodCompiler.getGenericReturnType());
			addInstructions(c);
		}
		addInstruction(new XInstructionReturn(), xReturn);
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		XStatementCompiler c = visitTree(xThrow.statement, new XClassPtrClass("xscript.lang.Throwable"));
		addInstructions(c);
		addInstruction(new XInstructionThrow(), xThrow);
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		codeGen = visitTree(xVarDecls.varDecls);
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
		String name=null;
		Class<?> c = xConstant.value.getType();
		if(c==null){
			addInstruction(new XInstructionLoadConstNull(), xConstant);
			setReturn(XClassPtrAny.instance, xConstant);
			return;
		}else if(c==Boolean.class){
			addInstruction(new XInstructionLoadConstBool(xConstant.value.getBool()), xConstant);
			name = "bool";
		}else if(c==Character.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getChar()), xConstant);
			name = "char";
		}else if(c==Byte.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getByte()), xConstant);
			name = "byte";
		}else if(c==Short.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getShort()), xConstant);
			name = "short";
		}else if(c==Integer.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getInt()), xConstant);
			name = "int";
		}else if(c==Long.class){
			addInstruction(new XInstructionLoadConstLong(xConstant.value.getLong()), xConstant);
			name = "long";
		}else if(c==Float.class){
			addInstruction(new XInstructionLoadConstFloat(xConstant.value.getFloat()), xConstant);
			name = "float";
		}else if(c==Double.class){
			addInstruction(new XInstructionLoadConstDouble(xConstant.value.getDouble()), xConstant);
			name = "double";
		}else if(c==String.class){
			addInstruction(new XInstructionLoadConstString(xConstant.value.getString()), xConstant);
			name = "xscript.lang.String";
		}else{
			shouldNeverCalled();
		}
		setReturn(new XClassPtrClass(name), xConstant);
	}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		XVarAccess varAccess = visitVarAccess(xMethodCall.method);
		addInstructions(varAccess.codeGen);
		if(varAccess.variable!=null){
			addInstruction(new XInstructionReadLocal(varAccess.variable.id), xMethodCall);
		}else{
			if(varAccess.declaringClass==null){
				addInstruction(new XInstructionReadLocal(0), xMethodCall);
			}
		}
		XClass c = varAccess.declaringClass==null?methodCompiler.getDeclaringClass():getXClassFor(varAccess.declaringClass);
		XMethodSearch possibleMethods = new XMethodSearch(c, varAccess.isStatic, varAccess.name, varAccess.specialInvoke, true);
		if(xMethodCall.typeParam!=null){
			XClassPtr[] generics = new XClassPtr[xMethodCall.typeParam.size()];
			for(int i=0; i<generics.length; i++){
				generics[i] = methodCompiler.getGenericClass(xMethodCall.typeParam.get(i), true);
			}
			possibleMethods.applyGenerics(generics);
		}
		XClassPtr[] types = null;
		if(xMethodCall.params!=null){
			types = new XClassPtr[xMethodCall.params.size()];
			for(int i=0; i<types.length; i++){
				XStatementCompiler sc = visitTree(xMethodCall.params.get(i), XClassPtrAny.instance);
				addInstructions(sc);
				types[i] = sc.returnType;
			}
		}
		possibleMethods.applyTypes(types);
		setReturn(makeCall(possibleMethods, xMethodCall), xMethodCall);
	}

	private XClassPtr makeCall(XMethodSearch possibleMethods, XTree tree) {
		XMethod m = possibleMethods.getMethod();
		if(m==null){
			if(possibleMethods.isEmpty()){
				compilerError(XMessageLevel.ERROR, "nomethodfor", tree.line, possibleMethods.getDesk());
			}else{
				compilerError(XMessageLevel.ERROR, "toomanymethodfor", tree.line, possibleMethods.getDesk());
			}
			return null;
		}
		if(xscript.runtime.XModifier.isStatic(m.getModifier())){
			addInstruction(new XInstructionInvokeStatic(m, possibleMethods.getGenerics()), tree);
			if(!possibleMethods.shouldBeStatic()){
				addInstruction(new XInstructionOPop(), tree);
				compilerError(XMessageLevel.WARNING, "static.access", tree.line, possibleMethods.getDesk());
			}
		}else{
			if(possibleMethods.shouldBeStatic()){
				compilerError(XMessageLevel.ERROR, "non.static.access", tree.line, possibleMethods.getDesk());
			}
			if(possibleMethods.specialInvoke()){
				addInstruction(new XInstructionInvokeSpecial(m, possibleMethods.getGenerics()), tree);
			}else{
				addInstruction(new XInstructionInvokeDynamic(m, possibleMethods.getGenerics()), tree);
			}
		}
		return m.getReturnTypePtr();
	}

	@Override
	public void visitNew(XNew xNew) {
		//TODO
		
		XClassPtr classPtr = methodCompiler.getGenericClass(xNew.type, true);
		XMethodSearch search = new XMethodSearch(getXClassFor(classPtr), false, "<init>", true, false);
		addInstruction(new XInstructionNew(classPtr), xNew);
		addInstruction(new XInstructionODup(), xNew);
		makeCall(search, xNew);
		setReturn(classPtr, xNew);
	}
	
	private int num(int nid, XLineDesk lineDesk){
		switch(nid){
		case XPrimitive.CHAR:
			return 3;
		case XPrimitive.BYTE:
			return 1;
		case XPrimitive.SHORT:
			return 2;
		case XPrimitive.INT:
			return 3;
		case XPrimitive.LONG:
			return 4;
		case XPrimitive.FLOAT:
			return 5;
		case XPrimitive.DOUBLE:
			return 6;
		default:
			compilerError(XMessageLevel.ERROR, "wrong.cast", lineDesk, XPrimitive.getName(nid));
			return 0;
		}
	}
	
	private int compNID(int nid1, int nid2, XLineDesk lineDesk){
		switch(nid1){
		case XPrimitive.BOOL:
			if(nid2!=XPrimitive.BOOL){
				compilerError(XMessageLevel.ERROR, "wrong.cast2bool", lineDesk, XPrimitive.getName(nid2));
			}
			return XPrimitive.BOOL;
		case XPrimitive.CHAR:
		case XPrimitive.BYTE:
		case XPrimitive.SHORT:
		case XPrimitive.INT:
		case XPrimitive.LONG:
		case XPrimitive.FLOAT:
		case XPrimitive.DOUBLE:
			int n1 = num(nid1, lineDesk);
			int n2 = num(nid2, lineDesk);
			if(n1<n2){
				n1 = n2;
			}
			if(n1==1){
				return XPrimitive.BOOL;
			}else if(n1==2){
				return XPrimitive.SHORT;
			}else if(n1==3){
				if(nid1==XPrimitive.CHAR || nid2==XPrimitive.CHAR)
					return XPrimitive.CHAR;
				return XPrimitive.INT;
			}else if(n1==4){
				return XPrimitive.LONG;
			}else if(n1==5){
				return XPrimitive.FLOAT;
			}else if(n1==5){
				return XPrimitive.DOUBLE;
			}
		default:
			shouldNeverCalled();
			return 0;
		}
	}
	
	private static int pid2iid(int n){
		switch(n){
		case XPrimitive.BOOL:
			return XOperatorHelper.BOOLINST;
		case XPrimitive.CHAR:
		case XPrimitive.BYTE:
		case XPrimitive.SHORT:
		case XPrimitive.INT:
			return XOperatorHelper.INTINST;
		case XPrimitive.LONG:
			return XOperatorHelper.LONGINST;
		case XPrimitive.FLOAT:
			return XOperatorHelper.FLOATINST;
		case XPrimitive.DOUBLE:
			return XOperatorHelper.DOUBLEINST;
		default:
			shouldNeverCalled();
			return -1;
		}
	}
	
	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		XOperator op = xOperatorStatement.operator;
		if(op == XOperator.ELEMENT){
			String name = ((XIdent)xOperatorStatement.right).name;
			if(xOperatorStatement.left instanceof XSuper){
				varAccess = new XVarAccess();
				varAccess.codeGen = new XCodeGen();
				varAccess.declaringClass = getXSuperClassFor(methodCompiler.getDeclaringClassGen());
				varAccess.specialInvoke = true;
				setReturn(XClassPtrAny.instance, xOperatorStatement);
			}else{
				XStatementCompiler cl = visitTree(xOperatorStatement.left, XClassPtrAny.instance);
				varAccess = cl.varAccess;
				if(varAccess==null){
					varAccess = new XVarAccess();
					varAccess.codeGen = new XCodeGen();
					varAccess.codeGen.addInstructions(cl.getCodeGen());
					varAccess.declaringClass = cl.returnType;
					setReturn(XClassPtrAny.instance, xOperatorStatement);
				}else{
					XField field = null;
					XVariable var = null;
					if(varAccess.declaringClass==null){
						var = getVariable(varAccess.name);
						if(var==null){
							field = methodCompiler.getDeclaringClass().getField(varAccess.name);
							if(field==null){
								compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
							}
						}
					}else if(varAccess.name!=null){
						field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
						if(field==null){
							compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
						}
					}
					if(varAccess.name==null){
						varAccess.name = name;
					}else{
						if(field==null){
							if(var!=null){
								varAccess.variable = var;
								varAccess.declaringClass = var.type;
							}
						}else{
							if(xscript.runtime.XModifier.isStatic(field.getModifier())){
								if(!varAccess.isStatic)
									compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
								varAccess.codeGen = new XCodeGen();
								varAccess.codeGen.addInstruction(new XInstructionGetStaticField(field), xOperatorStatement.line.startLine);
							}else{
								if(varAccess.isStatic)
									compilerError(XMessageLevel.WARNING, "field.is.static", varAccess.tree.line, varAccess.name);
								if(varAccess.variable==null){
									if(varAccess.declaringClass==null){
										varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, field), xOperatorStatement.line.startLine);
									}else{
										varAccess.codeGen.addInstruction(new XInstructionGetField(field), xOperatorStatement.line.startLine);
									}
								}else{
									varAccess.codeGen.addInstruction(new XInstructionGetLocalField(varAccess.variable.id, field), xOperatorStatement.line.startLine);
								}
							}
							varAccess.variable = null;
							varAccess.declaringClass = field.getType();
						}
					}
				}
			}
			varAccess.name = name;
			varAccess.tree = xOperatorStatement;
		}else if(op == XOperator.LET){
			if(xOperatorStatement.left instanceof XIndex){
				XIndex index = ((XIndex)xOperatorStatement.left);
				XStatementCompiler s = visitTree(index.array, XClassPtrAny.instance);
				XStatementCompiler i = visitTree(index.index, XClassPtrAny.instance);
				XStatementCompiler cr = visitTree(xOperatorStatement.right, XClassPtrAny.instance);
				addInstructions(s);
				addInstructions(i);
				addInstructions(cr);
				XMethodSearch methodSearch = new XMethodSearch(getXClassFor(s.returnType), false, "operator[]", false, true);
				methodSearch.applyTypes(new XClassPtr[]{i.returnType, cr.returnType});
				setReturn(makeCall(methodSearch, index), index);
			}else{
				XVarAccess varAccess = visitVarAccess(xOperatorStatement.left);
				XField field = null;
				XVariable var = null;
				if(varAccess.declaringClass==null){
					var = getVariable(varAccess.name);
					if(var==null){
						field = methodCompiler.getDeclaringClass().getField(varAccess.name);
						if(field==null){
							compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
						}
					}
				}else{
					field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
					if(field==null){
						compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
					}
				}
				if(field==null){
					addInstructions(varAccess.codeGen);
					XStatementCompiler cr = visitTree(xOperatorStatement.right, var.type);
					addInstructions(cr);
					if(xscript.runtime.XModifier.isFinal(var.modifier)){
						compilerError(XMessageLevel.ERROR, "write.final.var", xOperatorStatement.line, var.name);
					}
					addInstruction(new XInstructionWriteLocal(var.id), xOperatorStatement);
				}else{
					XStatementCompiler cr = visitTree(xOperatorStatement.right, field.getType());
					if(xscript.runtime.XModifier.isFinal(field.getModifier())){
						if(!(methodCompiler.getDeclaringClass()==field.getDeclaringClass() && methodCompiler.isConstructor() && 
								xscript.runtime.XModifier.isStatic(field.getModifier())== xscript.runtime.XModifier.isStatic(methodCompiler.getModifier()))){
							compilerError(XMessageLevel.ERROR, "write.final.field", xOperatorStatement.line, field.getName());
						}
					}
					if(xscript.runtime.XModifier.isStatic(field.getModifier())){
						addInstructions(cr);
						if(!varAccess.isStatic)
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(new XInstructionSetStaticField(field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						addInstructions(cr);
						if(varAccess.isStatic)
							compilerError(XMessageLevel.WARNING, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(new XInstructionSetLocalField(0, field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionSetField(field), xOperatorStatement);
							}
						}else{
							addInstruction(new XInstructionSetLocalField(varAccess.variable.id, field), xOperatorStatement);
						}
					}
				}
			}
		}else if(op == XOperator.LETADD || op == XOperator.LETSUB || op == XOperator.LETMUL || op == XOperator.LETDIV || op == XOperator.LETMOD ||
				op == XOperator.LETOR || op == XOperator.LETAND || op == XOperator.LETXOR || op == XOperator.LETSHR || op == XOperator.LETSHL){
			XVarAccess varAccess = visitVarAccess(xOperatorStatement.left);
			XField field = null;
			XVariable var = null;
			if(varAccess.declaringClass==null){
				var = getVariable(varAccess.name);
				if(var==null){
					varAccess.declaringClass = methodCompiler.getDeclaringClassGen();
					field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
					if(field==null){
						compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
					}
				}
			}else{
				field = getXClassFor(varAccess.declaringClass).getField(varAccess.name);
				if(field==null){
					compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
				}
			}
		}else{
			XStatementCompiler cl = visitTree(xOperatorStatement.left, XClassPtrAny.instance);
			XStatementCompiler cr = visitTree(xOperatorStatement.right, XClassPtrAny.instance);
			addInstructions(cl);
			addInstructions(cr);
			int t1 = XPrimitive.getPrimitiveID(getXClassFor(cl.returnType));
			int t2 = XPrimitive.getPrimitiveID(getXClassFor(cr.returnType));
			if(t1 == XPrimitive.OBJECT){
				if(op == XOperator.EQ || op == XOperator.NEQ){
					if(op == XOperator.EQ){
						addInstruction(new XInstructionEqObject(), xOperatorStatement);
					}else{
						addInstruction(new XInstructionNEqObject(), xOperatorStatement);
					}
					setReturn(new XClassPtrClass("bool"), xOperatorStatement);
				}else{
					XMethodSearch methodSearch = new XMethodSearch(getXClassFor(cl.returnType), false, "operator"+op.op, false, true);
					methodSearch.applyTypes(cr.returnType);
					setReturn(makeCall(methodSearch, xOperatorStatement), xOperatorStatement);
				}
			}else{
				int ret = compNID(t1, t2, xOperatorStatement.line);
				int type = pid2iid(ret);
				addInstruction(XOperatorHelper.makeInstructionForOperator(xOperatorStatement.operator, type), xOperatorStatement);
				if(op == XOperator.SMA || op == XOperator.BIG || op == XOperator.SEQ || op == XOperator.BEQ || op == XOperator.EQ || op == XOperator.NEQ || op == XOperator.REQ || op == XOperator.RNEQ){
					ret = XPrimitive.BOOL;
				}else if(op == XOperator.COMP){	
					ret = XPrimitive.INT;
				}
				setReturn(new XClassPtrClass(XPrimitive.getName(ret)), xOperatorStatement);
			}
		}
	}
	
	private XClassPtr getXSuperClassFor(XClassPtr classPtr){
		// TODO
		return null;
	}
	
	private XClass getXClassFor(XClassPtr classPtr){
		if(classPtr==null)
			return null;
		return classPtr.getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
	}
	
	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		XStatementCompiler s = visitTree(xOperatorPrefixSuffix.statement, XClassPtrAny.instance);
		addInstructions(s);
		returnType = s.returnType;
		if(xOperatorPrefixSuffix.prefix!=null){
			for(XOperator op:xOperatorPrefixSuffix.prefix){
				int t = XPrimitive.getPrimitiveID(returnType.getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine()));
				if(t == XPrimitive.OBJECT){
					XMethodSearch methodSearch = new XMethodSearch(getXClassFor(returnType), false, "operator"+op.op, false, true);
					methodSearch.applyTypes();
					returnType = makeCall(methodSearch, xOperatorPrefixSuffix);
				}else{
					int type = pid2iid(t);
					addInstruction(XOperatorHelper.makeInstructionForOperator(op, type), xOperatorPrefixSuffix);
				}
			}
		}
		if(xOperatorPrefixSuffix.suffix!=null){
			for(XOperator op:xOperatorPrefixSuffix.suffix){
				int t = XPrimitive.getPrimitiveID(returnType.getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine()));
				if(t == XPrimitive.OBJECT){
					XMethodSearch methodSearch = new XMethodSearch(getXClassFor(returnType), false, "operator"+op.op, false, true);
					if(op==XOperator.INCS || op==XOperator.DECS){
						addInstruction(new XInstructionLoadConstInt(0), xOperatorPrefixSuffix);
						methodSearch.applyTypes(new XClassPtrClass("int"));
						returnType = makeCall(methodSearch, xOperatorPrefixSuffix);
					}else{
						methodSearch.applyTypes();
						returnType = makeCall(methodSearch, xOperatorPrefixSuffix);
					}
				}else{
					int type = pid2iid(t);
					addInstruction(XOperatorHelper.makeInstructionForOperator(op, type), xOperatorPrefixSuffix);
				}
			}
		}
		setReturn(returnType, xOperatorPrefixSuffix);
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		XStatementCompiler s = visitTree(xIndex.array, XClassPtrAny.instance);
		XStatementCompiler i = visitTree(xIndex.index, XClassPtrAny.instance);
		addInstructions(s);
		addInstructions(i);
		XMethodSearch methodSearch = new XMethodSearch(getXClassFor(s.returnType), false, "operator[]", false, true);
		methodSearch.applyTypes(new XClassPtr[]{i.returnType});
		setReturn(makeCall(methodSearch, xIndex), xIndex);
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		XStatementCompiler sc = visitTree(xIfOperator.left, new XClassPtrClass("bool"));
		XStatementCompiler sct = visitTree(xIfOperator.statement, returnExpected);
		XStatementCompiler scf = visitTree(xIfOperator.right, returnExpected);
		addInstructions(sc);
		XInstructionDumyNIf iif = new XInstructionDumyNIf();
		addInstruction(iif, xIfOperator);
		addInstructions(sct);
		XInstructionDumyJump jump = new XInstructionDumyJump();
		addInstruction(jump, xIfOperator);
		iif.target = getTarget(scf.getCodeGen(), xIfOperator);
		addInstruction(jump.target = new XInstructionDumyDelete(), xIfOperator);
		//TODO
		setReturn(returnType, xIfOperator);
	}

	@Override
	public void visitCast(XCast xCast) {
		XStatementCompiler a = visitTree(xCast.statement, XClassPtrAny.instance);
		addInstructions(a);
		//TODO
		XClassPtr castTo = methodCompiler.getGenericClass(xCast.type, true);
		addInstruction(new XInstructionCheckCast(castTo), xCast);
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
		boolean n = false;
		int init = 0;
		for(XStatement s:xNewArray.arraySizes){
			if(s==null){
				n=true;
			}else{
				if(!n){
					init++;
				}
			}
		}
		XClassPtr type = methodCompiler.getGenericClass(xNewArray.type, true);
		if(init==0){
			XStatementCompiler sc = visitTree(xNewArray.arrayInitialize, type);
			addInstructions(sc);
			setReturn(sc.returnType, xNewArray);
		}else{
			for(int i=0; i<init; i++){
				XStatement s = xNewArray.arraySizes.get(i);
				XStatementCompiler sc = visitTree(s, new XClassPtrClass("int"));
				addInstructions(sc);
			}
			addInstruction(new XInstructionNewArray(type, init), xNewArray);
			setReturn(type, xNewArray);
		}
	}
	
	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		XClass c = getXClassFor(returnExpected);
		if(c.isArray()){
			if(xArrayInitialize.statements==null){
				addInstruction(new XInstructionLoadConstInt(0), xArrayInitialize);
				addInstruction(new XInstructionNewArray(returnExpected, 1), xArrayInitialize);
				setReturn(returnExpected, xArrayInitialize);
			}else{
				int size = xArrayInitialize.statements.size();
				addInstruction(new XInstructionLoadConstInt(size), xArrayInitialize);
				addInstruction(new XInstructionNewArray(returnExpected, 1), xArrayInitialize);
				XClassPtr inner;
				if(returnExpected instanceof XClassPtrGeneric){
					inner = ((XClassPtrGeneric)returnExpected).getGeneric(0);
				}else{
					inner = new XClassPtrClass(XPrimitive.getName(c.getArrayPrimitive()));
				}
				for(int i=0; i<size; i++){
					XStatement s = xArrayInitialize.statements.get(i);
					if(s!=null){
						XStatementCompiler sc = visitTree(s, inner);
						addInstruction(new XInstructionODup(), xArrayInitialize);
						addInstructions(sc);
						XMethodSearch search = new XMethodSearch(c, false, "operator[]", false, true);
						search.applyTypes(new XClassPtrClass("int"));
						makeCall(search, xArrayInitialize);
						addInstruction(new XInstructionPop(), xArrayInitialize);
					}
				}
				setReturn(returnExpected, xArrayInitialize);
			}
		}else{
			compilerError(XMessageLevel.ERROR, "type.not.resolved", xArrayInitialize.line);
		}
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
		if(xscript.runtime.XModifier.isStatic(methodCompiler.getModifier())){
			compilerError(XMessageLevel.ERROR, "this.not.ariviable", xThis.line);
		}else{
			varAccess = new XVarAccess();
			varAccess.codeGen = new XCodeGen();
			varAccess.name = "this";
			varAccess.tree = xThis;
		}
	}

	@Override
	public void visitSuper(XSuper xSuper) {
		shouldNeverCalled();
	}

	private void setReturn(XClassPtr returnType, XTree tree){
		this.returnType = returnType;
	}
	
	private static void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
	private static XInstruction getTarget(XCodeGen codeGen, XTree tree){
		List<XInstruction> list = codeGen.getInstructionList();
		if(list.isEmpty()){
			XInstructionDumyDelete inst = new XInstructionDumyDelete();
			list.add(inst);
			return inst;
		}else{
			return list.get(0);
		}
	}
	
}
