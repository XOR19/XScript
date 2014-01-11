package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

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
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.instruction.XInstruction;
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
import xscript.runtime.instruction.XInstructionInvokeConstructor;
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
import xscript.runtime.instruction.XInstructionThrow;
import xscript.runtime.instruction.XInstructionVarJump;
import xscript.runtime.instruction.XInstructionWriteLocal;
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
					addInstruction(new XInstructionReadLocal(rv.variable.id), varAccess.tree);
					setReturn(rv.variable.type, varAccess.tree);
				}
			}else{
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(new XInstructionGetStaticField(rv.field), varAccess.tree);
				}else{
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					addInstructions(varAccess.codeGen);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(new XInstructionGetLocalField(0, rv.field), varAccess.tree);
						}else{
							addInstruction(new XInstructionGetField(rv.field), varAccess.tree);
						}
					}else{
						addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), varAccess.tree);
					}
				}
				setReturn(getVarTypeFor(rv.field.getType()), varAccess.tree);
			}
			if(returnType==null){
				setReturn(null, varAccess.tree);
			}
		}
		return codeGen;
	}
	
	private XVarType getVarTypeFor(XClassPtr classPtr){
		return XVarType.getVarTypeFor(classPtr, methodCompiler.getDeclaringClass().getVirtualMachine());
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
			addBreak(jump, xBreak, any);
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
			addContinue(jump, xContinue, any);
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
			varAccess.declaringClass = getVarTypeFor(methodCompiler.getGenericClass(xType, true));
		}else{
			String rname = xType.name.name;
			int s=-1;
			String next = null;
			while(true){
				XClassPtr c = methodCompiler.getGenericClass(xType, false);
				if(c!=null){
					varAccess.declaringClass =getVarTypeFor(c);
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
							varAccess.codeGen.addInstruction(new XInstructionGetStaticField(rv.field), xType.line.startLine);
						}else{
							if(rv.isStatic)
								compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
							if(varAccess.codeGen==null)
								varAccess.codeGen = new XCodeGen();
							if(varAccess.variable==null){
								if(varAccess.declaringClass==null){
									varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, rv.field), xType.line.startLine);
								}else{
									varAccess.codeGen.addInstruction(new XInstructionGetField(rv.field), xType.line.startLine);
								}
							}else{
								varAccess.codeGen.addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), xType.line.startLine);
							}
						}
						varAccess.variable = null;
						varAccess.declaringClass = getVarTypeFor(rv.field.getType());
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
		var.type = getVarTypeFor(methodCompiler.getGenericClass(xVarDecl.type, true));
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
			addInstruction(new XInstructionWriteLocal(var.id), xVarDecl);
			if(var.type.getPrimitiveID()==XPrimitive.OBJECT){
				addInstruction(new XInstructionOPop(), xVarDecl);
			}else{
				addInstruction(new XInstructionPop(), xVarDecl);
			}
		}
	}

	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		if(parent==null){
			vars = new HashMap<String, XVariable>();
			if(!xscript.runtime.XModifier.isStatic(methodCompiler.getModifier())){
				XVariable variable = new XVariable();
				variable.modifier = xscript.runtime.XModifier.FINAL;
				variable.type = getVarTypeFor(methodCompiler.getDeclaringClassGen());
				variable.name = "this";
				addInstruction(variable.start = new XInstructionDumyDelete(), xMethodDecl);
				addVariable(variable);
			}
			addInstructions(visitTree(xMethodDecl.paramTypes));
			if(methodCompiler.isConstructor()){
				List<XClassPtr> superClasses = new ArrayList<XClassPtr>();
				for(XClassPtr superClass:methodCompiler.getDeclaringClass().getSuperClasses()){
					superClasses.add(superClass);
				}
				boolean selvInvokeOk = true;
				boolean selvInvoke = false;
				if(xMethodDecl.superConstructors!=null){
					for(XStatement s:xMethodDecl.superConstructors){
						if(s instanceof XMethodCall){
							XMethodCall mc = (XMethodCall) s;
							XVarAccess varAccess = visitClassAccess(mc.method);
							if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
								XClassPtr sp = varAccess.declaringClass.getXClassPtr();
								ListIterator<XClassPtr> i = superClasses.listIterator();
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
									XMethodSearch possibleMethods = new XMethodSearch(getXClassFor(sp), false, "<init>", true, false);
									if(mc.typeParam!=null){
										XClassPtr[] generics = new XClassPtr[mc.typeParam.size()];
										for(int j=0; j<generics.length; j++){
											generics[j] = methodCompiler.getGenericClass(mc.typeParam.get(j), true);
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
									possibleMethods.applyTypes(types);
									makeCall2(possibleMethods, mc, codeGens, true);
									selvInvokeOk = false;
								}else{
									if(sp.equals(methodCompiler.getDeclaringClassGen())){
										if(selvInvokeOk){
											if(selvInvoke){
												compilerError(XMessageLevel.ERROR, "allready.selvinvoked", mc.line);
											}else{
												XMethodSearch possibleMethods = new XMethodSearch(methodCompiler.getDeclaringClass(), false, "<init>", true, false);
												if(mc.typeParam!=null){
													XClassPtr[] generics = new XClassPtr[mc.typeParam.size()];
													for(int j=0; j<generics.length; j++){
														generics[j] = methodCompiler.getGenericClass(mc.typeParam.get(j), true);
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
												possibleMethods.applyTypes(types);
												makeCall2(possibleMethods, mc, codeGens, true);
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
					for(XClassPtr superClass:superClasses){
						XMethodSearch search = new XMethodSearch(getXClassFor(superClass), false, "<init>", true, false);
						search.applyTypes(new XVarType[0]);
						search.applyReturn(getVarTypeFor(new XClassPtrClass("void")));
						XMethod m = search.getMethod();
						if(m==null){
							if(search.isEmpty()){
								compilerError(XMessageLevel.ERROR, "nomethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
							}else{
								compilerError(XMessageLevel.ERROR, "toomanymethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
							}
						}else{
							codeGen.addInstruction(new XInstructionInvokeConstructor(m, new XClassPtr[0]), xMethodDecl.line.startLine);
						}
					}
				}
			}
			addInstructions(visitTree(xMethodDecl.block, null));
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
		return new XPrimitiveType(primitiveID, c);
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
			XStatementCompiler c = visitTree(xReturn.statement, getVarTypeFor(methodCompiler.getGenericReturnType()));
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
		XStatementCompiler c = visitTree(xThrow.statement, getVarTypeFor(new XClassPtrClass("xscript.lang.Throwable")));
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
		XStatementCompiler sc = visitTree(xSynchroized.ident, getVarTypeFor(new XClassPtrClass("xscript.lang.Object")));
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
			setReturn(getVarTypeFor(new XClassPtrClass("xscript.lang.String")), xConstant);
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
		if(varAccess.variable!=null){
			addInstruction(new XInstructionReadLocal(varAccess.variable.id), xMethodCall);
		}else{
			if(varAccess.declaringClass==null){
				addInstruction(new XInstructionReadLocal(0), xMethodCall);
			}
		}
		XClass c[];
		if(varAccess.declaringClass==null){
			c = new XClass[]{methodCompiler.getDeclaringClass()};
		}else{
			c = varAccess.declaringClass.getXClasses();
		}
		XMethodSearch possibleMethods = new XMethodSearch(c, varAccess.isStatic, varAccess.name, varAccess.specialInvoke, true);
		if(xMethodCall.typeParam!=null){
			XClassPtr[] generics = new XClassPtr[xMethodCall.typeParam.size()];
			for(int i=0; i<generics.length; i++){
				generics[i] = methodCompiler.getGenericClass(xMethodCall.typeParam.get(i), true);
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
		setReturn(makeCall(possibleMethods, xMethodCall, codeGens), xMethodCall);
	}
	
	private XVarType makeCall(XMethodSearch possibleMethods, XTree tree, XCodeGen[] codeGens) {
		return makeCall2(possibleMethods, tree, codeGens, false);
	}

	private XVarType makeCall2(XMethodSearch possibleMethods, XTree tree, XCodeGen[] codeGens, boolean constructorCall) {
		XMethod m = possibleMethods.getMethod();
		if(m==null){
			if(possibleMethods.isEmpty()){
				compilerError(XMessageLevel.ERROR, "nomethodfor", tree.line, possibleMethods.getDesk());
			}else{
				compilerError(XMessageLevel.ERROR, "toomanymethodfor", tree.line, possibleMethods.getDesk());
			}
			return new XErroredType();
		}
		for(int i=0; i<codeGens.length; i++){
			addInstructions(codeGens[i]);
			makeAutoCast(possibleMethods.getTypes()[i], getVarTypeFor(m.getParams()[i]), tree);
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
				if(constructorCall){
					addInstruction(new XInstructionInvokeConstructor(m, possibleMethods.getGenerics()), tree);
				}else{
					addInstruction(new XInstructionInvokeSpecial(m, possibleMethods.getGenerics()), tree);
				}
			}else{
				addInstruction(new XInstructionInvokeDynamic(m, possibleMethods.getGenerics()), tree);
			}
		}
		return getVarTypeFor(m.getReturnTypePtr());
	}
	
	private void makeAutoCast(XVarType from, XVarType to, XTree tree){
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
		
		XClassPtr classPtr = methodCompiler.getGenericClass(xNew.type, true);
		XMethodSearch search = new XMethodSearch(getXClassFor(classPtr), false, "<init>", true, false);
		addInstruction(new XInstructionNew(classPtr), xNew);
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
		setReturn(getVarTypeFor(classPtr), xNew);
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
				XClass cc;
				XClass c=cc=methodCompiler.getDeclaringClass();
				rv.field = c.getFieldAndParents(varAccess.name);
				while(rv.field==null && c!=null){
					if(xscript.runtime.XModifier.isStatic(c.getModifier()))
						rv.isStatic = true;
					c = c.getOuterClass();
					if(c==null)
						break;
					outer++;
					rv.field = c.getFieldAndParents(varAccess.name);
				}
				if(rv.field==null){
					compilerError(XMessageLevel.ERROR, "var.notfound", varAccess.tree.line, varAccess.name);
				}else{
					if(!rv.isStatic && outer>0){
						if(varAccess.codeGen==null){
							varAccess.codeGen = new XCodeGen();
						}
						outer--;
						XField field = cc.getFieldAndParents("outer$");
						varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, field), tree.line.startLine);
						while(outer>0){
							cc = cc.getOuterClass();
							field = cc.getFieldAndParents("outer$");
							varAccess.codeGen.addInstruction(new XInstructionGetField(field), tree.line.startLine);
							outer--;
						}
						varAccess.declaringClass = getVarTypeFor(field.getType());
					}
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
	
	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		XOperator op = xOperatorStatement.operator;
		if(op == XOperator.ELEMENT){
			String name = ((XIdent)xOperatorStatement.right).name;
			if(name.equals("class")){
				XVarAccess varAccess = visitClassAccess(xOperatorStatement.left);
				if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
					addInstruction(new XInstructionLoadConstClass(varAccess.declaringClass.getXClassPtr()), xOperatorStatement);
					setReturn(getVarTypeFor(new XClassPtrClass("xscript.lang.Class")), xOperatorStatement);
				}else{
					compilerError(XMessageLevel.ERROR, "not.a.class", xOperatorStatement.line);
				}
			}else if(name.equals("this")){
				XVarAccess varAccess = visitClassAccess(xOperatorStatement.left);
				if(varAccess.isStatic && varAccess.declaringClass!=null && varAccess.variable==null && varAccess.name==null){
					varAccess.codeGen = new XCodeGen();
					varAccess.isStatic = false;
					XClass c = getXClassFor(varAccess.declaringClass.getXClassPtr());
					XClass cc = methodCompiler.getDeclaringClass();
					if(cc==c){
						this.varAccess = new XVarAccess();
						this.varAccess.name = "this";
						this.varAccess.tree = xOperatorStatement;
						setReturn(getVarTypeFor(varAccess.declaringClass.getXClassPtr()), xOperatorStatement);
					}else{
						if(!xscript.runtime.XModifier.isStatic(cc.getModifier())){
							XField field = cc.getFieldAndParents("outer$");
							varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, field), xOperatorStatement.line.startLine);
							cc = cc.getOuterClass();
							while(cc!=c){
								if(cc==null)
									break;
								if(xscript.runtime.XModifier.isStatic(cc.getModifier()))
									break;
								field = cc.getFieldAndParents("outer$");
								varAccess.codeGen.addInstruction(new XInstructionGetField(field), xOperatorStatement.line.startLine);
								cc = cc.getOuterClass();
							}
						}
						this.varAccess = varAccess;
						if(cc!=c){
							compilerError(XMessageLevel.ERROR, "not.a.outer.class", xOperatorStatement.left.line);
							setReturn(new XErroredType(), xOperatorStatement);
						}else{
							setReturn(getVarTypeFor(new XClassPtrClass(cc.getName())), xOperatorStatement);
						}
					}
				}else{
					compilerError(XMessageLevel.ERROR, "not.a.class", xOperatorStatement.line);
				}
			}else{
				if(xOperatorStatement.left instanceof XSuper){
					varAccess = new XVarAccess();
					varAccess.declaringClass = getXSuperClassFor(methodCompiler.getDeclaringClassGen());
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
									varAccess.codeGen.addInstruction(new XInstructionGetStaticField(rv.field), xOperatorStatement.line.startLine);
								}else{
									if(rv.isStatic)
										compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
									if(varAccess.codeGen==null){
										varAccess.codeGen = new XCodeGen();
									}
									if(varAccess.variable==null){
										if(varAccess.declaringClass==null){
											varAccess.codeGen.addInstruction(new XInstructionGetLocalField(0, rv.field), xOperatorStatement.line.startLine);
										}else{
											varAccess.codeGen.addInstruction(new XInstructionGetField(rv.field), xOperatorStatement.line.startLine);
										}
									}else{
										varAccess.codeGen.addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), xOperatorStatement.line.startLine);
									}
								}
								varAccess.variable = null;
								varAccess.declaringClass = getVarTypeFor(rv.field.getType());
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
				XMethodSearch methodSearch = new XMethodSearch(s.returnType.getXClasses(), false, "operator[]", false, true);
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
						addInstruction(new XInstructionWriteLocal(rv.variable.id), xOperatorStatement);
						setReturn(rv.variable.type, xOperatorStatement);
					}
				}else{
					XStatementCompiler cr = visitTree(xOperatorStatement.right, getVarTypeFor(rv.field.getType()));
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
						addInstruction(new XInstructionSetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						addInstructions(cr);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(new XInstructionSetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionSetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(new XInstructionSetLocalField(varAccess.variable.id, rv.field), xOperatorStatement);
						}
					}
					setReturn(getVarTypeFor(rv.field.getType()), xOperatorStatement);
				}
			}
		}else if(op == XOperator.LETADD || op == XOperator.LETSUB || op == XOperator.LETMUL || op == XOperator.LETDIV || op == XOperator.LETMOD ||
				op == XOperator.LETOR || op == XOperator.LETAND || op == XOperator.LETXOR || op == XOperator.LETSHR || op == XOperator.LETSHL){
			XVarAccess varAccess = visitVarAccess(xOperatorStatement.left);
			XResolvedVariable rv = getVariable(varAccess, xOperatorStatement);
			int primitiveID;
			if(rv.field==null){
				addInstructions(varAccess.codeGen);
				addInstruction(new XInstructionReadLocal(rv.variable.id), xOperatorStatement);
				if((primitiveID = rv.variable.type.getPrimitiveID())==XPrimitive.OBJECT){
					XStatementCompiler sc = visitTree(xOperatorStatement.right, XAnyType.type);
					XCodeGen[] codeGens = {sc.getCodeGen()};
					XMethodSearch search = new XMethodSearch(rv.variable.type.getXClasses(), false, "operator"+op, false, true);
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
					addInstruction(new XInstructionWriteLocal(rv.variable.id), xOperatorStatement);
					setReturn(rv.variable.type, xOperatorStatement);
				}
			}else{
				if((primitiveID = rv.field.getTypePrimitive())==XPrimitive.OBJECT){
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(new XInstructionGetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(new XInstructionGetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionGetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), xOperatorStatement);
						}
					}
					XStatementCompiler sc = visitTree(xOperatorStatement.right, XAnyType.type);
					XCodeGen[] codeGens = {sc.getCodeGen()};
					XMethodSearch search = new XMethodSearch(rv.variable.type.getXClasses(), false, "operator"+op, false, true);
					search.applyTypes(sc.returnType);
					setReturn(makeCall(search, xOperatorStatement, codeGens), xOperatorStatement);
				}else{
					if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
						if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
							compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
						addInstruction(new XInstructionGetStaticField(rv.field), xOperatorStatement);
					}else{
						addInstructions(varAccess.codeGen);
						if(rv.isStatic)
							compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(new XInstructionGetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionODup(), xOperatorStatement);
								addInstruction(new XInstructionGetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), xOperatorStatement);
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
						addInstruction(new XInstructionSetStaticField(rv.field), xOperatorStatement);
					}else{
						if(varAccess.variable==null){
							if(varAccess.declaringClass==null){
								addInstruction(new XInstructionSetLocalField(0, rv.field), xOperatorStatement);
							}else{
								addInstruction(new XInstructionSetField(rv.field), xOperatorStatement);
							}
						}else{
							addInstruction(new XInstructionSetLocalField(varAccess.variable.id, rv.field), xOperatorStatement);
						}
					}
					setReturn(getVarTypeFor(rv.field.getType()), xOperatorStatement);
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
					XMethodSearch methodSearch = new XMethodSearch(cl.returnType.getXClasses(), false, "operator"+op.op, false, true);
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
	
	private XMultibleType getXSuperClassFor(XClassPtr classPtr){
		XClass c = getXClassFor(classPtr);
		XClassPtr[] superClasses = c.getSuperClasses();
		XSingleType[] singleType = new XSingleType[superClasses.length];
		for(int i=0; i<superClasses.length; i++){
			singleType[i] = (XSingleType)getVarTypeFor(superClasses[i]);
		}
		return new XMultibleType(null, singleType);
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
			addInstruction(new XInstructionReadLocal(rv.variable.id), tree);
			if((primitiveID = rv.variable.type.getPrimitiveID())==XPrimitive.OBJECT){
				XMethodSearch search = new XMethodSearch(rv.variable.type.getXClasses(), false, "operator"+op.op, false, true);
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
				addInstruction(new XInstructionWriteLocal(rv.variable.id), tree);
				returnType = rv.variable.type;
				if(suffix)
					addInstruction(new XInstructionPop(), tree);
			}
		}else{
			if((primitiveID = rv.field.getTypePrimitive())==XPrimitive.OBJECT){
				if(xscript.runtime.XModifier.isStatic(rv.field.getModifier())){
					if(!rv.isStatic && (varAccess.codeGen!=null || varAccess.variable!=null))
						compilerError(XMessageLevel.WARNING, "field.shouldnt.be.static", varAccess.tree.line, varAccess.name);
					addInstruction(new XInstructionGetStaticField(rv.field), tree);
				}else{
					addInstructions(varAccess.codeGen);
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(new XInstructionGetLocalField(0, rv.field), tree);
						}else{
							addInstruction(new XInstructionGetField(rv.field), tree);
						}
					}else{
						addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), tree);
					}
				}
				XMethodSearch search = new XMethodSearch(rv.variable.type.getXClasses(), false, "operator"+op.op, false, true);
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
					addInstruction(new XInstructionGetStaticField(rv.field), tree);
				}else{
					addInstructions(varAccess.codeGen);
					if(rv.isStatic)
						compilerError(XMessageLevel.ERROR, "field.is.static", varAccess.tree.line, varAccess.name);
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(new XInstructionGetLocalField(0, rv.field), tree);
						}else{
							addInstruction(new XInstructionODup(), tree);
							addInstruction(new XInstructionGetField(rv.field), tree);
						}
					}else{
						addInstruction(new XInstructionGetLocalField(varAccess.variable.id, rv.field), tree);
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
					addInstruction(new XInstructionSetStaticField(rv.field), tree);
				}else{
					if(varAccess.variable==null){
						if(varAccess.declaringClass==null){
							addInstruction(new XInstructionSetLocalField(0, rv.field), tree);
						}else{
							addInstruction(new XInstructionSetField(rv.field), tree);
						}
					}else{
						addInstruction(new XInstructionSetLocalField(varAccess.variable.id, rv.field), tree);
					}
				}
				if(suffix)
					addInstruction(new XInstructionPop(), tree);
				returnType = getVarTypeFor(rv.field.getType());
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
						XMethodSearch methodSearch = new XMethodSearch(returnType.getXClasses(), false, "operator"+op.op, false, true);
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
						XMethodSearch methodSearch = new XMethodSearch(returnType.getXClasses(), false, "operator"+op.op, false, true);
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
		XMethodSearch methodSearch = new XMethodSearch(s.returnType.getXClasses(), false, "operator[]", false, true);
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
		XClassPtr castTo = methodCompiler.getGenericClass(xCast.type, true);
		XVarType vt = getVarTypeFor(castTo);
		int prim1 = a.returnType.getPrimitiveID();
		int prim2 = vt.getPrimitiveID();
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
				compilerError(XMessageLevel.ERROR, "no.cast.able", xCast.line, a.returnType, vt);
			}
		}else if(prim1 == XPrimitive.OBJECT){
			if(a.returnType.canCastTo(vt) || vt.canCastTo(a.returnType)){
				addInstruction(new XInstructionCheckCast(castTo), xCast);
			}else{
				compilerError(XMessageLevel.ERROR, "no.cast.able", xCast.line, a.returnType, vt);
			}
		}
		setReturn(vt, xCast);
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
					var.type = getVarTypeFor(methodCompiler.getGenericClass(varDecl.type, true));
					var.name = varDecl.name;
					if(getVariable(var.name)!=null){
						compilerError(XMessageLevel.ERROR, "variable.duplicated", varDecl.line, var.name);
					}else{
						addVariable(var);
					}
					addInstruction(var.start = new XInstructionDumyDelete(), varDecl);
					addInstruction(new XInstructionLoadConstNull(), varDecl);
					addInstruction(new XInstructionWriteLocal(var.id), varDecl);
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
						addInstruction(new XInstructionWriteLocal(var.id), varDecl);
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
				XClassPtr[] ptrs;
				if(c.types.size()==1){
					ptrs = new XClassPtr[1];
					var.type = getVarTypeFor(ptrs[0] = methodCompiler.getGenericClass(c.types.get(0), true));
				}else{
					ptrs = new XClassPtr[c.types.size()];
					XSingleType types[] = new XSingleType[c.types.size()];
					for(int i=0; i<types.length; i++){
						types[i] = (XSingleType) getVarTypeFor(ptrs[i] = methodCompiler.getGenericClass(c.types.get(i), true));
					}
					var.type = new XMultibleType(null, types);
				}
				addVariable(var);
				addInstruction(var.start = new XInstructionDumyDelete(), c);
				sc = visitTree(c.block, null);
				XInstructionDumyDelete target = new XInstructionDumyDelete();
				for(XClassPtr ptr:ptrs){
					tryHandle.jumpTargets.put(ptr, target);
				}
				addInstruction(target, c);
				addInstruction(new XInstructionWriteLocal(var.id), c);
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
					addInstruction(new XInstructionReadLocal(var.id), xTry);
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
		XClassPtr type = methodCompiler.getGenericClass(xNewArray.type, true);
		if(init==0){
			XStatementCompiler sc = visitTree(xNewArray.arrayInitialize, getVarTypeFor(type));
			addInstructions(sc);
			setReturn(sc.returnType, xNewArray);
		}else{
			for(int i=0; i<init; i++){
				XStatement s = xNewArray.arraySizes.get(i);
				XStatementCompiler sc = visitTree(s, getPrimitiveType(XPrimitive.INT));
				addInstructions(sc);
			}
			addInstruction(new XInstructionNewArray(type, init), xNewArray);
			setReturn(getVarTypeFor(type), xNewArray);
		}
	}
	
	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		XClassPtr cptr = returnExpected.getXClassPtr();
		XClass[] c = returnExpected.getXClasses();
		if(c!=null && c.length==1 && c[0].isArray()){
			if(xArrayInitialize.statements==null){
				addInstruction(new XInstructionLoadConstInt(0), xArrayInitialize);
				addInstruction(new XInstructionNewArray(cptr, 1), xArrayInitialize);
				setReturn(returnExpected, xArrayInitialize);
			}else{
				int size = xArrayInitialize.statements.size();
				addInstruction(new XInstructionLoadConstInt(size), xArrayInitialize);
				addInstruction(new XInstructionNewArray(cptr, 1), xArrayInitialize);
				XClassPtr inner;
				if(cptr instanceof XClassPtrGeneric){
					inner = ((XClassPtrGeneric)cptr).getGeneric(0);
				}else{
					inner = new XClassPtrClass(XPrimitive.getName(c[0].getArrayPrimitive()));
				}
				for(int i=0; i<size; i++){
					XStatement s = xArrayInitialize.statements.get(i);
					if(s!=null){
						XStatementCompiler sc = visitTree(s, getVarTypeFor(inner));
						addInstruction(new XInstructionODup(), xArrayInitialize);
						XCodeGen[] codeGens = {new XCodeGen(), sc.getCodeGen()};
						codeGens[0].addInstruction(new XInstructionLoadConstInt(i), s.line.startLine);
						XMethodSearch search = new XMethodSearch(c, false, "operator[]", false, true);
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
		XStatementCompiler sc = visitTree(xSwitch.statement, XAnyType.type);
		addInstructions(sc);
		breaks = new ArrayList<XInstructionDumyJump>();
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
			}else if(c.key instanceof XConstant){
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
			setReturn(getVarTypeFor(methodCompiler.getDeclaringClassGen()), xThis);
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

	private void setReturn(XVarType returnType, XTree tree){
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
