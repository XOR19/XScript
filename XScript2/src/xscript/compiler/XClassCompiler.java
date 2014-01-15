package xscript.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
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
import xscript.compiler.tree.XTree.XTag;
import xscript.compiler.tree.XTree.XThis;
import xscript.compiler.tree.XTree.XThrow;
import xscript.compiler.tree.XTree.XTry;
import xscript.compiler.tree.XTree.XType;
import xscript.compiler.tree.XTree.XTypeParam;
import xscript.compiler.tree.XTree.XVarDecl;
import xscript.compiler.tree.XTree.XVarDecls;
import xscript.compiler.tree.XTree.XWhile;
import xscript.compiler.tree.XTreeSearch;
import xscript.compiler.tree.XVisitor;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.instruction.XInstructionGetStaticField;
import xscript.runtime.instruction.XInstructionInvokeDynamic;
import xscript.runtime.instruction.XInstructionLoadConstClass;
import xscript.runtime.instruction.XInstructionLoadConstInt;
import xscript.runtime.instruction.XInstructionNewArray;
import xscript.runtime.instruction.XInstructionODup;
import xscript.runtime.instruction.XInstructionOPop;
import xscript.runtime.instruction.XInstructionSetStaticField;
import xscript.runtime.method.XMethod;

public class XClassCompiler extends XClass implements XVisitor {

	public static final int STATE_TOGEN=6;
	
	private XMessageList messages;
	
	private boolean innerClasses;
	
	private boolean gotFirst;
	
	private List<XMethod> methodList;
	
	private List<XField> fieldList;
	
	private List<XStatement> staticInit;
	
	private List<XStatement> init;
	
	private XImportHelper importHelper;
	
	private boolean errored;
	
	private boolean visitConstructor;
	
	private boolean hasAssertions;
	
	private List<String> enumNames = new ArrayList<String>();
	
	private HashMap<String, Integer> syntheticNames = new HashMap<String, Integer>();
	
	protected XClassCompiler(XVirtualMachine virtualMachine, String name, XMessageList messages, XImportHelper importHelper, XPackage p) {
		super(virtualMachine, name, p);
		this.messages = messages;
		this.importHelper = importHelper;
		state = STATE_TOGEN;
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		messages.postMessage(level, "compiler."+key, lineDesk, args);
		errored = true;
	}
	
	protected XMessageList getMessageList(){
		return messages;
	}
	
	public void gen() {
		if(errored)
			state = STATE_ERRORED;
		if(state==STATE_TOGEN){
			for(XMethod method:methods){
				((XMethodCompiler)method).gen();
			}
			for(XPackage p:childs.values()){
				if(p instanceof XClassCompiler){
					((XClassCompiler) p).gen();
				}
			}
			if(errored){
				state = STATE_ERRORED;
			}else{
				state = STATE_RUNNABLE;
			}
		}
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
	
	private void registerClasses(List<XTree> defs){
		if(importHelper==null){
			importHelper = new XImportHelper((XCompiler) virtualMachine, this);
		}
		for(XTree tree:defs){
			if(tree instanceof XClassDecl){
				XClassDecl decl = (XClassDecl)tree;
				String name = getName()+"."+decl.name;
				XClassCompiler compiler = new XClassCompiler(virtualMachine, decl.name, new XMessageClass((XCompiler) virtualMachine, name), importHelper, this);
				if(childs.containsKey(decl.name)){
					compilerError(XMessageLevel.ERROR, "duplicatedclass", decl.line, decl.name);
				}else{
					addChild(compiler);
					compiler.registerClasses(decl.defs);
				}
			}
		}
	}
	
	private void registerClasses(XClassFile xClassFile){
		XPackage xPackage = getParent();
		boolean gotFirst = false;
		for(XTree tree:xClassFile.defs){
			if(tree instanceof XClassDecl){
				XClassDecl decl = (XClassDecl)tree;
				if(gotFirst){
					String name = xPackage.getName()+"."+decl.name;
					XClassCompiler compiler = new XClassCompiler(virtualMachine, decl.name, new XMessageClass((XCompiler) virtualMachine, name), importHelper, this);
					if(xPackage.getChild(decl.name)==null){
						xPackage.addChild(compiler);
						compiler.registerClasses(decl.defs);
					}else{
						compilerError(XMessageLevel.ERROR, "duplicatedclass", decl.line, decl.name);
					}
				}else{
					registerClasses(decl.defs);
					gotFirst = true;
				}
			}
		}
	}
	
	@Override
	public void visitTopLevel(XClassFile xClassFile) {
		if(state==STATE_TOGEN){
			if(xClassFile.packID==null){
				if(getParent().getName()!=null){
					compilerError(XMessageLevel.ERROR, "nopackagename", new XLineDesk(1, 1, 1, 1), getParent().getName());
				}
			}else{
				if(!xClassFile.packID.name.equals(getParent().getName())){
					compilerError(XMessageLevel.ERROR, "wrongpackagename", xClassFile.packID.line, getParent().getName());
				}
			}
			registerClasses(xClassFile);
			visitTree(xClassFile.defs);
		}
	}

	@Override
	public void visitImport(XImport xImport) {
		importHelper.addImport(this, xImport);
	}
	
	private boolean isNameForFieldOcupied(String name){
		for(int i=0; i<fieldList.size(); i++){
			if(fieldList.get(i).getSimpleName().equals(name)){
				return true;
			}
		}
		return false;
	}
	
	private XField addSyntheticField(int modifier, String name, XClassPtr type){
		String sname = "$"+name;
		if(isNameForFieldOcupied(sname)){
			int i=0;
			while(isNameForFieldOcupied(sname+"_"+i))i++;
			sname+="_"+i;
			syntheticNames.put(name, i);
		}
		XField field = new XField(this, modifier | xscript.runtime.XModifier.SYNTHETIC, sname, type, new xscript.runtime.XAnnotation[0]);
		fieldList.add(field);
		addChild(field);
		return field;
	}
	
	public XField getSyntheticField(String name){
		Integer id = syntheticNames.get(name);
		if(id==null){
			name = "$"+name;
		}else{
			name = "$"+name+"_"+id;
		}
		for(int i=0; i<fields.length; i++){
			if(fields[i].getSimpleName().equals(name)){
				if(xscript.runtime.XModifier.isSynthetic(fields[i].getModifier()))
					return fields[i];
				return null;
			}
		}
		return null;
	}
	
	public XField getSyntheticFieldAndParents(String name) {
		XField field = getSyntheticField(name);
		if(field!=null)
			return field;
		for(int i=0; i<superClasses.length; i++){
			XClass sc = superClasses[i].getXClassNonNull(virtualMachine);
			if(sc instanceof XClassCompiler){
				field = ((XClassCompiler)sc).getSyntheticFieldAndParents(name);
				if(field!=null)
					return field;
			}
		}
		return null;
	}
	
	@Override
	public void visitClassDecl(XClassDecl xClassDef) {
		if(innerClasses){
			xClassDef.accept((XVisitor)getChild(xClassDef.name));
		}else{
			innerClasses=true;
			if(gotFirst){
				xClassDef.accept((XVisitor)getParent().getChild(xClassDef.name));
			}else{
				gotFirst = true;
				if(!xClassDef.name.equals(getSimpleName())){
					compilerError(XMessageLevel.ERROR, "wrongclassname", xClassDef.line, getSimpleName());
				}
				if(xClassDef.typeParam==null){
					genericInfos = new XGenericInfo[0];
				}else{
					genericInfos = new XGenericInfo[xClassDef.typeParam.size()];
					for(int i=0; i<genericInfos.length; i++){
						XTypeParam typeParam = xClassDef.typeParam.get(i);
						genericInfos[i] = new XGenericInfo(typeParam.name, new XClassPtr[0], typeParam.isSuper);
					}
					for(int i=0; i<genericInfos.length; i++){
						XTypeParam typeParam = xClassDef.typeParam.get(i);
						XClassPtr[] ptr = getGenericClasses(typeParam.extend, null);
						genericInfos[i] = new XGenericInfo(typeParam.name, ptr, typeParam.isSuper);
					}
				}
				boolean extendsOtherOuter = false;
				if(xClassDef.superClasses==null){
					if(getName().equals("xscript.lang.Object")){
						superClasses = new XClassPtr[0];
					}else{
						superClasses = new XClassPtr[1];
						superClasses[0] = new XClassPtrClass("xscript.lang.Object");
						try{
							superClasses[0].getXClassNonNull(virtualMachine);
						}catch(Exception e){}
					}
				}else{
					superClasses = getGenericClasses(xClassDef.superClasses, null);
					for(XClassPtr superClass:superClasses){
						XClass c = superClass.getXClassNonNull(getVirtualMachine());
						if(c.getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(c.getModifier())){
							if(getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(modifier)){
								if(c.getOuterClass()==getOuterClass()){
									extendsOtherOuter = true;
								}else{
									compilerError(XMessageLevel.ERROR, "cant.extend.inner.class", xClassDef.line, c.getName());
								}
							}else{
								compilerError(XMessageLevel.ERROR, "cant.extend.inner.class", xClassDef.line, c.getName());
							}
						}
					}
				}
				modifier = xClassDef.modifier==null?0:xClassDef.modifier.modifier;
				if(isEnum()){
					modifier |= xscript.runtime.XModifier.STATIC;
				}
				methodList = new ArrayList<XMethod>();
				fieldList = new ArrayList<XField>();
				visitTree(xClassDef.defs);
				if(!visitConstructor){
					XMethodDecl decl = new XMethodDecl(XLineDesk.NULL, new XModifier(XLineDesk.NULL, xscript.runtime.XModifier.PUBLIC), 
							"<init>", null, new XType(XLineDesk.NULL, new XIdent(XLineDesk.NULL, "void"), null, 0), null, null, null, null, false);
					decl.accept(this);
				}
				XCodeGen assertionCodeGen = null;
				if(hasAssertions){
					if(staticInit == null){
						staticInit = new ArrayList<XTree.XStatement>();
					}
					assertionCodeGen = new XCodeGen();
					staticInit.add(0, new XCompiledPart(XLineDesk.NULL, assertionCodeGen));
				}
				if(staticInit != null){
					XMethodDecl staticMethodDecl = new XMethodDecl(XLineDesk.NULL, new XModifier(XLineDesk.NULL, xscript.runtime.XModifier.STATIC), 
							"<static>", null, new XType(XLineDesk.NULL, new XIdent(XLineDesk.NULL, "void"), null, 0), null, null,
							new XBlock(XLineDesk.NULL, staticInit), null, false);
					staticMethodDecl.accept(this);
				}
				if(isEnum()){
					XClassPtr array = new XClassPtrGeneric("xscript.lang.Array", new XClassPtr[]{new XClassPtrClass(getName())});
					XField field = addSyntheticField(xscript.runtime.XModifier.PRIVATE | xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.STATIC,
							"values", array);
					if(staticInit != null){
						XCodeGen codeGen = new XCodeGen();
						codeGen.addInstruction(new XInstructionNewArray(array, enumNames.size()), 0);
						XMethod method = getVirtualMachine().getClassProvider().getXClass("xscript.lang.Array").getMethod("operator[](int, T)T");
						for(int i=0; i<enumNames.size(); i++){
							codeGen.addInstruction(new XInstructionODup(), 0);
							codeGen.addInstruction(new XInstructionLoadConstInt(i), 0);
							codeGen.addInstruction(new XInstructionGetStaticField(fieldList.get(i)), 0);
							codeGen.addInstruction(new XInstructionInvokeDynamic(method, new XClassPtr[0]), 0);
							codeGen.addInstruction(new XInstructionOPop(), 0);
						}
						codeGen.addInstruction(new XInstructionSetStaticField(field), 0);
						codeGen.addInstruction(new XInstructionOPop(), 0);
						staticInit.add(new XCompiledPart(XLineDesk.NULL, codeGen));
					}
				}else if(getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(modifier) && !extendsOtherOuter){
					XClass outer = getOuterClass();
					String name = outer.getName();
					XClassPtr type;
					if(outer.getGenericParams()>0){
						XClassPtr generics[] = new XClassPtr[outer.getGenericParams()];
						for(int i=0; i<generics.length; i++){
							generics[i] = new XClassPtrClassGeneric(name, outer.getGenericInfo(i).getName());
						}
						type = new XClassPtrGeneric(name, generics);
					}else{
						type = new XClassPtrClass(name);
					}
					addSyntheticField(xscript.runtime.XModifier.PROTECTED | xscript.runtime.XModifier.FINAL, "outer", type);
				}
				if(hasAssertions){
					String name = getName();
					XClassPtr type;
					if(getGenericParams()>0){
						XClassPtr generics[] = new XClassPtr[getGenericParams()];
						for(int i=0; i<generics.length; i++){
							generics[i] = new XClassPtrClassGeneric(name, getGenericInfo(i).getName());
						}
						type = new XClassPtrGeneric(name, generics);
					}else{
						type = new XClassPtrClass(name);
					}
					addSyntheticField(xscript.runtime.XModifier.PROTECTED | xscript.runtime.XModifier.FINAL, "outer", type);
					XField field = addSyntheticField(xscript.runtime.XModifier.PRIVATE | xscript.runtime.XModifier.FINAL | xscript.runtime.XModifier.STATIC, "assertionsDisabled", new XClassPtrClass("bool"));
					assertionCodeGen.addInstruction(new XInstructionLoadConstClass(type), 0);
					XMethod m = getVirtualMachine().getClassProvider().getXClass("xscript.lang.Class").getMethod("desiredAssertionStatus()bool");
					assertionCodeGen.addInstruction(new XInstructionInvokeDynamic(m, new XClassPtr[0]), 0);
					assertionCodeGen.addInstruction(new XInstructionSetStaticField(field), 0);
				}
				annotations = new xscript.runtime.XAnnotation[0];
				methods = methodList.toArray(new XMethod[methodList.size()]);
				fields = fieldList.toArray(new XField[fieldList.size()]);
			}
			innerClasses=false;
		}
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
		shouldNeverCalled();
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
		int modifier;
		if(xVarDecl.modifier==null){
			modifier = 0;
		}else{
			modifier = xVarDecl.modifier.modifier;
		}
		XClassPtr type = getGenericClass(xVarDecl.type, null);
		xscript.runtime.XAnnotation[] annotations = new xscript.runtime.XAnnotation[0];
		try{
			XField field = new XField(this, modifier, xVarDecl.name, type, annotations);
			if(childs.containsKey(field.getSimpleName())){
				compilerError(XMessageLevel.ERROR, "duplicatedfield", xVarDecl.line, xVarDecl.name);
			}else{
				addChild(field);
			}
			fieldList.add(field);
			if(xVarDecl.init!=null){
				if(xVarDecl.modifier!=null && xscript.runtime.XModifier.isStatic(xVarDecl.modifier.modifier)){
					if(staticInit==null)
						staticInit = new ArrayList<XTree.XStatement>();
					staticInit.add(new XOperatorStatement(xVarDecl.line, new XIdent(xVarDecl.line, xVarDecl.name), XOperator.LET, xVarDecl.init));
				}else{
					if(init==null)
						init = new ArrayList<XTree.XStatement>();
					XOperatorStatement thisName = new XOperatorStatement(xVarDecl.line, new XIdent(xVarDecl.line, "this"), XOperator.ELEMENT, 
							new XIdent(xVarDecl.line, xVarDecl.name));
					init.add(new XOperatorStatement(xVarDecl.line, thisName, XOperator.LET, xVarDecl.init));
				}
			}
		}catch(XRuntimeException e){
			compilerError(XMessageLevel.ERROR, "intern", xVarDecl.line, e.getMessage());
		}
	}
	
	@Override
	public int getStaticFieldIndex(int sizeInObject) {
		if(state!=STATE_TOGEN)
			throw new XRuntimeException("You can't get a field index now");
		int ret = staticFieldCount;
		staticFieldCount += sizeInObject;
		return ret;
	}

	@Override
	public int getFieldIndex(int sizeInObject) {
		if(state!=STATE_TOGEN)
			throw new XRuntimeException("You can't get a field index now");
		int ret = fieldCount;
		fieldCount += sizeInObject;
		return ret;
	}
	
	@Override
	public void visitMethodDecl(XMethodDecl xMethodDecl) {
		int modifier;
		if(xMethodDecl.modifier==null){
			modifier = 0;
		}else{
			modifier = xMethodDecl.modifier.modifier;
		}
		if(xMethodDecl.varargs){
			modifier |= xscript.runtime.XModifier.VARARGS;
		}
		List<XClassPtr> classes = new ArrayList<XClassPtr>();
		XGenericInfo[] genericInfos;
		if(xMethodDecl.typeParam==null){
			genericInfos = new XGenericInfo[0];
		}else{
			genericInfos = new XGenericInfo[xMethodDecl.typeParam.size()];
			for(int i=0; i<genericInfos.length; i++){
				XTypeParam typeParam = xMethodDecl.typeParam.get(i);
				genericInfos[i] = new XGenericInfo(typeParam.name, new XClassPtr[0], typeParam.isSuper);
			}
			for(int i=0; i<genericInfos.length; i++){
				XTypeParam typeParam = xMethodDecl.typeParam.get(i);
				XClassPtr[] ptr = getGenericClasses(typeParam.extend, genericInfos);
				classes.addAll(Arrays.asList(ptr));
				genericInfos[i] = new XGenericInfo(typeParam.name, ptr, typeParam.isSuper);
			}
		}
		XClassPtr returnType = getGenericClass(xMethodDecl.returnType, genericInfos);
		classes.add(returnType);
		xscript.runtime.XAnnotation[] annotations = new xscript.runtime.XAnnotation[0];
		XClassPtr[] paramTypes;
		int size = xMethodDecl.paramTypes==null?0:xMethodDecl.paramTypes.size();
		int ss = 0;
		if(xMethodDecl.name.equals("<init>")){
			if(isEnum()){
				ss = 2;
				paramTypes = new XClassPtr[size+2];
				paramTypes[0] = new XClassPtrClass("xscript.lang.String");
				paramTypes[1] = new XClassPtrClass("int");
			}else if(getOuterClass()!=null && !xscript.runtime.XModifier.isStatic(modifier)){
				ss = 1;
				paramTypes = new XClassPtr[size+1];
				XClass outer = getOuterClass();
				String name = outer.getName();
				if(outer.getGenericParams()>0){
					XClassPtr generics[] = new XClassPtr[outer.getGenericParams()];
					for(int i=0; i<generics.length; i++){
						generics[i] = new XClassPtrClassGeneric(name, outer.getGenericInfo(i).getName());
					}
					paramTypes[0] = new XClassPtrGeneric(name, generics);
				}else{
					paramTypes[0] = new XClassPtrClass(name);
				}
			}else{
				paramTypes = new XClassPtr[size];
			}
		}else{
			paramTypes = new XClassPtr[size];
		}
		if(xMethodDecl.paramTypes!=null){
			for(int i=0; i<size; i++){
				paramTypes[i+ss] = getGenericClass(xMethodDecl.paramTypes.get(i).type, genericInfos);
				classes.add(paramTypes[i+ss]);
			}
		}
		if(xMethodDecl.name.equals("<init>")){
			visitConstructor = true;
		}
		xscript.runtime.XAnnotation[][] paramAnnotations = new xscript.runtime.XAnnotation[paramTypes.length][0];
		XClassPtr[] throwTypes = getGenericClasses(xMethodDecl.throwList, genericInfos);
		classes.addAll(Arrays.asList(throwTypes));
		try{
			XMethodCompiler method = new XMethodCompiler(this, modifier, xMethodDecl.name, returnType, 
					annotations, paramTypes, paramAnnotations, throwTypes, genericInfos, xMethodDecl, importHelper);
			addChild(method);
			methodList.add(method);
			for(XClassPtr cp:classes){
				resolveClassGeneric(cp, method);
			}
			if(!hasAssertions && xMethodDecl.block!=null){
				XTreeSearch s = new XTreeSearch(XTag.ASSERT);
				xMethodDecl.block.accept(s);
				hasAssertions = !s.getFounds().isEmpty();
			}
		}catch(XRuntimeException e){
			compilerError(XMessageLevel.ERROR, "intern", xMethodDecl.line, e.getMessage());
		}
	}

	private void resolveClassGeneric(XClassPtr cp, XMethod method){
		if(cp instanceof XClassPtrMethodGeneric){
			((XClassPtrMethodGeneric) cp).methodName = method.getRealName();
			((XClassPtrMethodGeneric) cp).params = method.getParams();
			((XClassPtrMethodGeneric) cp).returnType = method.getReturnTypePtr();
		}else if(cp instanceof XClassPtrGeneric){
			for(XClassPtr cpp:((XClassPtrGeneric) cp).genericPtrs){
				resolveClassGeneric(cpp, method);
			}
		}
	}
	
	public int getMethodIndex() {
		if(state!=STATE_TOGEN)
			throw new XRuntimeException("You can't get a method index now");
		return methodCount++;
	}
	
	@Override
	public void visitBlock(XBlock xBlock) {
		if(staticInit==null){
			staticInit = new ArrayList<XTree.XStatement>();
		}
		staticInit.add(xBlock);
	}

	@Override
	public void visitBreak(XBreak xBreak) {
		shouldNeverCalled();
	}

	@Override
	public void visitContinue(XContinue xContinue) {
		shouldNeverCalled();
	}

	@Override
	public void visitDo(XDo xDo) {
		shouldNeverCalled();
	}

	@Override
	public void visitWhile(XWhile xWhile) {
		shouldNeverCalled();
	}

	@Override
	public void visitFor(XFor xFor) {
		shouldNeverCalled();
	}

	@Override
	public void visitIf(XIf xIf) {
		shouldNeverCalled();
	}

	@Override
	public void visitReturn(XReturn xReturn) {
		shouldNeverCalled();
	}

	@Override
	public void visitThrow(XThrow xThrow) {
		shouldNeverCalled();
	}

	@Override
	public void visitVarDecls(XVarDecls xVarDecls) {
		visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XGroup xGroup) {
		shouldNeverCalled();
	}

	@Override
	public void visitSynchronized(XSynchronized xSynchroized) {
		shouldNeverCalled();
	}

	@Override
	public void visitConstant(XConstant xConstant) {
		shouldNeverCalled();
	}

	@Override
	public void visitMethodCall(XMethodCall xMethodCall) {
		shouldNeverCalled();
	}

	@Override
	public void visitNew(XNew xNew) {
		if(isEnum()){
			if(staticInit==null){
				staticInit = new ArrayList<XTree.XStatement>();
			}
			XClassPtr type;
			String name = getName();
			if(getGenericParams()>0){
				XClassPtr generics[] = new XClassPtr[getGenericParams()];
				for(int i=0; i<generics.length; i++){
					generics[i] = new XClassPtrClassGeneric(name, getGenericInfo(i).getName());
				}
				type = new XClassPtrGeneric(name, generics);
			}else{
				type = new XClassPtrClass(name);
			}
			fieldList.add(new XField(this, xscript.runtime.XModifier.STATIC | xscript.runtime.XModifier.FINAL | 
					xscript.runtime.XModifier.PUBLIC, xNew.type.name.name, type, new xscript.runtime.XAnnotation[0]));
			XIdent left = new XIdent(xNew.line, xNew.type.name.name);
			xNew.params.add(0, new XConstant(xNew.line, new XConstantValue(enumNames.size())));
			xNew.params.add(0, new XConstant(xNew.line, new XConstantValue(xNew.type.name.name)));
			XNew right = new XNew(xNew.line, new XType(xNew.line, new XIdent(xNew.line, getName()), null, 0), xNew.params, xNew.classDecl);
			staticInit.add(new XOperatorStatement(xNew.line, left, XOperator.LET, right));
			enumNames.add(xNew.type.name.name);
		}else{
			shouldNeverCalled();
		}
	}

	@Override
	public void visitOperator(XOperatorStatement xOperatorStatement) {
		shouldNeverCalled();
	}

	@Override
	public void visitOperatorPrefixSuffix(XOperatorPrefixSuffix xOperatorPrefixSuffix) {
		shouldNeverCalled();
	}

	@Override
	public void visitIndex(XIndex xIndex) {
		shouldNeverCalled();
	}

	@Override
	public void visitIfOperator(XIfOperator xIfOperator) {
		shouldNeverCalled();
	}

	@Override
	public void visitCast(XCast xCast) {
		shouldNeverCalled();
	}

	@Override
	public void visitLambda(XLambda xLambda) {
		shouldNeverCalled();
	}

	@Override
	public void visitTry(XTry xTry) {
		shouldNeverCalled();
	}

	@Override
	public void visitCatch(XCatch xCatch) {
		shouldNeverCalled();
	}

	@Override
	public void visitNewArray(XNewArray xNewArray) {
		shouldNeverCalled();
	}

	@Override
	public void visitArrayInitialize(XArrayInitialize xArrayInitialize) {
		shouldNeverCalled();
	}

	@Override
	public void visitForeach(XForeach xForeach) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitLable(XLable xLable) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitSwitch(XSwitch xSwitch) {
		shouldNeverCalled();
	}

	@Override
	public void visitCase(XCase xCase) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitThis(XThis xThis) {
		shouldNeverCalled();
	}

	@Override
	public void visitSuper(XSuper xSuper) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitInstanceof(XInstanceof xInstanceof) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitAssert(XAssert xAssert) {
		shouldNeverCalled();
	}
	
	@Override
	public void visitCompiled(XCompiledPart xCompiledPart) {
		shouldNeverCalled();
	}
	
	private void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}

	public XClassPtr getGenericClass(XType type, XGenericInfo[] extra) {
		return importHelper.getGenericClass(this, type, null, extra, true);
	}
	
	public XClassPtr[] getGenericClasses(List<XType> types, XGenericInfo[] extra) {
		if(types==null)
			return new XClassPtr[0];
		XClassPtr[] ptr = new XClassPtr[types.size()];
		for(int i=0; i<ptr.length; i++){
			ptr[i] = getGenericClass(types.get(i), extra);
		}
		return ptr;
	}

	public Collection<XPackage> getChildren() {
		return childs.values();
	}

	public XField getLengthField() {
		return getField("length");
	}
	
}
