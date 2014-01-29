package xscript.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import xscript.compiler.classtypes.XVarType;
import xscript.compiler.dumyinstruction.XInstructionDumyGetLocalField;
import xscript.compiler.dumyinstruction.XInstructionDumyReadLocal;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTree.XTag;
import xscript.compiler.tree.XTree.XTreeAnnotation;
import xscript.compiler.tree.XTree.XTreeAnnotationEntry;
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
import xscript.compiler.tree.XTree.XTreeStatement;
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
import xscript.compiler.tree.XTreeSearch;
import xscript.compiler.tree.XVisitor;
import xscript.runtime.XAnnotation;
import xscript.runtime.XAnnotationEntry;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.clazz.XPrimitive;
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
	public static final int STATE_ISGEN=7;
	public static final int STATE_GENM=8;
	public static final int STATE_ISGENM=9;
	
	private XMessageList messages;
	
	private List<XMethod> methodList;
	
	private List<XField> fieldList;
	
	private HashMap<String, XSyntheticField> syntheticFields;
	
	private HashMap<String, XSyntheticField> syntheticVars;
	
	private List<XTreeStatement> staticInit;
	
	private List<XTreeStatement> init;
	
	private XImportHelper importHelper;
	
	private boolean errored;
	
	private boolean visitConstructor;
	
	private boolean hasAssertions;
	
	private List<String> enumNames = new ArrayList<String>();
	
	private XTreeClassDecl classDecl;
	
	private XLineDesk declLine;
	
	private boolean hasEnumChildClasses;
	
	private XMethodSearch superEnumCall;
	
	protected XClassCompiler(XVirtualMachine virtualMachine, String name, XMessageList messages, XImportHelper importHelper, XPackage p, XMethodSearch search) {
		super(virtualMachine, name, p);
		this.messages = messages;
		this.importHelper = importHelper;
		this.superEnumCall = search;
		state = STATE_TOGEN;
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		messages.postMessage(level, "compiler."+key, lineDesk, args);
		errored = true;
	}
	
	protected XMessageList getMessageList(){
		return messages;
	}
	
	public boolean canCastTo(XClass xClass){
		if(xClass instanceof XClassCompiler){
			if(this==xClass)
				return true;
			for(XClassPtr ptr:superClasses){
				if(ptr.getXClassNonNull(virtualMachine).canCastTo(xClass))
					return true;
			}
			return false;
		}
		return xClass.getClassTable(this)!=null;
	}
	
	public void gen() {
		if(errored)
			state = STATE_ERRORED;
		if(state==STATE_TOGEN){
			state = STATE_ISGEN;
			System.out.println("gen:"+getName());
			if(parent instanceof XClassCompiler){
				((XClassCompiler) parent).onRequest();
				((XClassCompiler) parent).gen();
			}else if(parent instanceof XMethodCompiler){
				XClassCompiler cc = ((XMethodCompiler) parent).getDeclaringClassCompiler();
				cc.onRequest();
				cc.gen();
			}
			for(XClassPtr superClass:superClasses){
				XClass c = superClass.getXClassNonNull(virtualMachine);
				if(c instanceof XClassCompiler){
					((XClassCompiler) c).gen();
				}
			}
			for(XMethod method:methods){
				((XMethodCompiler)method).compile();
			}
			List<XField> toAdd = new ArrayList<XField>();
			Iterator<XSyntheticField> i = syntheticFields.values().iterator();
			while(i.hasNext()){
				XSyntheticField field = i.next();
				if(field.getReads()!=0 || field.getWrites()!=0){
					toAdd.add(field);
					field.getIndex();
				}else{
					i.remove();
				}
			}
			i = syntheticVars.values().iterator();
			while(i.hasNext()){
				XSyntheticField field = i.next();
				if(field.getReads()!=0 || field.getWrites()!=0){
					toAdd.add(field);
					field.getIndex();
				}else{
					i.remove();
				}
			}
			XField[] oldFields = fields;
			fields = new XField[oldFields.length+toAdd.size()];
			for(int j=0; j<oldFields.length; j++){
				fields[j] = oldFields[j];
				if(oldFields[j] instanceof XFieldCompiler){
					XFieldCompiler field = (XFieldCompiler) oldFields[j];
					if(XModifier.isPrivate(field.getModifier()) && field.getReads()==0){
						compilerError(XMessageLevel.WARNING, "var.never.used", field.getDeclLine(), field.getName());
					}
				}
			}
			for(int j=0; j<toAdd.size(); j++){
				fields[j+oldFields.length] = toAdd.get(j);
			}
			for(XMethod method:methods){
				((XMethodCompiler)method).change();
			}
			for(XMethod method:methods){
				((XMethodCompiler)method).gen();
			}
			setupClassTable();
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
	
	protected void registerClass(XTreeClassDecl classDecl){
		((XCompiler)virtualMachine).childToCompile(this);
		registerClasses(classDecl.defs);
		this.classDecl = classDecl;
	}
	
	private void registerClasses(List<XTree> defs){
		if(importHelper==null){
			importHelper = new XImportHelper((XCompiler) virtualMachine, this);
		}
		for(XTree tree:defs){
			if(tree instanceof XTreeClassDecl){
				XTreeClassDecl decl = (XTreeClassDecl)tree;
				String name = getName()+"."+decl.name;
				XClassCompiler compiler = new XClassCompiler(virtualMachine, decl.name, new XMessageClass((XCompiler) virtualMachine, name), importHelper, this, null);
				if(childs.containsKey(decl.name)){
					compilerError(XMessageLevel.ERROR, "duplicatedclass", decl.line, decl.name);
				}else{
					addChild(compiler);
					compiler.registerClass(decl);
				}
			}
		}
	}
	
	private void registerClasses(XTreeClassFile xClassFile){
		XPackage xPackage = getParent();
		boolean gotFirst = false;
		for(XTree tree:xClassFile.defs){
			if(tree instanceof XTreeClassDecl){
				XTreeClassDecl decl = (XTreeClassDecl)tree;
				if(gotFirst){
					String name = xPackage.getName()+"."+decl.name;
					XClassCompiler compiler = new XClassCompiler(virtualMachine, decl.name, new XMessageClass((XCompiler) virtualMachine, name), importHelper, this, null);
					if(xPackage.getChild(decl.name)==null){
						xPackage.addChild(compiler);
						compiler.registerClasses(decl.defs);
					}else{
						compilerError(XMessageLevel.ERROR, "duplicatedclass", decl.line, decl.name);
					}
				}else{
					registerClass(decl);
					gotFirst = true;
				}
			}
		}
	}
	
	@Override
	public void visitTopLevel(XTreeClassFile xClassFile) {
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
			for(XTree tree:xClassFile.defs){
				if(tree instanceof XTreeImport){
					importHelper.addImport(this, (XTreeImport)tree);
				}
			}
		}
	}
	
	@Override
	public void onRequest(){
		if(classDecl!=null){
			XTreeClassDecl xClassDef = classDecl;
			declLine = xClassDef.line;
			classDecl = null;
			if(!xClassDef.name.equals(getSimpleName())){
				compilerError(XMessageLevel.ERROR, "wrong.classname", xClassDef.line, getSimpleName(), xClassDef.name);
			}
			if(xClassDef.typeParam==null){
				genericInfos = new XGenericInfo[0];
			}else{
				genericInfos = new XGenericInfo[xClassDef.typeParam.size()];
				for(int i=0; i<genericInfos.length; i++){
					XTreeTypeParam typeParam = xClassDef.typeParam.get(i);
					genericInfos[i] = new XGenericInfo(typeParam.name, new XClassPtr[0], typeParam.isSuper);
				}
				for(int i=0; i<genericInfos.length; i++){
					XTreeTypeParam typeParam = xClassDef.typeParam.get(i);
					XClassPtr[] ptr = getGenericClasses(typeParam.extend, null);
					genericInfos[i] = new XGenericInfo(typeParam.name, ptr, typeParam.isSuper);
				}
			}
			boolean extendsOtherOuter = false;
			if(xClassDef.superClasses==null){
				if(getName().equals(XConstants.CLASS_OBJECT)){
					superClasses = new XClassPtr[0];
				}else{
					superClasses = new XClassPtr[1];
					superClasses[0] = new XClassPtrClass(XConstants.CLASS_OBJECT);
					try{
						superClasses[0].getXClassNonNull(virtualMachine);
					}catch(Exception e){}
				}
			}else{
				superClasses = getGenericClasses(xClassDef.superClasses, null);
				int k = 0;
				for(XClassPtr superClass:superClasses){
					XClass c = superClass.getXClassNonNull(getVirtualMachine());
					int modifier = c.getModifier();
					if(XModifier.isFinal(modifier)){
						compilerError(XMessageLevel.ERROR, "super.isfinal", xClassDef.superClasses.get(k).line, c.getName());
					}
					if(c.isEnum()){
						XMethod m = getOuterMethod();
						if(c.isIndirectEnum() || superClasses.length!=1 || !m.isConstructor() || !XModifier.isStatic(m.getModifier()) || m.getDeclaringClass()!=c){
							compilerError(XMessageLevel.ERROR, "cant.extend.enum", xClassDef.superClasses.get(k).line, c.getName());
						}
					}
					if(!XModifier.isStatic(c.getModifier())){
						if(c.getOuterClass()!=null){
							if(getOuterClass()==c.getOuterClass() && !XModifier.isStatic(modifier)){
								extendsOtherOuter = true;
							}else{
								compilerError(XMessageLevel.ERROR, "cant.extend.inner.class", xClassDef.line, c.getName());
							}
						}
						if(c.getOuterMethod()!=null){
							if(getOuterMethod()==c.getOuterMethod() && !XModifier.isStatic(modifier)){
								extendsOtherOuter = true;
							}else{
								compilerError(XMessageLevel.ERROR, "cant.extend.inner.class", xClassDef.line, c.getName());
							}
						}
					}
					k++;
				}
			}
			modifier = xClassDef.modifier==null?0:xClassDef.modifier.modifier;
			if(isAnnotation()){
				modifier |= XModifier.FINAL;
			}else if(isIndirectEnum()){
				modifier |= XModifier.FINAL | XModifier.STATIC;
			}
			annotations = makeAnnotations(xClassDef.modifier);
			methodList = new ArrayList<XMethod>();
			fieldList = new ArrayList<XField>();
			syntheticFields = new HashMap<String, XSyntheticField>();
			syntheticVars = new HashMap<String, XSyntheticField>();
			visitTree(xClassDef.defs);
			if(!hasEnumChildClasses && isEnum()){
				modifier |= XModifier.FINAL;
			}
			if(!visitConstructor){
				if(isIndirectEnum() && superEnumCall!=null){
					try{
						XVarType[] types = superEnumCall.getTypes();
						XClassPtr params[] = new XClassPtr[types.length];
						XAnnotation paramAnnotations[][] = new XAnnotation[types.length][0];
						for(int i=0; i<types.length; i++){
							params[i] = types[i].getXClassPtr();
						}
						XMethodCompiler method = new XMethodCompiler(this, XModifier.PRIVATE, XMethod.INIT, new XClassPtrClass(XPrimitive.VOID_NAME), 
								new XAnnotation[0], params, paramAnnotations, new XClassPtr[0], new XGenericInfo[0], null, importHelper, superEnumCall);
						addChild(method);
						methodList.add(method);
					}catch(XRuntimeException e){
						compilerError(XMessageLevel.ERROR, "intern", XLineDesk.NULL, e.getMessage());
					}
				}else{
					XTreeMethodDecl decl = new XTreeMethodDecl(XLineDesk.NULL, new XTreeModifier(XLineDesk.NULL, XModifier.PUBLIC), 
							XMethod.INIT, null, new XTreeType(XLineDesk.NULL, new XTreeIdent(XLineDesk.NULL, XPrimitive.VOID_NAME), null, 0), null, null, null, null, false);
					decl.accept(this);
				}
			}
			XCodeGen assertionCodeGen = null;
			if(hasAssertions){
				if(staticInit == null){
					staticInit = new ArrayList<XTree.XTreeStatement>();
				}
				assertionCodeGen = new XCodeGen();
				staticInit.add(0, new XTreeCompiledPart(XLineDesk.NULL, assertionCodeGen));
			}
			if(staticInit != null){
				XTreeMethodDecl staticMethodDecl = new XTreeMethodDecl(XLineDesk.NULL, new XTreeModifier(XLineDesk.NULL, XModifier.STATIC | XModifier.PRIVATE), 
						XMethod.STATIC_INIT, null, new XTreeType(XLineDesk.NULL, new XTreeIdent(XLineDesk.NULL, XPrimitive.VOID_NAME), null, 0), null, null,
						new XTreeBlock(XLineDesk.NULL, staticInit), null, false);
				staticMethodDecl.accept(this);
			}
			if(isEnum()){
				XClassPtr array = new XClassPtrGeneric("xscript.lang.Array", new XClassPtr[]{new XClassPtrClass(getName())});
				XFieldCompiler field = addSyntheticField(XModifier.PRIVATE | XModifier.FINAL | XModifier.STATIC,
						"values", array);
				if(staticInit != null){
					XCodeGen codeGen = new XCodeGen();
					codeGen.addInstruction(new XInstructionLoadConstInt(enumNames.size()), 0);
					codeGen.addInstruction(new XInstructionNewArray(array, 1), 0);
					XMethod method = getVirtualMachine().getClassProvider().getXClass("xscript.lang.Array").getMethod("operator[](int, T)T");
					for(int i=0; i<enumNames.size(); i++){
						codeGen.addInstruction(new XInstructionODup(), 0);
						codeGen.addInstruction(new XInstructionLoadConstInt(i), 0);
						codeGen.addInstruction(new XInstructionGetStaticField(fieldList.get(i)), 0);
						codeGen.addInstruction(new XInstructionInvokeDynamic(method, new XClassPtr[0]), 0);
						codeGen.addInstruction(new XInstructionOPop(), 0);
					}
					field.incWrites();
					codeGen.addInstruction(new XInstructionSetStaticField(field), 0);
					codeGen.addInstruction(new XInstructionOPop(), 0);
					staticInit.add(new XTreeCompiledPart(XLineDesk.NULL, codeGen));
				}
			}else if(getOuterClass()!=null && !XModifier.isStatic(modifier) && !extendsOtherOuter){
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
				addSyntheticField(XModifier.PROTECTED | XModifier.FINAL, "outer", type);
			}else if(getOuterMethod()!=null && !XModifier.isStatic(modifier) && !extendsOtherOuter && !XModifier.isStatic(getOuterMethod().getModifier())){
				XClass outer = getOuterMethod().getDeclaringClass();
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
				addSyntheticField(XModifier.PROTECTED | XModifier.FINAL, "outer", type);
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
				XFieldCompiler field = addSyntheticField(XModifier.PRIVATE | XModifier.FINAL | XModifier.STATIC, "assertionsDisabled", new XClassPtrClass(XPrimitive.BOOL_NAME));
				assertionCodeGen.addInstruction(new XInstructionLoadConstClass(type), 0);
				XMethod m = getVirtualMachine().getClassProvider().getXClass("xscript.lang.Class").getMethod("desiredAssertionStatus()bool");
				assertionCodeGen.addInstruction(new XInstructionInvokeDynamic(m, new XClassPtr[0]), 0);
				field.incWrites();
				assertionCodeGen.addInstruction(new XInstructionSetStaticField(field), 0);
			}
			annotations = new xscript.runtime.XAnnotation[0];
			methods = methodList.toArray(new XMethod[methodList.size()]);
			fields = fieldList.toArray(new XField[fieldList.size()]);
			fieldList = null;
			methodList = null;
			((XCompiler)virtualMachine).toCompile(this);
		}
	}
	
	@Override
	public void visitImport(XTreeImport xImport) {
		XError.shouldNeverCalled();
	}
	
	private XAnnotation[] makeAnnotations(XTreeModifier modifier){
		if(modifier==null)
			return new XAnnotation[0];
		return makeAnnotations(modifier.annotations);
	}
	
	private XAnnotation[] makeAnnotations(List<XTreeAnnotation> annotations){
		if(annotations==null)
			return new XAnnotation[0];
		XAnnotation[] a = new XAnnotation[annotations.size()];
		for(int i=0; i<a.length; i++){
			a[i] = makeAnnotation(annotations.get(i));
		}
		return a;
	}
	
	private XAnnotation makeAnnotation(XTreeAnnotation annotation){
		XClassPtr cp = getGenericClass(new XTreeType(annotation.annotation.line, annotation.annotation, null, 0), null);
		XClass c = cp.getXClassNonNull(virtualMachine);
		XAnnotationEntry[] entries;
		if(annotation.entries==null){
			entries = new XAnnotationEntry[0];
		}else{
			entries = new XAnnotationEntry[annotation.entries.size()];
			for(int i=0; i<entries.length; i++){
				entries[i] = makeAnnotationEntry(annotation.entries.get(i));
			}
		}
		return new XAnnotation(c.getName(), entries);
	}
	
	private XAnnotationEntry makeAnnotationEntry(XTreeAnnotationEntry annotationEntry){
		String name = annotationEntry.name.name;
		XTreeStatement v = annotationEntry.value;
		//TODO
		int type;
		Object[] value;
		return null;//new XAnnotationEntry(name, type, value);
	}
	
	private XSyntheticField addSyntheticField(int modifier, String name, XClassPtr type){
		XSyntheticField field = new XSyntheticField(this, modifier | XModifier.SYNTHETIC, name, type, new xscript.runtime.XAnnotation[0], declLine);
		syntheticFields.put(name, field);
		field.checkName(this);
		addChild(field);
		return field;
	}
	
	private XSyntheticField addSyntheticVar(int modifier, String name, XClassPtr type){
		XSyntheticField field = new XSyntheticField(this, modifier | XModifier.SYNTHETIC, name, type, new xscript.runtime.XAnnotation[0], declLine);
		syntheticVars.put(name, field);
		field.checkName(this);
		addChild(field);
		return field;
	}
	
	public XFieldCompiler getSyntheticField(String name){
		return syntheticFields.get(name);
	}
	
	public XFieldCompiler getSyntheticFieldAndParents(String name) {
		XFieldCompiler field = getSyntheticField(name);
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
	public XField getField(String name) {
		XField field = super.getField(name);
		if(field==null){
			field = syntheticVars.get(name);
		}
		return field;
	}

	@Override
	public void visitClassDecl(XTreeClassDecl xClassDef) {}

	@Override
	public void visitAnnotation(XTreeAnnotation xAnnotation) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitModifier(XTreeModifier xModifier) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitError(XTreeError xError) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitIdent(XTreeIdent xIdent) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitType(XTreeType xType) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitTypeParam(XTreeTypeParam xTypeParam) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitVarDecl(XTreeVarDecl xVarDecl) {
		int modifier;
		if(xVarDecl.modifier==null){
			modifier = 0;
		}else{
			modifier = xVarDecl.modifier.modifier;
		}
		XClassPtr type = getGenericClass(xVarDecl.type, null);
		xscript.runtime.XAnnotation[] annotations = makeAnnotations(xVarDecl.modifier);
		try{
			XField field = new XFieldCompiler(this, modifier, xVarDecl.name, type, annotations, xVarDecl.line);
			XPackage c = getChild(field.getSimpleName());
			if(c instanceof XSyntheticField){
				((XSyntheticField) c).inc();
				c = null;
			}
			if(c==null){
				addChild(field);
			}else{
				compilerError(XMessageLevel.ERROR, "duplicatedfield", xVarDecl.line, xVarDecl.name);
			}
			fieldList.add(field);
			if(xVarDecl.init!=null){
				if(xVarDecl.modifier!=null && XModifier.isStatic(xVarDecl.modifier.modifier)){
					if(staticInit==null)
						staticInit = new ArrayList<XTree.XTreeStatement>();
					staticInit.add(new XTreeOperatorStatement(xVarDecl.line, new XTreeIdent(xVarDecl.line, xVarDecl.name), XOperator.LET, xVarDecl.init));
				}else{
					if(init==null)
						init = new ArrayList<XTree.XTreeStatement>();
					XTreeOperatorStatement thisName = new XTreeOperatorStatement(xVarDecl.line, new XTreeIdent(xVarDecl.line, "this"), XOperator.ELEMENT, 
							new XTreeIdent(xVarDecl.line, xVarDecl.name));
					init.add(new XTreeOperatorStatement(xVarDecl.line, thisName, XOperator.LET, xVarDecl.init));
				}
			}
		}catch(XRuntimeException e){
			compilerError(XMessageLevel.ERROR, "intern", xVarDecl.line, e.getMessage());
		}
	}
	
	@Override
	public int getStaticFieldIndex(int sizeInObject) {
		if(state!=STATE_TOGEN && state!=STATE_ISGEN)
			throw new XRuntimeException("You can't get a field index now");
		int ret = staticFieldCount;
		staticFieldCount += sizeInObject;
		return ret;
	}

	@Override
	public int getFieldIndex(int sizeInObject) {
		if(state!=STATE_TOGEN && state!=STATE_ISGEN)
			throw new XRuntimeException("You can't get a field index now");
		int ret = fieldCount;
		fieldCount += sizeInObject;
		return ret;
	}
	
	@Override
	public int getEnumIndex() {
		if(!isEnum())
			throw new XRuntimeException("This is not a enum");
		if(state!=STATE_TOGEN && state!=STATE_ISGEN)
			throw new XRuntimeException("You can't get a enum index now");
		return enumCount ++;
	}
	
	@Override
	public void visitMethodDecl(XTreeMethodDecl xMethodDecl) {
		int modifier;
		if(xMethodDecl.modifier==null){
			modifier = 0;
		}else{
			modifier = xMethodDecl.modifier.modifier;
		}
		if(xMethodDecl.varargs){
			modifier |= XModifier.VARARGS;
		}
		xscript.runtime.XAnnotation[] annotations = makeAnnotations(xMethodDecl.modifier);
		List<XClassPtr> classes = new ArrayList<XClassPtr>();
		XGenericInfo[] genericInfos;
		if(xMethodDecl.typeParam==null){
			genericInfos = new XGenericInfo[0];
		}else{
			genericInfos = new XGenericInfo[xMethodDecl.typeParam.size()];
			for(int i=0; i<genericInfos.length; i++){
				XTreeTypeParam typeParam = xMethodDecl.typeParam.get(i);
				genericInfos[i] = new XGenericInfo(typeParam.name, new XClassPtr[0], typeParam.isSuper);
			}
			for(int i=0; i<genericInfos.length; i++){
				XTreeTypeParam typeParam = xMethodDecl.typeParam.get(i);
				XClassPtr[] ptr = getGenericClasses(typeParam.extend, genericInfos);
				classes.addAll(Arrays.asList(ptr));
				genericInfos[i] = new XGenericInfo(typeParam.name, ptr, typeParam.isSuper);
			}
		}
		XClassPtr returnType = getGenericClass(xMethodDecl.returnType, genericInfos);
		classes.add(returnType);
		XClassPtr[] paramTypes;
		int size = xMethodDecl.paramTypes==null?0:xMethodDecl.paramTypes.size();
		int ss = 0;
		if(xMethodDecl.name.equals(XMethod.INIT)){
			if(isEnum()){
				ss = 2;
				paramTypes = new XClassPtr[size+2];
				paramTypes[0] = new XClassPtrClass("xscript.lang.String");
				paramTypes[1] = new XClassPtrClass(XPrimitive.INT_NAME);
			}else if(getOuterClass()!=null && !XModifier.isStatic(this.modifier)){
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
			}else if(getOuterMethod()!=null && !XModifier.isStatic(this.modifier) && !XModifier.isStatic(getOuterMethod().getModifier())){
				ss = 1;
				paramTypes = new XClassPtr[size+1];
				XClass outer = getOuterMethod().getDeclaringClass();
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
		if(xMethodDecl.name.equals(XMethod.INIT)){
			visitConstructor = true;
		}
		XAnnotation[][] paramAnnotations = new XAnnotation[paramTypes.length][];
		for(int i=0; i<ss; i++){
			paramAnnotations[i] = new XAnnotation[0];
		}
		for(int i=0; i<size; i++){
			paramAnnotations[i+ss] = makeAnnotations(xMethodDecl.paramTypes.get(i).modifier);
		}
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
	public void visitBlock(XTreeBlock xBlock) {
		if(staticInit==null){
			staticInit = new ArrayList<XTree.XTreeStatement>();
		}
		staticInit.add(xBlock);
	}

	@Override
	public void visitBreak(XTreeBreak xBreak) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitContinue(XTreeContinue xContinue) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitDo(XTreeDo xDo) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitWhile(XTreeWhile xWhile) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitFor(XTreeFor xFor) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitIf(XTreeIf xIf) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitReturn(XTreeReturn xReturn) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitThrow(XTreeThrow xThrow) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitVarDecls(XTreeVarDecls xVarDecls) {
		visitTree(xVarDecls.varDecls);
	}

	@Override
	public void visitGroup(XTreeGroup xGroup) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitSynchronized(XTreeSynchronized xSynchroized) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitConstant(XTreeConstant xConstant) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitMethodCall(XTreeMethodCall xMethodCall) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitNew(XTreeNew xNew) {
		if(isEnum()){
			if(staticInit==null){
				staticInit = new ArrayList<XTree.XTreeStatement>();
			}
			if(xNew.classDecl!=null){
				hasEnumChildClasses = true;
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
			fieldList.add(new XField(this, XModifier.STATIC | XModifier.FINAL | 
					XModifier.PUBLIC | XField.ENUMFIELD, xNew.type.name.name, type, new xscript.runtime.XAnnotation[0]));
			XTreeIdent left = new XTreeIdent(xNew.line, xNew.type.name.name);
			xNew.params.add(0, new XTreeConstant(xNew.line, new XConstantValue(enumNames.size())));
			xNew.params.add(0, new XTreeConstant(xNew.line, new XConstantValue(xNew.type.name.name)));
			XTreeNew right = new XTreeNew(XLineDesk.NULL, new XTreeType(xNew.line, new XTreeIdent(xNew.line, getName()), null, 0), xNew.params, xNew.classDecl);
			staticInit.add(new XTreeOperatorStatement(xNew.line, left, XOperator.LET, right));
			enumNames.add(xNew.type.name.name);
		}else{
			XError.shouldNeverCalled();
		}
	}

	@Override
	public void visitOperator(XTreeOperatorStatement xOperatorStatement) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix xOperatorPrefixSuffix) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitIndex(XTreeIndex xIndex) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitIfOperator(XTreeIfOperator xIfOperator) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitCast(XTreeCast xCast) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitLambda(XTreeLambda xLambda) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitTry(XTreeTry xTry) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitCatch(XTreeCatch xCatch) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitNewArray(XTreeNewArray xNewArray) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitArrayInitialize(XTreeArrayInitialize xArrayInitialize) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitForeach(XTreeForeach xForeach) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitLable(XTreeLable xLable) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitSwitch(XTreeSwitch xSwitch) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitCase(XTreeCase xCase) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitThis(XTreeThis xThis) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitSuper(XTreeSuper xSuper) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitInstanceof(XTreeInstanceof xInstanceof) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitAssert(XTreeAssert xAssert) {
		XError.shouldNeverCalled();
	}
	
	@Override
	public void visitCompiled(XTreeCompiledPart xCompiledPart) {
		XError.shouldNeverCalled();
	}

	@Override
	public void visitAnnotationEntry(XTreeAnnotationEntry xTreeAnnotationEntry) {
		XError.shouldNeverCalled();
	}
	
	public XClassPtr getGenericClass(XTreeType type, XGenericInfo[] extra) {
		if(getOuterMethod()==null){
			return importHelper.getGenericClass(this, type, null, extra, true);
		}else{
			return ((XMethodCompiler)getOuterMethod()).getGenericClass(this, type, null, extra, true);
		}
	}
	
	public XClassPtr[] getGenericClasses(List<XTreeType> types, XGenericInfo[] extra) {
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

	public void addVars(HashMap<String, XVariable> vars) {
		for(XVariable var:vars.values()){
			if(!var.name.equals("this") && XModifier.isFinal(var.modifier))
				addSyntheticVar((var.modifier & ~(XModifier.PUBLIC | XModifier.PRIVATE)) | XModifier.PROTECTED | XModifier.FINAL, var.name, var.type.getXClassPtr());
		}
	}

	public List<XSyntheticField> getSyntheticVars() {
		List<XSyntheticField> list = new ArrayList<XSyntheticField>(syntheticVars.values());
		for(XClassPtr superClass:superClasses){
			XClass c = superClass.getXClassNonNull(getVirtualMachine());
			if(c instanceof XClassCompiler){
				list.addAll(((XClassCompiler) c).getSyntheticVars());
			}
		}
		return list;
	}
	
	public int enumOrdinal(String name){
		return enumNames.indexOf(name);
	}

	public void checkAccess(XClass c, XLineDesk line) {
		if(getVirtualMachine()!=c.getVirtualMachine()){
			throw new XRuntimeException("%s has a diferent VM than %s", this, c);
		}
		int modifier = c.getModifier();
		XClass checkClass1 = this;
		while(checkClass1.getOuterClassOrMethodDeclClass()!=null){
			checkClass1 = checkClass1.getOuterClassOrMethodDeclClass();
		}
		XClass checkClass2 = c;
		while(checkClass2.getOuterClassOrMethodDeclClass()!=null){
			checkClass2 = checkClass2.getOuterClassOrMethodDeclClass();
		}
		boolean sameOuterClass = checkClass1==checkClass2;
		if(XModifier.isPrivate(modifier)){
			if(!sameOuterClass){
				compilerError(XMessageLevel.ERROR, "unable.to.access", line, this, c);
			}
		}else if(XModifier.isProtected(modifier)){
			if(!sameOuterClass && !canCastTo(c) && getPackage()!=c.getPackage()){
				compilerError(XMessageLevel.ERROR, "unable.to.access", line, this, c);
			}
		}else if(!XModifier.isPublic(modifier)){
			if(!sameOuterClass && getPackage()!=c.getPackage()){
				compilerError(XMessageLevel.ERROR, "unable.to.access", line, this, c);
			}
		}
	}

	public List<XTreeStatement> getInitStatements() {
		return init;
	}
	
}
