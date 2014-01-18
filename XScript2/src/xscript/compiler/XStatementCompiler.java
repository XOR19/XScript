package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import xscript.compiler.dumyinstruction.XInstructionDumyDelete;
import xscript.compiler.dumyinstruction.XInstructionDumyGetLocalField;
import xscript.compiler.dumyinstruction.XInstructionDumyIf;
import xscript.compiler.dumyinstruction.XInstructionDumyInvokeConstructor;
import xscript.compiler.dumyinstruction.XInstructionDumyJump;
import xscript.compiler.dumyinstruction.XInstructionDumyJumpTargetResolver;
import xscript.compiler.dumyinstruction.XInstructionDumyNIf;
import xscript.compiler.dumyinstruction.XInstructionDumyReadLocal;
import xscript.compiler.dumyinstruction.XInstructionDumySetLocalField;
import xscript.compiler.dumyinstruction.XInstructionDumyStringSwitch;
import xscript.compiler.dumyinstruction.XInstructionDumySwitch;
import xscript.compiler.dumyinstruction.XInstructionDumyTryStart;
import xscript.compiler.dumyinstruction.XInstructionDumyWriteLocal;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree;
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
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionAddInt;
import xscript.runtime.instruction.XInstructionCheckCast;
import xscript.runtime.instruction.XInstructionD2F;
import xscript.runtime.instruction.XInstructionD2I;
import xscript.runtime.instruction.XInstructionD2L;
import xscript.runtime.instruction.XInstructionDup;
import xscript.runtime.instruction.XInstructionEqObject;
import xscript.runtime.instruction.XInstructionF2D;
import xscript.runtime.instruction.XInstructionF2I;
import xscript.runtime.instruction.XInstructionF2L;
import xscript.runtime.instruction.XInstructionGetField;
import xscript.runtime.instruction.XInstructionGetLocalField;
import xscript.runtime.instruction.XInstructionGetStaticField;
import xscript.runtime.instruction.XInstructionI2B;
import xscript.runtime.instruction.XInstructionI2D;
import xscript.runtime.instruction.XInstructionI2F;
import xscript.runtime.instruction.XInstructionI2L;
import xscript.runtime.instruction.XInstructionI2S;
import xscript.runtime.instruction.XInstructionInstanceof;
import xscript.runtime.instruction.XInstructionInvokeDynamic;
import xscript.runtime.instruction.XInstructionInvokeSpecial;
import xscript.runtime.instruction.XInstructionInvokeStatic;
import xscript.runtime.instruction.XInstructionL2D;
import xscript.runtime.instruction.XInstructionL2F;
import xscript.runtime.instruction.XInstructionL2I;
import xscript.runtime.instruction.XInstructionLoadConstBool;
import xscript.runtime.instruction.XInstructionLoadConstClass;
import xscript.runtime.instruction.XInstructionLoadConstDouble;
import xscript.runtime.instruction.XInstructionLoadConstFloat;
import xscript.runtime.instruction.XInstructionLoadConstInt;
import xscript.runtime.instruction.XInstructionLoadConstLong;
import xscript.runtime.instruction.XInstructionLoadConstNull;
import xscript.runtime.instruction.XInstructionLoadConstString;
import xscript.runtime.instruction.XInstructionMonitorEnter;
import xscript.runtime.instruction.XInstructionMonitorExit;
import xscript.runtime.instruction.XInstructionNEqObject;
import xscript.runtime.instruction.XInstructionNew;
import xscript.runtime.instruction.XInstructionNewArray;
import xscript.runtime.instruction.XInstructionODup;
import xscript.runtime.instruction.XInstructionOPop;
import xscript.runtime.instruction.XInstructionOSwap;
import xscript.runtime.instruction.XInstructionPop;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.instruction.XInstructionReturn;
import xscript.runtime.instruction.XInstructionSetField;
import xscript.runtime.instruction.XInstructionSetLocalField;
import xscript.runtime.instruction.XInstructionSetReturn;
import xscript.runtime.instruction.XInstructionSetStaticField;
import xscript.runtime.instruction.XInstructionSmaInt;
import xscript.runtime.instruction.XInstructionThrow;
import xscript.runtime.instruction.XInstructionVarJump;
import xscript.runtime.method.XMethod;

public class XStatementCompiler implements XVisitor {
	
	private XMethodCompiler methodCompiler;
	
	private XVarType returnExpected;
	
	private XVarType returnType;
	
	private XStatementCompiler parent;
	
	private HashMap<String, XVariable> vars;
	
	private List<XInstructionDumyJump> breaks;
	
	private List<XInstructionDumyJump> continues;
	
	private boolean lableUsed;
	
	private String lable;
	
	private XCodeGen codeGen;
	
	private XVarAccess varAccess;
	
	private XInstructionDumyDelete blockFinally;
	
	private XTryHandle finallyTryHandle;
	
	private XInstructionDumyJump blockFinallyEndJump;
	
	public XStatementCompiler(XVarType returnExpected, XStatementCompiler parent, XMethodCompiler methodCompiler){
		this.returnExpected = returnExpected;
		if(parent==null){
			vars = new HashMap<String, XVariable>();
		}
		this.parent = parent;
		this.methodCompiler = methodCompiler;
		codeGen = new XCodeGen();
	}
	
	private XStatementCompiler visitTree(XTree tree, XVarType returnExpected){
		if(tree!=null){
			XStatementCompiler statementCompiler = new XStatementCompiler(returnExpected, this, methodCompiler);
			tree.accept(statementCompiler);
			if(statementCompiler.returnType==null && varAccess==null){
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
		XStatementCompiler sc = visitTree(tree, XAnyType.type);
		XVarAccess varAccess = sc.getVarAccess();
		if(varAccess==null){
			compilerError(XMessageLevel.ERROR, "no.varAccess", tree.line);
		}
		return varAccess;
	}
	
	private XVarAccess visitClassAccess(XTree tree){
		XStatementCompiler sc = visitTree(tree, null);
		XVarAccess varAccess = sc.getVarAccess();
		if(varAccess==null){
			compilerError(XMessageLevel.ERROR, "no.varAccess", tree.line);
		}
		return varAccess;
	}
	
	public HashMap<String, XVariable> getAllVars(){
		HashMap<String, XVariable> map;
		if(parent==null){
			map = new HashMap<String, XVariable>();
		}else{
			map = parent.getAllVars();
		}
		if(vars!=null){
			map.putAll(vars);
		}
		return map;
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
			XResolvedVariable rv = getVariable(varAccess, varAccess.tree);
			if(rv.field==null){
				if(rv.variable!=null){
					addInstructions(varAccess.codeGen);
					addInstruction(new XInstructionDumyReadLocal(rv.variable), varAccess.tree);
					setReturn(rv.variable.type, varAccess.tree);
				}
			}else{
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(instructionGetStaticField(rv.field), varAccess.tree);
				}else{
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					addInstructions(varAccess.codeGen);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(instructionGetLocalField(0, rv.field), varAccess.tree);
						}else{
							addInstruction(instructionGetField(rv.field), varAccess.tree);
						}
					}else{
						addInstruction(instructionGetLocalField(varAccess.variable, rv.field), varAccess.tree);
					}
				}
				setReturn(getVarTypeForFieldType(rv.field, varAccess.declaringClass), varAccess.tree);
			}
			if(returnType==null){
				setReturn(null, varAccess.tree);
			}
		}
		return codeGen;
	}
	
	private XVarType getVarTypeForFieldType(XField field, XVarType declaringClass){
		return getVarTypeFor(field.getType(), field.getDeclaringClass(), declaringClass==null?methodCompiler.getDeclaringClassVarType():declaringClass);
	}
	
	private XVarType getVarTypeFor(XClassPtr classPtr, XClass realDeclaringClass, XVarType declaringClass){
		XVarType type = declaringClass.getSuperClass(realDeclaringClass.getName());
		XVarType generics[];
		if(type instanceof XSingleType){
			generics = ((XSingleType)type).generics;
		}else{
			generics = null;
		}
		return XVarType.getVarTypeFor(classPtr, realDeclaringClass.getVirtualMachine(), generics, null);
	}
	
	private XVarType getVarTypeForThis(XType type, boolean doError){
		XClassPtr classPtr = methodCompiler.getGenericClass(type, doError);
		return XVarType.getVarTypeFor(classPtr, methodCompiler.getDeclaringClass().getVirtualMachine(), null, null);
	}
	
	private XVarType getVarTypeForName(String name){
		XClass c = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass(name);
		XClassPtr ptr;
		if(c.getGenericParams()>0){
			XClassPtr generics[] = new XClassPtr[c.getGenericParams()];
			for(int i=0; i<generics.length; i++){
				generics[i] = new XClassPtrClassGeneric(name, c.getGenericInfo(i).getName());
			}
			ptr = new XClassPtrGeneric(name, generics);
		}else{
			ptr = new XClassPtrClass(name);
		}
		return XVarType.getVarTypeFor(ptr, c.getVirtualMachine(), null, null);
	}
	
	private XVarType getVarTypeForThis(XClassPtr classPtr){
		return XVarType.getVarTypeFor(classPtr, methodCompiler.getDeclaringClass().getVirtualMachine(), null, null);
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
	
	private void addBreak(XJump jump, XBreak xBreak, boolean any){
		if(blockFinally!=null){
			jump.addJump().target = blockFinally;
		}
		if(breaks!=null){
			any |= true;
			if(xBreak.lable==null || (lable!=null && lable.equals(xBreak.lable))){
				if(xBreak.lable!=null)
					lableUsed = true;
				breaks.add(jump.addJump());
				return;
			}
		}
		if(parent!=null){
			parent.addBreak(jump, xBreak, any);
			return;
		}
		if(any)
			compilerError(XMessageLevel.ERROR, "break.lablenotfound", xBreak.line, xBreak.lable);
		compilerError(XMessageLevel.ERROR, "break.nobreakpoint", xBreak.line);
	}
	
	private void addContinue(XJump jump, XContinue xContinue, boolean any){
		if(blockFinally!=null){
			jump.addJump().target = blockFinally;
		}
		if(continues!=null){
			any |= true;
			if(xContinue.lable==null || (lable!=null && lable.equals(xContinue.lable))){
				if(xContinue.lable!=null)
					lableUsed = true;
				continues.add(jump.addJump());
				return;
			}
		}
		if(parent!=null){
			parent.addContinue(jump, xContinue, any);
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
		XClassCompiler classCompiler = new XClassCompiler(compiler, xClassDef.name, new XMessageClass(compiler, fullName), methodCompiler.getImportHelper(), methodCompiler);
		HashMap<String, XVariable> vars = getAllVars();
		methodCompiler.addClass(classCompiler, xClassDef.line);
		classCompiler.registerClass(xClassDef);
		classCompiler.onRequest();
		classCompiler.addVars(vars);
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
		varAccess.name = xIdent.name;
		varAccess.tree = xIdent;
		setReturn(XAnyType.type, xIdent);
	}

	@Override
	public void visitType(XType xType) {
		varAccess = new XVarAccess();
		varAccess.name = null;
		varAccess.tree = xType;
		if(xType.array>0 || xType.typeParam!=null){
			varAccess.declaringClass = getVarTypeForThis(xType, true);
			varAccess.isStatic = true;
		}else{
			String rname = xType.name.name;
			int s=-1;
			String next = null;
			while(true){
				XClassPtr c = methodCompiler.getGenericClass(xType, false);
				if(c!=null){
					varAccess.declaringClass = getVarTypeForThis(c);
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
					XResolvedVariable rv = getVariable(varAccess, xType);
					if(rv.field==null){
						if(rv.variable!=null){
							varAccess.variable = rv.variable;
							varAccess.declaringClass = rv.variable.type;
						}
					}else{
						if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
							if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
								compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
							varAccess.codeGen = new XCodeGen();
							varAccess.codeGen.addInstruction(instructionGetStaticField(rv.field), xType.line.startLine);
						}else{
							if(rv.isStatic)
								compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
							if(varAccess.codeGen==null)
								varAccess.codeGen = new XCodeGen();
							if(varAccess.variable==null){
								if(varAccess.declaringClass==null){
									varAccess.codeGen.addInstruction(instructionGetLocalField(0, rv.field), xType.line.startLine);
								}else{
									varAccess.codeGen.addInstruction(instructionGetField(rv.field), xType.line.startLine);
								}
							}else{
								varAccess.codeGen.addInstruction(instructionGetLocalField(varAccess.variable, rv.field), xType.line.startLine);
							}
						}
						varAccess.variable = null;
						varAccess.declaringClass = getVarTypeForFieldType(rv.field, varAccess.declaringClass);
					}
					varAccess.name = na[i];
				}
				setReturn(XAnyType.type, xType);
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
		var.type = getVarTypeForThis(xVarDecl.type, true);
		var.name = xVarDecl.name;
		if(getVariable(var.name)!=null){
			compilerError(XMessageLevel.ERROR, "variable.duplicated", xVarDecl.line, var.name);
		}else{
			addVariable(var);
		}
		addInstruction(var.start = new XInstructionDumyDelete(), xVarDecl);
		if(xVarDecl.init!=null){
			XStatementCompiler sc = visitTree(xVarDecl.init, var.type);
			addInstructions(sc);
			addInstruction(new XInstructionDumyWriteLocal(var), xVarDecl);
			if(var.type.getPrimitiveID()==XPrimitive.OBJECT){
				addInstruction(new XInstructionOPop(), xVarDecl);
			}else{
				addInstruction(new XInstructionPop(), xVarDecl);
			}
		}
	}

	private XMethodSearch searchMethod(XVarType varType, String name){
		return searchMethod(varType, false, name, false, true);
	}
	
	private XMethodSearch searchMethod(XVarType varType, boolean shouldBeStatic, String name, boolean specialInvoke){
		return searchMethod(varType, shouldBeStatic, name, specialInvoke, true);
	}
	
	private XMethodSearch searchMethod(XVarType varType, boolean shouldBeStatic, String name, boolean specialInvoke, boolean lookIntoParents){
		return new XMethodSearch(methodCompiler.getDeclaringClass().getVirtualMachine(), varType, shouldBeStatic, name, specialInvoke, lookIntoParents);
	}
	
	public String getSyntheticVarName(String name){
		String sname = "$"+name;
		if(getVariable(sname)!=null){
			int i = 0;
			while(getVariable(sname+"_"+i)!=null)i++;
			sname+="_"+i;
		}
		return sname;
	}
	
	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		if(parent==null){
			vars = new HashMap<String, XVariable>();
			if(!xscript.runtime.XModifier.isStatic(methodCompiler.getModifier())){
				XVariable variable = new XVariable();
				variable.modifier = xscript.runtime.XModifier.FINAL;
				variable.type = methodCompiler.getDeclaringClassVarType();
				variable.name = "this";
				addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
				addVariable(variable);
			}
			XClassCompiler decl = methodCompiler.getDeclaringClassCompiler();
			if(methodCompiler.isConstructor() && !xscript.runtime.XModifier.isStatic(methodCompiler.getModifier())){
				XVariable enumNameID = null;
				XVariable enumOrdinalID = null;
				XVariable outerID = null;
				if(decl.isEnum()){
					XVariable variable = new XVariable();
					variable.modifier = xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.SYNTHETIC;
					variable.type = getVarTypeForName("xscript.lang.String");
					variable.name = getSyntheticVarName("name");
					addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
					addVariable(variable);
					enumNameID = variable;
					variable = new XVariable();
					variable.modifier = xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.SYNTHETIC;
					variable.type = getPrimitiveType(XPrimitive.INT);
					variable.name = getSyntheticVarName("ordinal");
					addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
					addVariable(variable);
					enumOrdinalID = variable;
				}else if(decl.getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(decl.getModifier())){
					XVariable variable = new XVariable();
					variable.modifier = xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.SYNTHETIC;
					variable.type = getVarTypeForName(decl.getOuterClass().getName());
					variable.name = getSyntheticVarName("outer");
					addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
					addVariable(variable);
					outerID = variable;
				}else if(decl.getOuterMethod()!=null && !xscript.runtime.XModifier.isStatic(decl.getModifier())){
					XVariable variable = new XVariable();
					variable.modifier = xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.SYNTHETIC;
					variable.type = getVarTypeForName(decl.getOuterMethod().getDeclaringClass().getName());
					variable.name = getSyntheticVarName("outer");
					addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
					addVariable(variable);
					outerID = variable;
				}
				addInstructions(visitTree(xMethodDecl.paramTypes));
				List<XVarType> superClasses = methodCompiler.getDeclaringClassVarType().getDirectSuperClasses();
				boolean selvInvokeOk = true;
				boolean selvInvoke = false;
				if(xMethodDecl.superConstructors!=null){
					for(XStatement s:xMethodDecl.superConstructors){
						if(s instanceof XMethodCall){
							XMethodCall mc = (XMethodCall) s;
							XVarAccess varAccess = visitClassAccess(mc.method);
							if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
								XVarType sp = varAccess.declaringClass;
								if(sp.getXClass().getName().equals("xscript.lang.Enum")){
									compilerError(XMessageLevel.ERROR, "method.enumconstructor.not.allowed", mc.line);
									continue;
								}
								ListIterator<XVarType> i = superClasses.listIterator();
								boolean found = false;
								while(i.hasNext()){
									if(sp.equals(i.next())){
										i.remove();
										found = true;
										break;
									}
								}
								if(found){
									if(selvInvoke){
										compilerError(XMessageLevel.ERROR, "allready.selvinvoked", mc.line);
									}
									if(selvInvokeOk && decl.isEnum()){
										XClass e = decl.getVirtualMachine().getClassProvider().getXClass("xscript.lang.Enum");
										addInstruction(new XInstructionDumyReadLocal(enumNameID), xMethodDecl);
										addInstruction(new XInstructionDumyReadLocal(enumOrdinalID), xMethodDecl);
										addInstruction(new XInstructionDumyInvokeConstructor(e.getMethod("<init>(xscript.lang.String, int)void"), new XClassPtr[0]), xMethodDecl);
									}
									XMethodSearch possibleMethods = searchMethod(sp, false, "<init>", true, false);
									if(mc.typeParam!=null){
										XVarType[] generics = new XVarType[mc.typeParam.size()];
										for(int j=0; j<generics.length; j++){
											generics[j] = getVarTypeForThis(mc.typeParam.get(j), true);
										}
										possibleMethods.applyGenerics(generics);
									}
									XVarType[] types;
									XCodeGen[] codeGens;
									if(mc.params!=null){
										types = new XVarType[mc.params.size()];
										codeGens = new XCodeGen[types.length];
										for(int j=0; j<types.length; j++){
											XStatementCompiler sc = visitTree(mc.params.get(j), XAnyType.type);
											codeGens[j] = sc.getCodeGen();
											types[j] = sc.returnType;
										}
									}else{
										types = new XVarType[0];
										codeGens = new XCodeGen[0];
									}
									XVarType[] realTypes;
									XCodeGen[] realCodeGens;
									XClass xsc = sp.getXClass();
									if(xsc.getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(xsc.getModifier())){
										realTypes = new XVarType[types.length+1];
										realCodeGens = new XCodeGen[codeGens.length+1];
										realTypes[0] = getVarTypeForName(decl.getOuterClass().getName());
										realCodeGens[0] = new XCodeGen();
										realCodeGens[0].addInstruction(new XInstructionDumyReadLocal(outerID), mc.line.startLine);
										for(int j=0; j<types.length; j++){
											realTypes[j+1] = types[j];
											realCodeGens[j+1] = codeGens[j];
										}
									}else if(xsc.getOuterMethod()!=null && !xscript.runtime.XModifier.isStatic(xsc.getModifier())){
										List<XSyntheticField> vars = ((XClassCompiler)xsc).getSyntheticVars();
										int params = vars.size()+1;
										realTypes = new XVarType[types.length+params];
										realCodeGens = new XCodeGen[codeGens.length+params];
										realTypes[0] = getVarTypeForName(decl.getOuterMethod().getDeclaringClass().getName());
										realCodeGens[0] = new XCodeGen();
										realCodeGens[0].addInstruction(new XInstructionDumyReadLocal(outerID), mc.line.startLine);
										for(int j=1; j<params; j++){
											realTypes[j] = getVarTypeFor(vars.get(j-1).getType(), xsc, methodCompiler.getDeclaringClassVarType().getSuperClass(xsc.getName()));
											realCodeGens[j] = new XCodeGen();
											
										}
										for(int j=0; j<types.length; j++){
											realTypes[j+params] = types[j];
											realCodeGens[j+params] = codeGens[j];
										}
									}else{
										realTypes = types;
										realCodeGens = codeGens;
									}
									possibleMethods.applyTypes(realTypes);
									makeCall2(possibleMethods, mc, realCodeGens, true, false);
									selvInvokeOk = false;
								}else{
									if(sp.equals(methodCompiler.getDeclaringClassVarType())){
										if(selvInvokeOk){
											if(selvInvoke){
												compilerError(XMessageLevel.ERROR, "allready.selvinvoked", mc.line);
											}else{
												XMethodSearch possibleMethods = searchMethod(methodCompiler.getDeclaringClassVarType(), false, "<init>", true, false);
												if(mc.typeParam!=null){
													XVarType[] generics = new XVarType[mc.typeParam.size()];
													for(int j=0; j<generics.length; j++){
														generics[j] = getVarTypeForThis(mc.typeParam.get(j), true);
													}
													possibleMethods.applyGenerics(generics);
												}
												XVarType[] types;
												XCodeGen[] codeGens;
												if(mc.params!=null){
													types = new XVarType[mc.params.size()];
													codeGens = new XCodeGen[types.length];
													for(int j=0; j<types.length; j++){
														XStatementCompiler sc = visitTree(mc.params.get(j), XAnyType.type);
														codeGens[j] = sc.getCodeGen();
														types[j] = sc.returnType;
													}
												}else{
													types = new XVarType[0];
													codeGens = new XCodeGen[0];
												}
												XVarType[] realTypes;
												XCodeGen[] realCodeGens;
												if(decl.isEnum()){
													realTypes = new XVarType[types.length+2];
													realCodeGens = new XCodeGen[codeGens.length+2];
													realTypes[0] = getVarTypeForName("xscript.lang.String");
													realCodeGens[0] = new XCodeGen();
													realCodeGens[0].addInstruction(new XInstructionDumyReadLocal(enumNameID), mc.line.startLine);
													realTypes[1] = getPrimitiveType(XPrimitive.INT);
													realCodeGens[1] = new XCodeGen();
													realCodeGens[1].addInstruction(new XInstructionDumyReadLocal(enumOrdinalID), mc.line.startLine);
													for(int j=0; j<types.length; j++){
														realTypes[j+2] = types[j];
														realCodeGens[j+2] = codeGens[j];
													}
												}else if(decl.getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(decl.getModifier())){
													realTypes = new XVarType[types.length+1];
													realCodeGens = new XCodeGen[codeGens.length+1];
													realTypes[0] = getVarTypeForName(decl.getOuterClass().getName());
													realCodeGens[0] = new XCodeGen();
													realCodeGens[0].addInstruction(new XInstructionDumyReadLocal(outerID), mc.line.startLine);
													for(int j=0; j<types.length; j++){
														realTypes[j+1] = types[j];
														realCodeGens[j+1] = codeGens[j];
													}
												}else if(decl.getOuterMethod()!=null && !xscript.runtime.XModifier.isStatic(decl.getModifier())){
													realTypes = new XVarType[types.length+1];
													realCodeGens = new XCodeGen[codeGens.length+1];
													realTypes[0] = getVarTypeForName(decl.getOuterMethod().getDeclaringClass().getName());
													realCodeGens[0] = new XCodeGen();
													realCodeGens[0].addInstruction(new XInstructionDumyReadLocal(outerID), mc.line.startLine);
													for(int j=0; j<types.length; j++){
														realTypes[j+1] = types[j];
														realCodeGens[j+1] = codeGens[j];
													}
												}else{
													realTypes = types;
													realCodeGens = codeGens;
												}
												possibleMethods.applyTypes(realTypes);
												makeCall2(possibleMethods, mc, realCodeGens, true, false);
												selvInvoke = true;
											}
										}else{
											compilerError(XMessageLevel.ERROR, "selvinvoke.not.allowed.here", mc.line);
										}
									}else{
										compilerError(XMessageLevel.ERROR, "not.a.superType", mc.line);
									}
								}
							}else{
								compilerError(XMessageLevel.ERROR, "expect.methodcall", mc.line);
							}
						}else{
							compilerError(XMessageLevel.ERROR, "expect.methodcall", s.line);
						}
					}
				}
				if(!selvInvoke){
					if(selvInvokeOk && decl.isEnum()){
						XClass e = decl.getVirtualMachine().getClassProvider().getXClass("xscript.lang.Enum");
						addInstruction(new XInstructionDumyReadLocal(enumNameID), xMethodDecl);
						addInstruction(new XInstructionDumyReadLocal(enumOrdinalID), xMethodDecl);
						addInstruction(new XInstructionDumyInvokeConstructor(e.getMethod("<init>(xscript.lang.String, int)void"), new XClassPtr[0]), xMethodDecl);
					}
					for(XVarType superClass:superClasses){
						if(superClass.getXClass().getName().equals("xscript.lang.Enum"))
							continue;
						XMethodSearch search = searchMethod(superClass, false, "<init>", true, false);
						if(superClass.getXClass().getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(superClass.getXClass().getModifier())){
							search.applyTypes(new XVarType[]{getVarTypeForName(superClass.getXClass().getOuterClass().getName())});
							addInstruction(new XInstructionDumyReadLocal(outerID), xMethodDecl);
						}else if(superClass.getXClass().getOuterMethod()!=null && !xscript.runtime.XModifier.isStatic(superClass.getXClass().getModifier())){
							List<XSyntheticField> vars = ((XClassCompiler)superClass.getXClass()).getSyntheticVars();
							int params = vars.size()+1;
							XVarType[] types = new XVarType[params];
							types[0] = getVarTypeForName(decl.getOuterMethod().getDeclaringClass().getName());
							addInstruction(new XInstructionDumyReadLocal(outerID), xMethodDecl);
							for(int j=1; j<params; j++){
								types[j] = getVarTypeFor(vars.get(j-1).getType(), superClass.getXClass(), methodCompiler.getDeclaringClassVarType().getSuperClass(superClass.getXClass().getName()));
								
							}
							search.applyTypes(types);
						}else{
							search.applyTypes(new XVarType[0]);
						}
						search.applyReturn(getPrimitiveType(XPrimitive.VOID));
						XCompilerMethod m = search.getMethod();
						if(m==null){
							if(search.isEmpty()){
								compilerError(XMessageLevel.ERROR, "nomethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
							}else{
								compilerError(XMessageLevel.ERROR, "toomanymethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
							}
						}else{
							codeGen.addInstruction(new XInstructionDumyInvokeConstructor(m.method, new XClassPtr[0]), xMethodDecl.line.startLine);
						}
					}
					if((decl.getOuterClass()!=null || decl.getOuterMethod()!=null) && !xscript.runtime.XModifier.isStatic(decl.getModifier())){
						XField field = decl.getSyntheticField("outer");
						if(field!=null){
							addInstruction(new XInstructionDumyReadLocal(outerID), xMethodDecl);
							addInstruction(instructionSetLocalField(0, field), xMethodDecl);
						}
					}
				}
			}else{
				addInstructions(visitTree(xMethodDecl.paramTypes));
			}
			if(xMethodDecl.block!=null){
				addInstructions(visitTree(xMethodDecl.block, null));
			}
			finalizeVars(xMethodDecl);
		}else{
			shouldNeverCalled();
		}
	}

	private void finalizeVars(XTree tree){
		if(vars!=null){
			XInstructionDumyDelete end = new XInstructionDumyDelete();
			addInstruction(end, tree);
			for(XVariable var:vars.values()){
				var.end = end;
				codeGen.addVariable(var);
			}
			vars = null;
		}
	}
	
	@Override
	public void visitBlock(XBlock xBlock) {
		vars = new HashMap<String, XVariable>();
		addInstructions(visitTree(xBlock.statements));
		finalizeVars(xBlock);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		XJump j = new XJump(jump);
		addBreak(j, xBreak, false);
		j.addInstructions(codeGen, xBreak);
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		XInstructionDumyJump jump = new XInstructionDumyJump();
		XJump j = new XJump(jump);
		addContinue(j, xContinue, false);
		j.addInstructions(codeGen, xContinue);
	}

	private XPrimitiveType getPrimitiveType(int primitiveID){
		XClass c = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass(XPrimitive.getName(primitiveID));
		return new XPrimitiveType(c);
	}
	
	@Override
	public void visitDo(XDo xDo) {
		List<XInstructionDumyJump> ccontinues = continues = new ArrayList<XInstructionDumyJump>();
		List<XInstructionDumyJump> bbreaks = breaks = new ArrayList<XInstructionDumyJump>();
		XStatementCompiler c1 = visitTree(xDo.block, null);
		continues = breaks = null;
		XStatementCompiler c2 = visitTree(xDo.doWhile, getPrimitiveType(XPrimitive.BOOL));
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
		XStatementCompiler c1 = visitTree(xWhile.doWhile, getPrimitiveType(XPrimitive.BOOL));
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
		c1 = visitTree(xFor.doWhile, getPrimitiveType(XPrimitive.BOOL));
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
		finalizeVars(xFor);
	}

	@Override
	public void visitIf(XIf xIf) {
		XStatementCompiler c = visitTree(xIf.iif, getPrimitiveType(XPrimitive.BOOL));
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

	private boolean anyFinallyBlocks(){
		if(blockFinally==null){
			return parent==null?false:parent.anyFinallyBlocks();
		}
		return true;
	}
	
	private void addReturn(XJump jump){
		if(blockFinally!=null){
			jump.addJump().target = blockFinally;
		}
		if(parent!=null){
			parent.addReturn(jump);
			return;
		}
		jump.addJump().target = null;
	}
	
	@Override
	public void visitReturn(XReturn xReturn) {
		if(xReturn.statement==null){
			XClass c= methodCompiler.getGenericReturnType().getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
			if(XPrimitive.getPrimitiveID(c)!=XPrimitive.VOID){
				compilerError(XMessageLevel.ERROR, "nonevoidreturn", xReturn.line);
			}
		}else{
			XStatementCompiler c = visitTree(xReturn.statement, getVarTypeForThis(methodCompiler.getGenericReturnType()));
			addInstructions(c);
		}
		if(anyFinallyBlocks()){
			if(methodCompiler.getReturnTypePrimitive()!=XPrimitive.VOID)
				addInstruction(new XInstructionSetReturn(), xReturn);
			XJump jump = new XJump(new XInstructionDumyJump());
			addReturn(jump);
			jump.addInstructions(codeGen, xReturn);
		}else{
			addInstruction(new XInstructionReturn(), xReturn);
		}
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		XStatementCompiler c = visitTree(xThrow.statement, getVarTypeForName("xscript.lang.Throwable"));
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
		XStatementCompiler sc = visitTree(xSynchroized.ident, getVarTypeForName("xscript.lang.Object"));
		addInstructions(sc);
		addInstruction(new XInstructionMonitorEnter(), xSynchroized);
		startFinallyBlock(xSynchroized);
		sc = visitTree(xSynchroized.block, null);
		addInstructions(sc);
		addInstruction(new XInstructionMonitorExit(), xSynchroized);
		blockFinallyEndJump = new XInstructionDumyJump();
		startFinally(xSynchroized);
		addInstruction(new XInstructionOSwap(), xSynchroized);
		addInstruction(new XInstructionMonitorExit(), xSynchroized);
		endFinally(xSynchroized);
	}
	
	private void startFinallyBlock(XTree tree){
		blockFinally = new XInstructionDumyDelete();
		finallyTryHandle = new XTryHandle();
		codeGen.addTryHandler(finallyTryHandle);
		XInstructionDumyTryStart ts = new XInstructionDumyTryStart();
		ts.handle = finallyTryHandle;
		addInstruction(finallyTryHandle.startInstruction = ts, tree);
	}
	
	private void startFinally(XTree tree){
		if(blockFinallyEndJump==null)
			blockFinallyEndJump = new XInstructionDumyJumpTargetResolver();
		finallyTryHandle.endInstruction = new XInstructionDumyDelete();
		addInstruction(finallyTryHandle.endInstruction, tree);
		addInstruction(blockFinallyEndJump, tree);
		addInstruction(blockFinally, tree);
		addInstruction(new XInstructionLoadConstNull(), tree);
		XInstructionDumyDelete dd = new XInstructionDumyDelete();
		finallyTryHandle.jumpTargets.put(new XClassPtrClass("xscript.lang.Throwable"), dd);
		addInstruction(dd, tree);
	}
	
	private void endFinally(XTree tree){
		addInstruction(new XInstructionVarJump(), tree);
		addInstruction(blockFinallyEndJump.target = new XInstructionDumyDelete(), tree);
	}
	
	@Override
	public void visitConstant(XConstant xConstant) {
		int primitiveID;
		Class<?> c = xConstant.value.getType();
		if(c==null){
			addInstruction(new XInstructionLoadConstNull(), xConstant);
			setReturn(XAnyType.type, xConstant);
			return;
		}else if(c==Boolean.class){
			addInstruction(new XInstructionLoadConstBool(xConstant.value.getBool()), xConstant);
			primitiveID = XPrimitive.BOOL;
		}else if(c==Character.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getChar()), xConstant);
			primitiveID = XPrimitive.CHAR;
		}else if(c==Byte.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getByte()), xConstant);
			primitiveID = XPrimitive.BYTE;
		}else if(c==Short.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getShort()), xConstant);
			primitiveID = XPrimitive.SHORT;
		}else if(c==Integer.class){
			addInstruction(new XInstructionLoadConstInt(xConstant.value.getInt()), xConstant);
			primitiveID = XPrimitive.INT;
		}else if(c==Long.class){
			addInstruction(new XInstructionLoadConstLong(xConstant.value.getLong()), xConstant);
			primitiveID = XPrimitive.LONG;
		}else if(c==Float.class){
			addInstruction(new XInstructionLoadConstFloat(xConstant.value.getFloat()), xConstant);
			primitiveID = XPrimitive.FLOAT;
		}else if(c==Double.class){
			addInstruction(new XInstructionLoadConstDouble(xConstant.value.getDouble()), xConstant);
			primitiveID = XPrimitive.DOUBLE;
		}else if(c==String.class){
			addInstruction(new XInstructionLoadConstString(xConstant.value.getString()), xConstant);
			setReturn(getVarTypeForName("xscript.lang.String"), xConstant);
			return;
		}else{
			shouldNeverCalled();
			return;
		}
		setReturn(getPrimitiveType(primitiveID), xConstant);
	}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		XVarAccess varAccess = visitVarAccess(xMethodCall.method);
		addInstructions(varAccess.codeGen);
		boolean addThis = false;
		if(varAccess.variable!=null){
			addInstruction(new XInstructionDumyReadLocal(varAccess.variable), xMethodCall);
		}else{
			if(varAccess.declaringClass==null){
				addThis = true;
			}
		}
		XVarType varType;
		if(varAccess.declaringClass==null){
			varType = methodCompiler.getDeclaringClassVarType();
		}else{
			varType = varAccess.declaringClass;
		}
		XMethodSearch possibleMethods = searchMethod(varType, varAccess.isStatic, varAccess.name, varAccess.specialInvoke);
		if(xMethodCall.typeParam!=null){
			XVarType[] generics = new XVarType[xMethodCall.typeParam.size()];
			for(int i=0; i<generics.length; i++){
				generics[i] = getVarTypeForThis(xMethodCall.typeParam.get(i), true);
			}
			possibleMethods.applyGenerics(generics);
		}
		XVarType[] types;
		XCodeGen[] codeGens;
		if(xMethodCall.params!=null){
			types = new XVarType[xMethodCall.params.size()];
			codeGens = new XCodeGen[types.length];
			for(int i=0; i<types.length; i++){
				XStatementCompiler sc = visitTree(xMethodCall.params.get(i), XAnyType.type);
				codeGens[i] = sc.getCodeGen();
				types[i] = sc.returnType;
			}
		}else{
			types = new XVarType[0];
			codeGens = new XCodeGen[0];
		}
		possibleMethods.applyTypes(types);
		possibleMethods.applyReturn(returnExpected);
		setReturn(makeCall2(possibleMethods, xMethodCall, codeGens, false, addThis), xMethodCall);
	}
	
	private XVarType makeCall(XMethodSearch possibleMethods, XTree tree, XCodeGen[] codeGens) {
		return makeCall2(possibleMethods, tree, codeGens, false, false);
	}

	private XClassPtr[] makeMethodGenerics(XCompilerMethod method, XMethodSearch possibleMethods, XTree tree){
		XClassPtr[] generics = new XClassPtr[method.method.getGenericParams()];
		if(possibleMethods.getGenerics()==null){
			if(possibleMethods.getTypes()!=null){
				for(int i=0; i<method.method.getParamCount(); i++){
					XClassPtr param = method.method.getParams()[i];
					XVarType type = possibleMethods.getTypes()[i];
					tryResolve(generics, method.method, param, type);
				}
			}
			if(possibleMethods.getReturnType()!=null){
				XClassPtr param = method.method.getReturnTypePtr();
				tryResolve(generics, method.method, param, possibleMethods.getReturnType());
			}
		}else{
			for(int i=0; i<generics.length; i++){
				generics[i] = possibleMethods.getGenerics()[i].getXClassPtr();
			}
		}
		boolean error = false;
		for(int i=0; i<generics.length; i++){
			if(generics[i]==null || generics[i] instanceof XClassPtrErrored){
				error = true;
				compilerError(XMessageLevel.ERROR, "unresolved.generic", tree.line, method.method.getGenericInfo(i).getName());
			}
		}
		if(error)
			return null;
		return generics;
	}
	
	private void tryResolve(XClassPtr[] generics, XMethod method, XClassPtr param, XVarType type){
		if(param instanceof XClassPtrMethodGeneric){
			generics[method.getGenericID(((XClassPtrMethodGeneric) param).genericName)] = type.getXClassPtr();
		}else if(param instanceof XClassPtrGeneric){
			XClassPtrGeneric cpg = (XClassPtrGeneric) param;
			for(int i=0; i<cpg.genericPtrs.length; i++){
				tryResolve(generics, method, cpg.genericPtrs[i], type.getGeneric(i));
			}
		}
	}
	
	private XVarType makeCall2(XMethodSearch possibleMethods, XTree tree, XCodeGen[] codeGens, boolean constructorCall, boolean loadThis) {
		XCompilerMethod m = possibleMethods.getMethod();
		if(m==null){
			if(possibleMethods.isEmpty()){
				if(loadThis){
					String lookFor = methodCompiler.getImportHelper().getStaticImportFor(possibleMethods.getName());
					XVarType type;
					if(lookFor==null){
						List<String> list = methodCompiler.getImportHelper().getStaticIndirectImports();
						if(!list.isEmpty()){
							XVarType[] vt = new XVarType[list.size()];
							for(int i=0; i<vt.length; i++){
								vt[i] = getVarTypeForName(list.get(i));
							}
							if(vt.length==1){
								type = vt[0];
							}else{
								type = new XMultibleType(vt);
							}
						}else{
							type = null;
						}
					}else{
						int index = lookFor.lastIndexOf('.');
						type = getVarTypeForName(lookFor.substring(0, index));
					}
					if(type!=null){
						XMethodSearch search = searchMethod(type, true, possibleMethods.getName(), true, false);
						search.applyTypes(possibleMethods.getTypes());
						search.applyGenerics(possibleMethods.getGenerics());
						search.applyReturn(possibleMethods.getReturnType());
						m = search.getMethod();
					}
				}
				if(m==null){
					compilerError(XMessageLevel.ERROR, "nomethodfor", tree.line, possibleMethods.getDesk());
					return new XErroredType();
				}
			}else{
				compilerError(XMessageLevel.ERROR, "toomanymethodfor", tree.line, possibleMethods.getDesk());
				return new XErroredType();
			}
		}
		boolean shouldBeStatic = loadThis && xscript.runtime.XModifier.isStatic(methodCompiler.getModifier());
		if(!xscript.runtime.XModifier.isStatic(m.method.getModifier()) && loadThis){
			addInstruction(new XInstructionReadLocal(0), tree);
		}
		for(int i=0; i<codeGens.length; i++){
			addInstructions(codeGens[i]);
			makeAutoCast(possibleMethods.getTypes()[i], m.params[i], tree);
		}
		XClassPtr[] generics = makeMethodGenerics(m, possibleMethods, tree);
		if(generics!=null){
			if(xscript.runtime.XModifier.isStatic(m.method.getModifier())){
				addInstruction(new XInstructionInvokeStatic(m.method, generics), tree);
				if(!possibleMethods.shouldBeStatic() && !loadThis){
					addInstruction(new XInstructionOPop(), tree);
					compilerError(XMessageLevel.WARNING, "static.access", tree.line, possibleMethods.getDesk());
				}
			}else{
				if(possibleMethods.shouldBeStatic() || shouldBeStatic){
					compilerError(XMessageLevel.ERROR, "non.static.access", tree.line, possibleMethods.getDesk());
				}
				if(possibleMethods.specialInvoke()){
					if(constructorCall){
						addInstruction(new XInstructionDumyInvokeConstructor(m.method, generics), tree);
					}else{
						addInstruction(new XInstructionInvokeSpecial(m.method, generics), tree);
					}
				}else{
					addInstruction(new XInstructionInvokeDynamic(m.method, generics), tree);
				}
			}
		}
		return m.returnType;
	}
	
	private void makeAutoCast(XVarType from, XVarType to, XTree tree){
		if(from instanceof XErroredType){
			return;
		}
		if(!(to instanceof XAnyType)){
			if(from.canCastTo(to)){
				int prim1 = from.getPrimitiveID();
				int prim2 = to.getPrimitiveID();
				if(prim1!=prim2){
					if(prim1>=2 && prim1<=8 && prim2>=2 && prim2<=8){
						int s1 = prim1-XPrimitive.INT;
						int s2 = prim2-XPrimitive.INT;
						if(s1<0)
							s1=0;
						if(s2<0)
							s2=0;
						if(s1!=s2){
							XInstruction instruction;
							if(s1==0){
								if(s2==1){
									instruction = new XInstructionI2L();
								}else if(s2==2){
									instruction = new XInstructionI2F();
								}else{
									instruction = new XInstructionI2D();
								}
							}else if(s1==1){
								if(s2==2){
									instruction = new XInstructionL2F();
								}else{
									instruction = new XInstructionL2D();
								}
							}else{
								instruction = new XInstructionF2D();
							}
							addInstruction(instruction, tree);
						}
					}
				}
			}else{
				compilerError(XMessageLevel.ERROR, "no.autocast.able", tree.line, from, to);
			}
		}
	}
	
	@Override
	public void visitNew(XNew xNew) {
		//TODO
		
		XVarType newType = getVarTypeForThis(xNew.type, true);
		XMethodSearch search = searchMethod(newType, false, "<init>", true, false);
		addInstruction(new XInstructionNew(newType.getXClassPtr()), xNew);
		addInstruction(new XInstructionODup(), xNew);
		XVarType[] types;
		XCodeGen[] codeGens;
		if(xNew.params!=null){
			types = new XVarType[xNew.params.size()];
			codeGens = new XCodeGen[types.length];
			for(int i=0; i<types.length; i++){
				XStatementCompiler sc = visitTree(xNew.params.get(i), XAnyType.type);
				codeGens[i] = sc.getCodeGen();
				types[i] = sc.returnType;
			}
		}else{
			types = new XVarType[0];
			codeGens = new XCodeGen[0];
		}
		search.applyTypes(types);
		search.applyReturn(getPrimitiveType(XPrimitive.VOID));
		makeCall(search, xNew, codeGens);
		setReturn(newType, xNew);
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
	
	public XResolvedVariable getVariable(XVarAccess varAccess, XTree tree){
		XResolvedVariable rv = new XResolvedVariable();
		if(varAccess==null)
			return rv;
		rv.isStatic = varAccess.isStatic;
		if(varAccess.declaringClass==null){
			rv.variable = getVariable(varAccess.name);
			if(rv.variable==null){
				int outer = 0;
				XClassCompiler cc;
				XClass c=cc=methodCompiler.getDeclaringClassCompiler();
				rv.field = c.getFieldAndParents(varAccess.name);
				while(rv.field==null && c!=null){
					if(xscript.runtime.XModifier.isStatic(c.getModifier()))
						rv.isStatic = true;
					if(c.getOuterClass()!=null){
						c = c.getOuterClass();
					}else if(c.getOuterMethod()!=null){
						c = c.getOuterMethod().getDeclaringClass();
					}else{
						c=null;
					}
					if(c==null)
						break;
					outer++;
					rv.field = c.getFieldAndParents(varAccess.name);
				}
				if(rv.field==null){
					String lookFor = methodCompiler.getImportHelper().getStaticImportFor(varAccess.name);
					if(lookFor==null){
						List<String> list = methodCompiler.getImportHelper().getStaticIndirectImports();
						for(String s:list){
							XClass imp = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass(s);
							rv.field = imp.getField(varAccess.name);
							if(rv.field!=null && xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
								break;
							}
						}
					}else{
						int index = lookFor.lastIndexOf('.');
						XClass imp = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass(lookFor.substring(0, index));
						rv.field = imp.getField(varAccess.name);
					}
					if(rv.field==null){
						compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
					}
				}else{
					if(!rv.isStatic && outer>0){
						if(varAccess.codeGen==null){
							varAccess.codeGen = new XCodeGen();
						}
						outer--;
						XField field = cc.getSyntheticFieldAndParents("outer");
						XVarType declaringClass = methodCompiler.getDeclaringClassVarType();
						declaringClass = getVarTypeForFieldType(field, declaringClass);
						varAccess.codeGen.addInstruction(instructionGetLocalField(0, field), tree.line.startLine);
						while(outer>0){
							cc = (XClassCompiler) cc.getOuterClass();
							field = cc.getSyntheticFieldAndParents("outer");
							declaringClass = getVarTypeForFieldType(field, declaringClass);
							varAccess.codeGen.addInstruction(instructionGetField(field), tree.line.startLine);
							outer--;
						}
						varAccess.declaringClass = declaringClass;
					}
				}
				if(rv.field!=null && xscript.runtime.XModifier.isStatic(methodCompiler.getModifier()) && !xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					compilerError(XMessageLevel.ERROR, "non.static.access", tree.line, rv.field);
					rv.field=null;
				}
			}
		}else if(varAccess.name!=null){
			rv.field = getField(varAccess.declaringClass.getXClasses(), varAccess.name, tree);
			if(rv.field==null){
				compilerError(XMessageLevel.ERROR, "field.notfound", varAccess.tree.line, varAccess.name);
			}
		}
		return rv;
	}
	
	public XField getField(XClass[] classes, String name, XTree tree){
		XField field = null;
		for(int i=0; i<classes.length; i++){
			XField f = classes[i].getFieldAndParents(name);
			if(field==null){
				field = f;
			}else if(field!=f){
				compilerError(XMessageLevel.ERROR, "multible.joise", tree.line, name);
			}
		}
		return field;
	}
	
	private XInstructionGetLocalField instructionGetLocalField(int local, XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incReads();
		}
		return new XInstructionGetLocalField(local, field);
	}
	
	private XInstructionDumyGetLocalField instructionGetLocalField(XVariable local, XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incReads();
		}
		return new XInstructionDumyGetLocalField(local, field);
	}
	
	private XInstructionGetField instructionGetField(XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incReads();
		}
		return new XInstructionGetField(field);
	}
	
	private XInstructionGetStaticField instructionGetStaticField(XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incReads();
		}
		return new XInstructionGetStaticField(field);
	}
	
	private XInstructionSetLocalField instructionSetLocalField(int local, XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incWrites();
		}
		return new XInstructionSetLocalField(local, field);
	}
	
	private XInstructionDumySetLocalField instructionSetLocalField(XVariable local, XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incWrites();
		}
		return new XInstructionDumySetLocalField(local, field);
	}
	
	private XInstructionSetField instructionSetField(XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incWrites();
		}
		return new XInstructionSetField(field);
	}
	
	private XInstructionSetStaticField instructionSetStaticField(XField field){
		if(field instanceof XFieldCompiler){
			((XFieldCompiler) field).incWrites();
		}
		return new XInstructionSetStaticField(field);
	}
	
	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		XOperator op = xOperatorStatement.operator;
		if(op == XOperator.ELEMENT){
			String name = ((XIdent)xOperatorStatement.right).name;
			if(name.equals("class")){
				XVarAccess varAccess = visitClassAccess(xOperatorStatement.left);
				if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
					addInstruction(new XInstructionLoadConstClass(varAccess.declaringClass.getXClassPtr()), xOperatorStatement);
					setReturn(getVarTypeForName("xscript.lang.Class"), xOperatorStatement);
				}else{
					compilerError(XMessageLevel.ERROR, "not.a.class", xOperatorStatement.line);
				}
			}else if(name.equals("this")){
				XVarAccess varAccess = visitClassAccess(xOperatorStatement.left);
				if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
					varAccess.codeGen = new XCodeGen();
					varAccess.isStatic = false;
					XClass c = getXClassFor(varAccess.declaringClass.getXClassPtr());
					XClassCompiler cc = methodCompiler.getDeclaringClassCompiler();
					if(cc==c){
						this.varAccess = new XVarAccess();
						this.varAccess.name = "this";
						this.varAccess.tree = xOperatorStatement;
						setReturn(varAccess.declaringClass, xOperatorStatement);
					}else{
						if(!xscript.runtime.XModifier.isStatic(cc.getModifier())){
							XField field = cc.getSyntheticFieldAndParents("outer");
							varAccess.codeGen.addInstruction(instructionGetLocalField(0, field), xOperatorStatement.line.startLine);
							if(cc.getOuterClass()!=null){
								cc = (XClassCompiler) cc.getOuterClass();
							}else if(cc.getOuterMethod()!=null){
								cc = (XClassCompiler) cc.getOuterMethod().getDeclaringClass();
							}else{
								cc=null;
							}
							while(cc!=c){
								if(cc==null)
									break;
								if(xscript.runtime.XModifier.isStatic(cc.getModifier()))
									break;
								field = cc.getSyntheticFieldAndParents("outer");
								varAccess.codeGen.addInstruction(instructionGetField(field), xOperatorStatement.line.startLine);
								cc = (XClassCompiler) cc.getOuterClass();
							}
						}
						this.varAccess = varAccess;
						if(cc!=c){
							compilerError(XMessageLevel.ERROR, "not.a.outer.class", xOperatorStatement.left.line);
							setReturn(new XErroredType(), xOperatorStatement);
						}else{
							setReturn(getVarTypeForName(cc.getName()), xOperatorStatement);
						}
					}
				}else{
					compilerError(XMessageLevel.ERROR, "not.a.class", xOperatorStatement.line);
				}
			}else{
				if(xOperatorStatement.left instanceof XSuper){
					varAccess = new XVarAccess();
					List<XVarType> l = methodCompiler.getDeclaringClassVarType().getDirectSuperClasses();
					if(l.isEmpty()){
						compilerError(XMessageLevel.ERROR, "unknown.type", xOperatorStatement.line);
					}else if(l.size()==1){
						varAccess.declaringClass = l.get(0);
					}else{
						varAccess.declaringClass = new XMultibleType(l.toArray(new XVarType[l.size()]));
					}
					varAccess.specialInvoke = true;
					setReturn(XAnyType.type, xOperatorStatement);
				}else{
					XStatementCompiler cl = visitTree(xOperatorStatement.left, XAnyType.type);
					varAccess = cl.varAccess;
					if(varAccess==null){
						varAccess = new XVarAccess();
						varAccess.codeGen = new XCodeGen();
						varAccess.codeGen.addInstructions(cl.getCodeGen());
						varAccess.declaringClass = cl.returnType;
						setReturn(XAnyType.type, xOperatorStatement);
					}else{
						XResolvedVariable rv = getVariable(varAccess, xOperatorStatement);
						if(varAccess.name==null){
							varAccess.name = name;
						}else{
							if(rv.field==null){
								if(rv.variable!=null){
									varAccess.variable = rv.variable;
									varAccess.declaringClass = rv.variable.type;
								}
							}else{
								if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
									if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
										compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
									varAccess.codeGen = new XCodeGen();
									varAccess.codeGen.addInstruction(instructionGetStaticField(rv.field), xOperatorStatement.line.startLine);
								}else{
									if(rv.isStatic)
										compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
									if(varAccess.codeGen==null){
										varAccess.codeGen = new XCodeGen();
									}
									if(varAccess.variable==null){
										if(varAccess.declaringClass==null){
											varAccess.codeGen.addInstruction(instructionGetLocalField(0, rv.field), xOperatorStatement.line.startLine);
										}else{
											varAccess.codeGen.addInstruction(instructionGetField(rv.field), xOperatorStatement.line.startLine);
										}
									}else{
										varAccess.codeGen.addInstruction(instructionGetLocalField(varAccess.variable, rv.field), xOperatorStatement.line.startLine);
									}
								}
								varAccess.variable = null;
								varAccess.declaringClass = getVarTypeForFieldType(rv.field, varAccess.declaringClass);
							}
						}
						setReturn(XAnyType.type, xOperatorStatement);
					}
				}
				varAccess.name = name;
				varAccess.tree = xOperatorStatement;
			}
		}else if(op == XOperator.LET){
			if(xOperatorStatement.left instanceof XIndex){
				XIndex index = ((XIndex)xOperatorStatement.left);
				XStatementCompiler s = visitTree(index.array, XAnyType.type);
				XStatementCompiler i = visitTree(index.index, XAnyType.type);
				XStatementCompiler cr = visitTree(xOperatorStatement.right, XAnyType.type);
				addInstructions(s);
				XCodeGen[] codeGens = {i.getCodeGen(), cr.getCodeGen()};
				XMethodSearch methodSearch = searchMethod(s.returnType, "operator[]");
				methodSearch.applyTypes(i.returnType, cr.returnType);
				setReturn(makeCall(methodSearch, index, codeGens), index);
			}else{
				XVarAccess varAccess = visitVarAccess(xOperatorStatement.left);
				XResolvedVariable rv = getVariable(varAccess, xOperatorStatement);
				if(rv.field==null){
					if(rv.variable!=null){
						addInstructions(varAccess.codeGen);
						XStatementCompiler cr = visitTree(xOperatorStatement.right, rv.variable.type);
						addInstructions(cr);
						if(xscript.runtime.XModifier.isFinal(rv.variable.modifier)){
							compilerError(XMessageLevel.ERROR, "write.final.var", xOperatorStatement.line, rv.variable.name);
						}
						addInstruction(new XInstructionDumyWriteLocal(rv.variable), xOperatorStatement);
						setReturn(rv.variable.type, xOperatorStatement);
					}
				}else{
					XVarType ret = getVarTypeForFieldType(rv.field, varAccess.declaringClass);
					XStatementCompiler cr = visitTree(xOperatorStatement.right, ret);
					if(xscript.runtime.XModifier.isFinal(rv.field.getModifier())){
						if(!(methodCompiler.getDeclaringClass()==rv.field.getDeclaringClass() && methodCompiler.isConstructor() && 
								xscript.runtime.XModifier.isStatic(rv.field.getModifier())== xscript.runtime.XModifier.isStatic(methodCompiler.getModifier()))){
							compilerError(XMessageLevel.ERROR, "write.final.field", xOperatorStatement.line, rv.field.getName());
						}
					}
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						addInstructions(cr);
						if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(instructionSetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						addInstructions(cr);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(instructionSetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(instructionSetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(instructionSetLocalField(varAccess.variable, rv.field), xOperatorStatement);
						}
					}
					setReturn(ret, xOperatorStatement);
				}
			}
		}else if(op == XOperator.LETADD || op == XOperator.LETSUB || op == XOperator.LETMUL || op == XOperator.LETDIV || op == XOperator.LETMOD ||
				op == XOperator.LETOR || op == XOperator.LETAND || op == XOperator.LETXOR || op == XOperator.LETSHR || op == XOperator.LETSHL){
			XVarAccess varAccess = visitVarAccess(xOperatorStatement.left);
			XResolvedVariable rv = getVariable(varAccess, xOperatorStatement);
			int primitiveID;
			if(rv.field==null){
				addInstructions(varAccess.codeGen);
				addInstruction(new XInstructionDumyReadLocal(rv.variable), xOperatorStatement);
				if((primitiveID = rv.variable.type.getPrimitiveID())==XPrimitive.OBJECT){
					XStatementCompiler sc = visitTree(xOperatorStatement.right, XAnyType.type);
					XCodeGen[] codeGens = {sc.getCodeGen()};
					XMethodSearch search =searchMethod(rv.variable.type, "operator"+op);
					search.applyTypes(sc.returnType);
					setReturn(makeCall(search, xOperatorStatement, codeGens), xOperatorStatement);
				}else{
					XStatementCompiler sc = visitTree(xOperatorStatement.right, getPrimitiveType(primitiveID));
					addInstructions(sc);
					XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, pid2iid(primitiveID));
					if(inst==null){
						compilerError(XMessageLevel.ERROR, "no.operator.for", xOperatorStatement.line, xOperatorStatement.operator, rv.variable.type);
					}
					addInstruction(inst, xOperatorStatement);
					addInstruction(new XInstructionDumyWriteLocal(rv.variable), xOperatorStatement);
					setReturn(rv.variable.type, xOperatorStatement);
				}
			}else{
				if((primitiveID = rv.field.getTypePrimitive())==XPrimitive.OBJECT){
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(instructionGetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(instructionGetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(instructionGetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(instructionGetLocalField(varAccess.variable, rv.field), xOperatorStatement);
						}
					}
					XStatementCompiler sc = visitTree(xOperatorStatement.right, XAnyType.type);
					XCodeGen[] codeGens = {sc.getCodeGen()};
					XMethodSearch search = searchMethod(rv.variable.type, "operator"+op.op);
					search.applyTypes(sc.returnType);
					setReturn(makeCall(search, xOperatorStatement, codeGens), xOperatorStatement);
				}else{
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(instructionGetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(instructionGetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionODup(), xOperatorStatement);
								addInstruction(instructionGetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(instructionGetLocalField(varAccess.variable, rv.field), xOperatorStatement);
						}
					}
					XStatementCompiler sc = visitTree(xOperatorStatement.right, getPrimitiveType(primitiveID));
					addInstructions(sc);
					XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, pid2iid(primitiveID));
					if(inst==null){
						compilerError(XMessageLevel.ERROR, "no.operator.for", xOperatorStatement.line, xOperatorStatement.operator, rv.variable.type);
					}
					addInstruction(inst, xOperatorStatement);
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						addInstruction(instructionSetStaticField(rv.field), xOperatorStatement);
					}else{
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(instructionSetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(instructionSetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(instructionSetLocalField(varAccess.variable, rv.field), xOperatorStatement);
						}
					}
					setReturn(getVarTypeForFieldType(rv.field, varAccess.declaringClass), xOperatorStatement);
				}
			}
		}else{
			XStatementCompiler cl = visitTree(xOperatorStatement.left, XAnyType.type);
			addInstructions(cl);
			int t1 = cl.returnType.getPrimitiveID();
			if(t1 == XPrimitive.OBJECT){
				XStatementCompiler cr = visitTree(xOperatorStatement.right, XAnyType.type);
				if(op == XOperator.EQ || op == XOperator.NEQ){
					addInstructions(cr);
					if(op == XOperator.EQ){
						addInstruction(new XInstructionEqObject(), xOperatorStatement);
					}else{
						addInstruction(new XInstructionNEqObject(), xOperatorStatement);
					}
					setReturn(getPrimitiveType(XPrimitive.BOOL), xOperatorStatement);
				}else{
					XCodeGen[] codeGens = {cr.getCodeGen()};
					XMethodSearch methodSearch = searchMethod(cl.returnType, "operator"+op.op);
					methodSearch.applyTypes(cr.returnType);
					setReturn(makeCall(methodSearch, xOperatorStatement, codeGens), xOperatorStatement);
				}
			}else{
				if(t1==XPrimitive.BOOL && op==XOperator.OR){
					XStatementCompiler cr = visitTree(xOperatorStatement.right, getPrimitiveType(XPrimitive.BOOL));
					XInstructionDumyIf iif = new XInstructionDumyIf();
					addInstruction(iif, xOperatorStatement);
					addInstruction(new XInstructionLoadConstBool(true), xOperatorStatement);
					XInstructionDumyJump jump = new XInstructionDumyJump();
					addInstruction(jump, xOperatorStatement);
					addInstruction(iif.target = new XInstructionDumyDelete(), xOperatorStatement);
					addInstructions(cr);
					addInstruction(jump.target = new XInstructionDumyDelete(), xOperatorStatement);
					setReturn(getPrimitiveType(XPrimitive.BOOL), xOperatorStatement);
				}else if(t1==XPrimitive.BOOL && op==XOperator.AND){
					XStatementCompiler cr = visitTree(xOperatorStatement.right, getPrimitiveType(XPrimitive.BOOL));
					XInstructionDumyIf iif = new XInstructionDumyIf();
					addInstruction(iif, xOperatorStatement);
					addInstructions(cr);
					XInstructionDumyJump jump = new XInstructionDumyJump();
					addInstruction(jump, xOperatorStatement);
					addInstruction(iif.target = new XInstructionDumyDelete(), xOperatorStatement);
					addInstruction(new XInstructionLoadConstBool(false), xOperatorStatement);
					addInstruction(jump.target = new XInstructionDumyDelete(), xOperatorStatement);
					setReturn(getPrimitiveType(XPrimitive.BOOL), xOperatorStatement);
				}else{
					XStatementCompiler cr = visitTree(xOperatorStatement.right, XAnyType.type);
					addInstructions(cr);
					int t2 = cr.returnType.getPrimitiveID();
					int ret = compNID(t1, t2, xOperatorStatement.line);
					int type = pid2iid(ret);
					XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, type);
					if(inst==null)
						compilerError(XMessageLevel.ERROR, "no.operator.for", xOperatorStatement.line, xOperatorStatement.operator, cl.returnType, cr.returnType);
					addInstruction(inst, xOperatorStatement);
					if(op == XOperator.SMA || op == XOperator.BIG || op == XOperator.SEQ || op == XOperator.BEQ || op == XOperator.EQ || op == XOperator.NEQ || op == XOperator.REQ || op == XOperator.RNEQ){
						ret = XPrimitive.BOOL;
					}else if(op == XOperator.COMP){	
						ret = XPrimitive.INT;
					}
					setReturn(getPrimitiveType(ret), xOperatorStatement);
				}
			}
		}
	}
	
	private XClass getXClassFor(XClassPtr classPtr){
		if(classPtr==null)
			return null;
		return classPtr.getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
	}
	
	private XVarType makeIncOrDec(XVarAccess varAccess, boolean inc, boolean suffix, XOperator op, XTree tree){
		XResolvedVariable rv = getVariable(varAccess, tree);
		int primitiveID;
		XVarType returnType;
		if(rv.field==null){
			addInstructions(varAccess.codeGen);
			addInstruction(new XInstructionDumyReadLocal(rv.variable), tree);
			if((primitiveID = rv.variable.type.getPrimitiveID())==XPrimitive.OBJECT){
				XMethodSearch search = searchMethod(rv.variable.type, "operator"+op.op);
				XCodeGen[] codeGens;
				if(suffix){
					codeGens = new XCodeGen[]{new XCodeGen()};
					codeGens[0].addInstruction(new XInstructionLoadConstInt(0), tree.line.startLine);
					search.applyTypes(getPrimitiveType(XPrimitive.INT));
				}else{
					search.applyTypes();
					codeGens = new XCodeGen[0];
				}
				returnType = makeCall(search, tree, codeGens);
			}else{
				if(xscript.runtime.XModifier.isFinal(rv.variable.modifier)){
					compilerError(XMessageLevel.ERROR, "write.final.var", tree.line, rv.variable.name);
				}
				if(suffix)
					addInstruction(new XInstructionDup(), tree);
				int iid = pid2iid(primitiveID);
				if(iid==XOperatorHelper.INTINST){
					addInstruction(new XInstructionLoadConstInt(1), tree);
				}else if(iid==XOperatorHelper.LONGINST){
					addInstruction(new XInstructionLoadConstLong(1), tree);
				}else if(iid==XOperatorHelper.FLOATINST){
					addInstruction(new XInstructionLoadConstFloat(1), tree);
				}else if(iid==XOperatorHelper.DOUBLEINST){
					addInstruction(new XInstructionLoadConstDouble(1), tree);
				}
				XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, iid);
				if(inst==null){
					compilerError(XMessageLevel.ERROR, "no.operator.for", tree.line, op, rv.variable.type);
				}
				addInstruction(inst, tree);
				addInstruction(new XInstructionDumyWriteLocal(rv.variable), tree);
				returnType = rv.variable.type;
				if(suffix)
					addInstruction(new XInstructionPop(), tree);
			}
		}else{
			if((primitiveID = rv.field.getTypePrimitive())==XPrimitive.OBJECT){
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(instructionGetStaticField(rv.field), tree);
				}else{
					addInstructions(varAccess.codeGen);
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(instructionGetLocalField(0, rv.field), tree);
						}else{
							addInstruction(instructionGetField(rv.field), tree);
						}
					}else{
						addInstruction(instructionGetLocalField(varAccess.variable, rv.field), tree);
					}
				}
				XMethodSearch search = searchMethod(rv.variable.type, "operator"+op.op);
				XCodeGen[] codeGens;
				if(suffix){
					codeGens = new XCodeGen[]{new XCodeGen()};
					codeGens[0].addInstruction(new XInstructionLoadConstInt(0), tree.line.startLine);
					search.applyTypes(getPrimitiveType(XPrimitive.INT));
				}else{
					search.applyTypes();
					codeGens = new XCodeGen[0];
				}
				returnType = makeCall(search, tree, codeGens);
			}else{
				if(xscript.runtime.XModifier.isFinal(rv.field.getModifier())){
					compilerError(XMessageLevel.ERROR, "write.final.field", tree.line, rv.field.getName());
				}
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(instructionGetStaticField(rv.field), tree);
				}else{
					addInstructions(varAccess.codeGen);
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(instructionGetLocalField(0, rv.field), tree);
						}else{
							addInstruction(new XInstructionODup(), tree);
							addInstruction(instructionGetField(rv.field), tree);
						}
					}else{
						addInstruction(instructionGetLocalField(varAccess.variable, rv.field), tree);
					}
				}
				if(suffix)
					addInstruction(new XInstructionDup(), tree);
				int iid = pid2iid(primitiveID);
				if(iid==XOperatorHelper.INTINST){
					addInstruction(new XInstructionLoadConstInt(1), tree);
				}else if(iid==XOperatorHelper.LONGINST){
					addInstruction(new XInstructionLoadConstLong(1), tree);
				}else if(iid==XOperatorHelper.FLOATINST){
					addInstruction(new XInstructionLoadConstFloat(1), tree);
				}else if(iid==XOperatorHelper.DOUBLEINST){
					addInstruction(new XInstructionLoadConstDouble(1), tree);
				}
				XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, iid);
				if(inst==null){
					compilerError(XMessageLevel.ERROR, "no.operator.for", tree.line, op, rv.variable.type);
				}
				addInstruction(inst, tree);
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					addInstruction(instructionSetStaticField(rv.field), tree);
				}else{
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(instructionSetLocalField(0, rv.field), tree);
						}else{
							addInstruction(instructionSetField(rv.field), tree);
						}
					}else{
						addInstruction(instructionSetLocalField(varAccess.variable, rv.field), tree);
					}
				}
				if(suffix)
					addInstruction(new XInstructionPop(), tree);
				returnType = getVarTypeForFieldType(rv.field, varAccess.declaringClass);
			}
		}
		return returnType;
	}
	
	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		XStatementCompiler s = visitTree(xOperatorPrefixSuffix.statement, XAnyType.type);
		XVarAccess varAccess = s.varAccess;
		if(xOperatorPrefixSuffix.prefix!=null){
			for(XOperator op:xOperatorPrefixSuffix.prefix){
				if(varAccess!=null && (op==XOperator.INC || op==XOperator.DEC)){
					returnType = makeIncOrDec(varAccess, op==XOperator.INC, false, op, xOperatorPrefixSuffix);
					varAccess = null;
				}else{
					if(varAccess!=null){
						varAccess = null;
						addInstructions(s);
						returnType = s.returnType;
					}
					int t = returnType.getPrimitiveID();
					if(t == XPrimitive.OBJECT){
						XMethodSearch methodSearch = searchMethod(returnType, "operator"+op.op);
						methodSearch.applyTypes();
						returnType = makeCall(methodSearch, xOperatorPrefixSuffix, new XCodeGen[0]);
					}else{
						if(op==XOperator.INC || op==XOperator.DEC){
							compilerError(XMessageLevel.ERROR, "operator.notallowed.here", xOperatorPrefixSuffix.line, op);
						}else{
							int type = pid2iid(t);
							XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, type);
							if(inst==null)
								compilerError(XMessageLevel.ERROR, "no.operator.for", xOperatorPrefixSuffix.line, op, returnType);
							addInstruction(inst, xOperatorPrefixSuffix);
						}
					}
				}
			}
		}
		if(xOperatorPrefixSuffix.suffix!=null){
			for(XOperator op:xOperatorPrefixSuffix.suffix){
				if(varAccess!=null && (op==XOperator.INCS || op==XOperator.DECS)){
					returnType = makeIncOrDec(varAccess, op==XOperator.INCS, true, op, xOperatorPrefixSuffix);
					varAccess = null;
				}else{
					if(varAccess!=null){
						varAccess = null;
						addInstructions(s);
						returnType = s.returnType;
					}
					int t = returnType.getPrimitiveID();
					if(t == XPrimitive.OBJECT){
						XMethodSearch methodSearch = searchMethod(returnType, "operator"+op.op);
						if(op==XOperator.INCS || op==XOperator.DECS){
							XCodeGen[] codeGens = {new XCodeGen()};
							codeGens[0].addInstruction(new XInstructionLoadConstInt(0), xOperatorPrefixSuffix.line.startLine);
							methodSearch.applyTypes(getPrimitiveType(XPrimitive.INT));
							returnType = makeCall(methodSearch, xOperatorPrefixSuffix, codeGens);
						}else{
							methodSearch.applyTypes();
							returnType = makeCall(methodSearch, xOperatorPrefixSuffix, new XCodeGen[0]);
						}
					}else{
						if(op==XOperator.INCS || op==XOperator.DECS){
							compilerError(XMessageLevel.ERROR, "operator.notallowed.here", xOperatorPrefixSuffix.line, op);
						}else{
							int type = pid2iid(t);
							XInstruction inst = XOperatorHelper.makeInstructionForOperator(op, type);
							if(inst==null)
								compilerError(XMessageLevel.ERROR, "no.operator.for", xOperatorPrefixSuffix.line, op, returnType);
							addInstruction(inst, xOperatorPrefixSuffix);
						}
					}
				}
			}
		}
		setReturn(returnType, xOperatorPrefixSuffix);
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		XStatementCompiler s = visitTree(xIndex.array, XAnyType.type);
		XStatementCompiler i = visitTree(xIndex.index, XAnyType.type);
		addInstructions(s);
		XCodeGen[] codeGens = {i.getCodeGen()};
		XMethodSearch methodSearch = searchMethod(s.returnType, "operator[]");
		methodSearch.applyTypes(i.returnType);
		setReturn(makeCall(methodSearch, xIndex, codeGens), xIndex);
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		XStatementCompiler sc = visitTree(xIfOperator.left, getPrimitiveType(XPrimitive.BOOL));
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
		XStatementCompiler a = visitTree(xCast.statement, XAnyType.type);
		addInstructions(a);
		XVarType castTo = getVarTypeForThis(xCast.type, true);
		int prim1 = a.returnType.getPrimitiveID();
		int prim2 = castTo.getPrimitiveID();
		if(prim1!=prim2){
			if(prim1>=2 && prim1<=8 && prim2>=2 && prim2<=8){
				int s1 = prim1-XPrimitive.INT;
				int s2 = prim2-XPrimitive.INT;
				if(s1<0)
					s1=0;
				if(s2<0)
					s2=0;
				if(s1!=s2){
					XInstruction instruction;
					if(s1==0){
						if(s2==1){
							instruction = new XInstructionI2L();
						}else if(s2==2){
							instruction = new XInstructionI2F();
						}else{
							instruction = new XInstructionI2D();
						}
					}else if(s1==1){
						if(s2==0){
							instruction = new XInstructionL2I();
						}else if(s2==2){
							instruction = new XInstructionL2F();
						}else{
							instruction = new XInstructionL2D();
						}
					}else  if(s1==2){
						if(s2==0){
							instruction = new XInstructionF2I();
						}else if(s2==1){
							instruction = new XInstructionF2L();
						}else{
							instruction = new XInstructionF2D();
						}
					}else{
						if(s2==0){
							instruction = new XInstructionD2I();
						}else if(s2==1){
							instruction = new XInstructionD2L();
						}else{
							instruction = new XInstructionD2F();
						}
					}
					addInstruction(instruction, xCast);
				}
				if(prim2==XPrimitive.BYTE){
					addInstruction(new XInstructionI2B(), xCast);
				}else if(prim2==XPrimitive.SHORT){
					addInstruction(new XInstructionI2S(), xCast);
				}
			}else{
				compilerError(XMessageLevel.ERROR, "no.cast.able", xCast.line, a.returnType, castTo);
			}
		}else if(prim1 == XPrimitive.OBJECT){
			if(a.returnType.canCastTo(castTo) || castTo.canCastTo(a.returnType)){
				addInstruction(new XInstructionCheckCast(castTo.getXClassPtr()), xCast);
			}else{
				compilerError(XMessageLevel.ERROR, "no.cast.able", xCast.line, a.returnType, castTo);
			}
		}
		setReturn(castTo, xCast);
	}

	@Override
	public void visitLambda(XLambda xLambda) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTry(XTry xTry) {
		if(xTry.finallyBlock!=null || xTry.resource!=null){
			System.out.println(xTry.line);
			startFinallyBlock(xTry);
		}
		
		if(xTry.resource!=null){
			vars = new HashMap<String, XVariable>();
			for(XVarDecls varDecls:xTry.resource){
				for(XVarDecl varDecl:varDecls.varDecls){
					XVariable var = new XVariable();
					var.varDecl = varDecl;
					var.modifier = varDecl.modifier==null?0:varDecl.modifier.modifier;
					var.type = getVarTypeForThis(varDecl.type, true);
					var.name = varDecl.name;
					if(getVariable(var.name)!=null){
						compilerError(XMessageLevel.ERROR, "variable.duplicated", varDecl.line, var.name);
					}else{
						addVariable(var);
					}
					addInstruction(var.start = new XInstructionDumyDelete(), varDecl);
					addInstruction(new XInstructionLoadConstNull(), varDecl);
					addInstruction(new XInstructionDumyWriteLocal(var), varDecl);
					addInstruction(new XInstructionOPop(), varDecl);
				}
			}
		}
		
		
		XTryHandle tryHandle = null;
		if(xTry.catchs!=null){
			tryHandle = new XTryHandle();
			codeGen.addTryHandler(tryHandle);
			XInstructionDumyTryStart ts = new XInstructionDumyTryStart();
			ts.handle = tryHandle;
			addInstruction(tryHandle.startInstruction = ts, xTry);
		}
		
		if(xTry.resource!=null){
			for(XVarDecls varDecls:xTry.resource){
				for(XVarDecl varDecl:varDecls.varDecls){
					XVariable var = vars.get(varDecl.name);
					if(varDecl.init!=null && var!=null){
						XStatementCompiler sc = visitTree(varDecl.init, var.type);
						addInstructions(sc);
						addInstruction(new XInstructionDumyWriteLocal(var), varDecl);
						addInstruction(new XInstructionOPop(), varDecl);
					}
				}
			}
		}
		
		XStatementCompiler sc = visitTree(xTry.block, null);
		if(sc!=null){
			addInstructions(sc);
		}
		
		if(xTry.catchs!=null){
			
			addInstruction(tryHandle.endInstruction = new XInstructionDumyDelete(), xTry);
			
			XInstructionDumyDelete end = new XInstructionDumyDelete();
			XInstructionDumyJump toEnd = new XInstructionDumyJump();
			toEnd.target = end;
			addInstruction(toEnd, xTry);
			
			for(XCatch c:xTry.catchs){
				boolean b = vars==null;
				if(b)
					vars = new HashMap<String, XVariable>();
				XVariable var = new XVariable();
				var.modifier = c.modifier.modifier;
				var.name = c.varName;
				XVarType[] ptrs;
				if(c.types.size()==1){
					ptrs = new XVarType[1];
					var.type = ptrs[0] = getVarTypeForThis(c.types.get(0), true);
				}else{
					ptrs = new XVarType[c.types.size()];
					XVarType types[] = new XVarType[c.types.size()];
					for(int i=0; i<types.length; i++){
						types[i] = ptrs[i] = getVarTypeForThis(c.types.get(i), true);
					}
					var.type = new XMultibleType(types);
				}
				addVariable(var);
				addInstruction(var.start = new XInstructionDumyDelete(), c);
				sc = visitTree(c.block, null);
				XInstructionDumyDelete target = new XInstructionDumyDelete();
				for(XVarType ptr:ptrs){
					tryHandle.jumpTargets.put(ptr.getXClassPtr(), target);
				}
				addInstruction(target, c);
				addInstruction(new XInstructionDumyWriteLocal(var), c);
				if(sc!=null){
					addInstructions(sc);
				}
				XInstructionDumyJump jump = new XInstructionDumyJump();
				addInstruction(jump, c);
				jump.target = end;
				if(b){
					finalizeVars(c);
				}else{
					XInstructionDumyDelete endd = new XInstructionDumyDelete();
					var.end = endd;
					addInstruction(endd, c);
					codeGen.addVariable(var);
					vars.remove(var.name);
				}
			}
			
			addInstruction(end, xTry);
			
		}
		
		if(xTry.finallyBlock!=null || xTry.resource!=null){
			startFinally(xTry);
			sc = visitTree(xTry.finallyBlock, null);
			if(sc!=null){
				addInstructions(sc);
			}
			if(xTry.resource!=null){
				XMethod m = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass("xscript.lang.AutoCloseable").getMethod("close");
				for(XVariable var:vars.values()){
					addInstruction(new XInstructionDumyReadLocal(var), xTry);
					addInstruction(new XInstructionODup(), xTry);
					addInstruction(new XInstructionLoadConstNull(), xTry);
					addInstruction(new XInstructionEqObject(), xTry);
					XInstructionDumyNIf iif = new XInstructionDumyNIf();
					addInstruction(iif, xTry);
					XTryHandle closeTry = new XTryHandle();
					codeGen.addTryHandler(closeTry);
					XInstructionDumyTryStart ts = new XInstructionDumyTryStart();
					ts.handle = closeTry;
					addInstruction(closeTry.startInstruction = ts, xTry);
					addInstruction(new XInstructionInvokeDynamic(m, new XClassPtr[0]), xTry);
					addInstruction(closeTry.endInstruction = new XInstructionDumyDelete(), xTry);
					XInstructionDumyJump endJump = new XInstructionDumyJump();
					addInstruction(endJump, xTry);
					addInstruction(iif.target = new XInstructionDumyDelete(), xTry);
					closeTry.jumpTargets.put(new XClassPtrClass("xscript.lang.Exception"), iif.target);
					addInstruction(new XInstructionOPop(), xTry);
					addInstruction(endJump.target = new XInstructionDumyDelete(), xTry);
				}
				finalizeVars(xTry);
			}
			endFinally(xTry);
		}
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
		XVarType type = getVarTypeForThis(xNewArray.type, true);
		if(init==0){
			XStatementCompiler sc = visitTree(xNewArray.arrayInitialize, type);
			addInstructions(sc);
			setReturn(sc.returnType, xNewArray);
		}else{
			for(int i=0; i<init; i++){
				XStatement s = xNewArray.arraySizes.get(i);
				XStatementCompiler sc = visitTree(s, getPrimitiveType(XPrimitive.INT));
				addInstructions(sc);
			}
			addInstruction(new XInstructionNewArray(type.getXClassPtr(), init), xNewArray);
			setReturn(type, xNewArray);
		}
	}
	
	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		XClassPtr cptr = returnExpected.getXClassPtr();
		XClass c = returnExpected.getXClass();
		if(c!=null && c.isArray()){
			if(xArrayInitialize.statements==null){
				addInstruction(new XInstructionLoadConstInt(0), xArrayInitialize);
				addInstruction(new XInstructionNewArray(cptr, 1), xArrayInitialize);
				setReturn(returnExpected, xArrayInitialize);
			}else{
				int size = xArrayInitialize.statements.size();
				addInstruction(new XInstructionLoadConstInt(size), xArrayInitialize);
				addInstruction(new XInstructionNewArray(cptr, 1), xArrayInitialize);
				int prim = c.getArrayPrimitive();
				XVarType inner;
				if(prim==XPrimitive.OBJECT){
					inner = returnExpected.getGeneric(0);
				}else{
					inner = getPrimitiveType(prim);
				}
				for(int i=0; i<size; i++){
					XStatement s = xArrayInitialize.statements.get(i);
					if(s!=null){
						XStatementCompiler sc = visitTree(s, inner);
						addInstruction(new XInstructionODup(), xArrayInitialize);
						XCodeGen[] codeGens = {new XCodeGen(), sc.getCodeGen()};
						codeGens[0].addInstruction(new XInstructionLoadConstInt(i), s.line.startLine);
						XMethodSearch search = searchMethod(returnExpected, "operator[]");
						search.applyTypes(getPrimitiveType(XPrimitive.INT), sc.returnType);
						makeCall(search, xArrayInitialize, codeGens);
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
		XStatementCompiler sc = visitTree(xForeach.in, XAnyType.type);
		addInstructions(sc);
		vars = new HashMap<String, XVariable>();
		if(xForeach.var instanceof XVarDecls){
			XVarDecls varDecls = (XVarDecls)xForeach.var;
			if(varDecls.varDecls.size()==1){
				XVarDecl varDecl = varDecls.varDecls.get(0);
				XVariable variable = new XVariable();
				variable.name = varDecl.name;
				variable.type = getVarTypeForThis(varDecl.type, true);
				int primitive = variable.type.getPrimitiveID();
				addInstruction(variable.start = new XInstructionDumyDelete(), varDecl);
				addVariable(variable);
				breaks = new ArrayList<XInstructionDumyJump>();
				continues = new ArrayList<XInstructionDumyJump>();
				XInstruction continueTarget;
				XInstructionDumyJump endJump;
				XInstruction breakTarget;
				XVarType cp = sc.returnType.getSuperClass("xscript.lang.Iterable");
				System.out.println(cp);
				if(cp != null){
					XMethodSearch search = searchMethod(sc.returnType, "iterator");
					search.applyTypes(new XVarType[0]);
					XVarType ret = makeCall(search, xForeach, new XCodeGen[0]);
					addInstruction(continueTarget = new XInstructionODup(), xForeach);
					search = searchMethod(ret, "hasNext");
					search.applyTypes(new XVarType[0]);
					search.applyReturn(getPrimitiveType(XPrimitive.BOOL));
					makeCall(search, xForeach, new XCodeGen[0]);
					addInstruction(endJump = new XInstructionDumyNIf(), xForeach);
					
					addInstruction(new XInstructionODup(), xForeach);
					search = searchMethod(ret, "next");
					search.applyTypes(new XVarType[0]);
					search.applyReturn(variable.type);
					makeCall(search, xForeach, new XCodeGen[0]);
					addInstruction(new XInstructionDumyWriteLocal(variable), xForeach);
					if(primitive==XPrimitive.OBJECT){
						addInstruction(new XInstructionOPop(), xForeach);
					}else{
						addInstruction(new XInstructionPop(), xForeach);
					}
					sc = visitTree(xForeach.block, null);
					addInstructions(sc);
					
					XInstructionDumyJump jump = new XInstructionDumyJump();
					jump.target = continueTarget;
					addInstruction(jump, xForeach);
					
					addInstruction(breakTarget = new XInstructionOPop(), xForeach);
					
				}else{
					XClass c = sc.returnType.getXClass();
					if(c!=null && c.isArray()){
						
						addInstruction(new XInstructionLoadConstInt(0), xForeach);
						XInstruction startJump;
						addInstruction(startJump = new XInstructionDup(), xForeach);
						addInstruction(new XInstructionODup(), xForeach);
						addInstruction(instructionGetField(c.getLengthField()), xForeach);
						addInstruction(new XInstructionSmaInt(), xForeach);
						addInstruction(endJump = new XInstructionDumyNIf(), xForeach);
						addInstruction(new XInstructionODup(), xForeach);
						addInstruction(new XInstructionDup(), xForeach);
						XMethodSearch search = searchMethod(sc.returnType, "operator[]");
						search.applyTypes(getPrimitiveType(XPrimitive.INT));
						search.applyReturn(variable.type);
						makeCall(search, xForeach, new XCodeGen[]{new XCodeGen()});
						addInstruction(new XInstructionDumyWriteLocal(variable), xForeach);
						if(primitive==XPrimitive.OBJECT){
							addInstruction(new XInstructionOPop(), xForeach);
						}else{
							addInstruction(new XInstructionPop(), xForeach);
						}
						sc = visitTree(xForeach.block, null);
						addInstructions(sc);
						addInstruction(continueTarget = new XInstructionLoadConstInt(1), xForeach);
						addInstruction(new XInstructionAddInt(), xForeach);
						XInstructionDumyJump jump = new XInstructionDumyJump();
						jump.target = startJump;
						addInstruction(jump, xForeach);
						addInstruction(breakTarget = new XInstructionOPop(), xForeach);
						addInstruction(new XInstructionPop(), xForeach);
					}else{
						compilerError(XMessageLevel.ERROR, "wrong.foreach.type", xForeach.line);
						return;
					}
				}
				endJump.target = breakTarget;
				for(XInstructionDumyJump ccontinue:continues){
					ccontinue.target = continueTarget;
				}
				for(XInstructionDumyJump bbreak:breaks){
					bbreak.target = breakTarget;
				}
			}else{
				compilerError(XMessageLevel.ERROR, "worng.vardecl", xForeach.line);
			}
		}else{
			compilerError(XMessageLevel.ERROR, "worng.vardecl", xForeach.line);
		}
		finalizeVars(xForeach);
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
		XStatementCompiler sc = visitTree(xSwitch.statement, XAnyType.type);
		addInstructions(sc);
		XClass enumClass = null;
		boolean stringSwitch = false;
		if(sc.returnType.getPrimitiveID()==XPrimitive.OBJECT){
			if(sc.returnType.getXClass().isEnum()){
				enumClass = sc.returnType.getXClass();
				XClass c = methodCompiler.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass("xscript.lang.Enum");
				XMethod m = c.getMethod("ordinal()int");
				addInstruction(new XInstructionInvokeDynamic(m, new XClassPtr[0]), xSwitch);
			}else if(sc.returnType.getXClass().getName().equals("xscript.lang.String")){
				stringSwitch = true;
			}else{
				compilerError(XMessageLevel.ERROR, "wrong.type.for.switch", xSwitch.line, sc.returnType);
			}
		}
		breaks = new ArrayList<XInstructionDumyJump>();
		if(stringSwitch){
			XInstructionDumyStringSwitch s = new XInstructionDumyStringSwitch();
			addInstruction(s, xSwitch);
			for(XCase c:xSwitch.cases){
				if(c.key==null){
					XInstructionDumyDelete dd = new XInstructionDumyDelete();
					if(s.table.containsKey(null)){
						compilerError(XMessageLevel.ERROR, "duplicated.default", c.line);
					}else{
						s.table.put(null, dd);
					}
					addInstruction(dd, c);
					addInstructions(visitTree(c.block));
				}else if(c.key instanceof XConstant){
					XConstantValue val = ((XConstant)c.key).value;
					if(val.getType()!=String.class){
						compilerError(XMessageLevel.ERROR, "case.only.string", c.line);
					}else{
						XInstructionDumyDelete dd = new XInstructionDumyDelete();
						if(s.table.containsKey(val.getString())){
							compilerError(XMessageLevel.ERROR, "duplicated.case", c.line, val.getString());
						}else{
							s.table.put(val.getString(), dd);
						}
						addInstruction(dd, c);
						addInstructions(visitTree(c.block));
					}
				}else{
					compilerError(XMessageLevel.ERROR, "case.not.constant", c.line);
				}
			}
			XInstructionDumyDelete breakTarget = new XInstructionDumyDelete();
			if(!s.table.containsKey(null)){
				s.table.put(null, breakTarget);
			}
			addInstruction(breakTarget, xSwitch);
			for(XInstructionDumyJump bbreak:breaks){
				bbreak.target = breakTarget;
			}
		}else{
			XInstructionDumySwitch s = new XInstructionDumySwitch();
			addInstruction(s, xSwitch);
			for(XCase c:xSwitch.cases){
				if(c.key==null){
					XInstructionDumyDelete dd = new XInstructionDumyDelete();
					if(s.table.containsKey(null)){
						compilerError(XMessageLevel.ERROR, "duplicated.default", c.line);
					}else{
						s.table.put(null, dd);
					}
					addInstruction(dd, c);
					addInstructions(visitTree(c.block));
				}else if(enumClass==null && c.key instanceof XConstant){
					XConstantValue val = ((XConstant)c.key).value;
					if(val.getType()!=Integer.class){
						compilerError(XMessageLevel.ERROR, "case.only.int", c.line);
					}else{
						XInstructionDumyDelete dd = new XInstructionDumyDelete();
						if(s.table.containsKey(val.getInt())){
							compilerError(XMessageLevel.ERROR, "duplicated.case", c.line, val.getInt());
						}else{
							s.table.put(val.getInt(), dd);
						}
						addInstruction(dd, c);
						addInstructions(visitTree(c.block));
					}
				}else if(enumClass!=null && c.key instanceof XType){
					XVarAccess va = visitVarAccess(c.key);
					XClass enumC = va.declaringClass.getXClass();
					if(enumClass!=enumC){
						compilerError(XMessageLevel.ERROR, "types.not.equal", c.line, enumClass, enumC);
					}else if(enumC instanceof XClassCompiler){
						int id = ((XClassCompiler)enumC).getField(va.name).getEnumID();
						XInstructionDumyDelete dd = new XInstructionDumyDelete();
						if(s.table.containsKey(id)){
							compilerError(XMessageLevel.ERROR, "duplicated.case", c.line, id);
						}else{
							s.table.put(id, dd);
						}
						addInstruction(dd, c);
						addInstructions(visitTree(c.block));
					}
				}else{
					compilerError(XMessageLevel.ERROR, "case.not.constant", c.line);
				}
			}
			XInstructionDumyDelete breakTarget = new XInstructionDumyDelete();
			if(!s.table.containsKey(null)){
				s.table.put(null, breakTarget);
			}
			addInstruction(breakTarget, xSwitch);
			for(XInstructionDumyJump bbreak:breaks){
				bbreak.target = breakTarget;
			}
		}
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
			varAccess.name = "this";
			varAccess.tree = xThis;
			setReturn(methodCompiler.getDeclaringClassVarType(), xThis);
		}
	}

	@Override
	public void visitInstanceof(XInstanceof xInstanceof) {
		XStatementCompiler sc = visitTree(xInstanceof.statement, XAnyType.type);
		addInstructions(sc);
		addInstruction(new XInstructionInstanceof(methodCompiler.getGenericClass(xInstanceof.type, true)), xInstanceof);
	}
	
	@Override
	public void visitSuper(XSuper xSuper) {
		shouldNeverCalled();
	}

	@Override
	public void visitAssert(XAssert xAssert) {
		XField field = methodCompiler.getDeclaringClassCompiler().getSyntheticField("assertionsDisabled");
		addInstruction(instructionGetStaticField(field), xAssert);
		XInstructionDumyIf overjump = new XInstructionDumyIf();
		addInstruction(overjump, xAssert);
		XStatementCompiler sc = visitTree(xAssert.statement, getPrimitiveType(XPrimitive.BOOL));
		addInstructions(sc);
		XInstructionDumyNIf end = new XInstructionDumyNIf();
		addInstruction(end, xAssert);
		XClassPtr cp = new XClassPtrClass("xscript.lang.AssertionError");
		addInstruction(new XInstructionNew(cp), xAssert);
		addInstruction(new XInstructionODup(), xAssert);
		XClass c = cp.getXClassNonNull(methodCompiler.getDeclaringClass().getVirtualMachine());
		XMethod m = c.getMethod("<init>()void");
		addInstruction(new XInstructionInvokeSpecial(m, new XClassPtr[0]), xAssert);
		addInstruction(new XInstructionThrow(), xAssert);
		addInstruction(overjump.target = end.target = new XInstructionDumyDelete(), xAssert);
	}
	
	@Override
	public void visitCompiled(XCompiledPart xCompiledPart) {
		codeGen = xCompiledPart.codeGen;
		setReturn(null, xCompiledPart);
	}
	
	private void setReturn(XVarType returnType, XTree tree){
		if(returnType instanceof XErroredType){
			this.returnType = returnType;
			return;
		}
		if(returnType!=null && returnType.getPrimitiveID()==XPrimitive.VOID){
			returnType = null;
		}
		if(returnExpected==null){
			if(returnType!=null){
				if(returnType.getPrimitiveID()==XPrimitive.OBJECT){
					addInstruction(new XInstructionOPop(), tree);
				}else{
					addInstruction(new XInstructionPop(), tree);
				}
			}
		}else{
			if(returnType==null){
				compilerError(XMessageLevel.ERROR, "no.void.return", tree.line);
				returnType = new XErroredType();
			}else{
				makeAutoCast(returnType, returnExpected, tree);
			}
		}
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
