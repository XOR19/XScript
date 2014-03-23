package xscript.compiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xscript.compiler.classtypes.XVarType;
import xscript.compiler.dumyinstruction.XInstructionDumyDelete;
import xscript.compiler.dumyinstruction.XInstructionDumyInvokeConstructor;
import xscript.compiler.dumyinstruction.XInstructionDumyReadLocal;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree.XTreeMethodDecl;
import xscript.compiler.tree.XTree.XTreeType;
import xscript.runtime.XAnnotation;
import xscript.runtime.XModifier;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.instruction.XInstructionInvokeConstructor;
import xscript.runtime.instruction.XInstructionInvokeSpecial;
import xscript.runtime.instruction.XInstructionNew;
import xscript.runtime.instruction.XInstructionODup;
import xscript.runtime.instruction.XInstructionOPop;
import xscript.runtime.instruction.XInstructionPop;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.instruction.XInstructionSetLocalField;
import xscript.runtime.instruction.XInstructionThrow;
import xscript.runtime.method.XCatchEntry;
import xscript.runtime.method.XLineEntry;
import xscript.runtime.method.XLocalEntry;
import xscript.runtime.method.XMethod;

public class XMethodCompiler extends XMethod {

	private XTreeMethodDecl xMethodDecl;
	
	private XImportHelper importHelper;
	
	private XCodeGen codeGen;
	
	private boolean hasChildClasses;
	
	private XMethodSearch search;
	
	private boolean errored;
	
	public XMethodCompiler(XClass declaringClass, int modifier, String name,
			XClassPtr returnType, xscript.runtime.XAnnotation[] annotations,
			XClassPtr[] params, xscript.runtime.XAnnotation[][] paramAnnotations,
			XClassPtr[] mThrows, XGenericInfo[] genericInfos, XTreeMethodDecl xMethodDecl, 
			XImportHelper importHelper) {
		super(declaringClass, modifier, name, returnType, annotations, params,
				paramAnnotations, mThrows, genericInfos);
		this.xMethodDecl = xMethodDecl;
		this.importHelper = importHelper;
	}
	
	public XMethodCompiler(XClass declaringClass, int modifier, String name,
			XClassPtr returnType, xscript.runtime.XAnnotation[] annotations,
			XClassPtr[] params, xscript.runtime.XAnnotation[][] paramAnnotations,
			XClassPtr[] mThrows, XGenericInfo[] genericInfos, XTreeMethodDecl xMethodDecl, 
			XImportHelper importHelper, XMethodSearch search) {
		super(declaringClass, modifier, name, returnType, annotations, params,
				paramAnnotations, mThrows, genericInfos);
		this.xMethodDecl = xMethodDecl;
		this.importHelper = importHelper;
		this.search = search;
	}

	public void compile(){
		if(xMethodDecl==null && isConstructor() && getDeclaringClass().isIndirectEnum() && search!=null){
			codeGen = new XCodeGen();
			XInstructionDumyDelete start = new XInstructionDumyDelete();
			codeGen.addInstruction(start, 0);
			List<XVariable> vars = new ArrayList<XVariable>();
			XVariable var = new XVariable();
			var.name = "this";
			var.modifier = XModifier.FINAL;
			var.type = getDeclaringClassVarType();
			var.start = start;
			codeGen.addVariable(var);
			vars.add(var);
			for(int i=0; i<params.length; i++){
				XVariable param = new XVariable();
				if(i==0){
					param.name = "$name";
				}else if(i==1){
					param.name = "$ordinal";
				}else{
					param.name = "$param_"+(i-1);
				}
				param.modifier = XModifier.FINAL | XModifier.SYNTHETIC;
				param.type = search.getTypes()[i];
				param.start = start;
				param.id = i+1;
				vars.add(param);
				codeGen.addVariable(param);
				codeGen.addInstruction(new XInstructionDumyReadLocal(param), 0);
			}
			codeGen.addInstruction(new XInstructionInvokeConstructor(search.getMethod().method, new XClassPtr[0]), 0);
			XInstructionDumyDelete end = new XInstructionDumyDelete();
			codeGen.addInstruction(end, 0);
			for(XVariable v:vars){
				v.end = end;
			}
			return;
		}
		if(!isConstructor() && (xMethodDecl.block==null || xscript.runtime.XModifier.isAbstract(modifier) || xscript.runtime.XModifier.isNative(modifier)))
			return;
		XStatementCompiler statementCompiler = new XStatementCompiler(null, null, this);
		xMethodDecl.accept(statementCompiler);
		codeGen = statementCompiler.getCodeGen();
	}
	
	public void change(){
		if(codeGen!=null && isConstructor()){
			XClassCompiler c = (XClassCompiler) getDeclaringClass();
			if(!XModifier.isStatic(modifier) && c.getOuterMethod()!=null && !c.isIndirectEnum()){
				int off = XModifier.isStatic(c.getOuterMethod().getModifier())?1:2;
				List<XSyntheticField> syntheticVars = c.getSyntheticVars();
				int numVars = syntheticVars.size();
				XClassPtr[] params = new XClassPtr[this.params.length+numVars];
				XAnnotation[][] annotations = new XAnnotation[params.length][];
				int i=0;
				if(!XModifier.isStatic(c.getOuterMethod().getModifier())){
					params[0] = this.params[0];
					annotations[0] = paramAnnotations[0];
					i=1;
				}
				for(XSyntheticField f:syntheticVars){
					params[i] = f.getType();
					annotations[i] = new XAnnotation[0];
					i++;
				}
				for(int j=1; j<this.params.length; j++, i++){
					params[i] = this.params[j];
					annotations[i] = paramAnnotations[j];
				}
				this.params = params;
				paramAnnotations = annotations;
				List<XInstruction> instructions = codeGen.instructions;
				int last = 1;
				boolean selvInvoke = false;
				for(i=0; i<instructions.size(); i++){
					XInstruction inst = instructions.get(i);
					if(inst instanceof XInstructionDumyInvokeConstructor){
						XMethod m = ((XInstructionDumyInvokeConstructor) inst).method;
						XClass dc = m.getDeclaringClass();
						if(dc==c)
							selvInvoke = true;
						if(dc.getOuterMethod()!=null){
							List<XSyntheticField> cSyntheticVars = ((XClassCompiler)dc).getSyntheticVars();
							int j=0;
							for(XSyntheticField sVar:cSyntheticVars){
								codeGen.addInstruction(last+2+j, new XInstructionReadLocal(syntheticVars.indexOf(sVar)+off), 0);
								j++;
							}
							i+=cSyntheticVars.size();
						}
						last = i;
					}
				}
				if(!selvInvoke){
					for(int j=0; j<numVars; j++){
						XField f = syntheticVars.get(j);
						if(f.getDeclaringClass()==getDeclaringClass()){
							codeGen.addInstruction(last+1+j*3, new XInstructionReadLocal(j+off), 0);
							codeGen.addInstruction(last+1+j*3+1, new XInstructionSetLocalField(0, f), 0);
							if(f.getTypePrimitive()==XPrimitive.OBJECT){
								codeGen.addInstruction(last+1+j*3+2, new XInstructionOPop(), 0);
							}else{
								codeGen.addInstruction(last+1+j*3+2, new XInstructionPop(), 0);
							}
						}
					}
				}
				for(XVariable var:codeGen.variables){
					if(var.id>1){
						var.id += numVars;
					}
				}
				for(int j=0; j<numVars; j++){
					XVariable var = new XVariable();
					var.start = instructions.get(0);
					var.end = instructions.get(instructions.size()-1);
					var.name = syntheticVars.get(j).getSimpleName();
					var.modifier = XModifier.FINAL | XModifier.SYNTHETIC;
					XClass cc = syntheticVars.get(j).getType().getXClassNonNull(getDeclaringClass().getVirtualMachine());
					String name = cc.getName();
					XClassPtr ptr;
					if(cc.getGenericParams()>0){
						XClassPtr generics[] = new XClassPtr[cc.getGenericParams()];
						for(int k=0; k<generics.length; k++){
							generics[k] = new XClassPtrClassGeneric(name, cc.getGenericInfo(k).getName());
						}
						ptr = new XClassPtrGeneric(name, generics);
					}else{
						ptr = new XClassPtrClass(name);
					}
					var.type = XVarType.getVarTypeFor(ptr, cc.getVirtualMachine(), null, null);
					var.id = j+off;
					codeGen.addVariable(var);
				}
			}
		}
	}
	
	public void gen(){
		if(codeGen!=null){
			if(errored){
				XClassPtr classPtr = new XClassPtrClass("xscript.lang.CompilerError");
				XClass c = classPtr.getXClassNonNull(getDeclaringClass().getVirtualMachine());
				XMethod m = c.getMethod("<init>()void");
				instructions = new XInstruction[4];
				instructions[0] = new XInstructionNew(classPtr);
				instructions[1] = new XInstructionODup();
				instructions[2] = new XInstructionInvokeSpecial(m, new XClassPtr[0]);
				instructions[3] = new XInstructionThrow();
				System.out.println("gen:"+getName()+Arrays.toString(instructions));
				if(codeGen.lines==null || codeGen.lines.isEmpty()){
					lineEntries = new XLineEntry[0];
				}else{
					lineEntries = new XLineEntry[]{new XLineEntry(0, codeGen.lines.get(0))};
				}
				catchEntries = new XCatchEntry[0];
				int num;
				if(XModifier.isStatic(modifier)){
					num = getParamCount();
				}else{
					num = getParamCount()+1;
				}
				localEntries = new XLocalEntry[num];
				for(int i=0; i<num; i++){
					XVariable variable = codeGen.variables.get(i);
					localEntries[i] = new XLocalEntry(0, 3, variable.id, variable.modifier, variable.name, variable.type.getXClassPtr());
				}
				maxStackSize = 0;
				maxObjectStackSize = 2;
			}else{
				codeGen.generateFinalCode(this);
				instructions = codeGen.getInstructions();
				lineEntries = codeGen.getLineEntries();
				catchEntries = codeGen.getCatchEntries();
				localEntries = codeGen.getLocalEntries();
				for(int i=0; i<localEntries.length; i++){
					if(maxLocalSize<localEntries[i].getIndex()+1){
						maxLocalSize = localEntries[i].getIndex()+1;
					}
				}
				maxStackSize = codeGen.getMaxStackSize();
				maxObjectStackSize = codeGen.getMaxObjectStackSize();
				codeGen = null;		
			}
		}
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		Object[] o = new Object[args.length+1];
		o[0] = this;
		System.arraycopy(args, 0, o, 1, args.length);
		((XClassCompiler)getDeclaringClass()).compilerError(level, "method."+key, lineDesk, o);
		if(level==XMessageLevel.ERROR)
			errored = true;
	} 
	
	public void addClass(XClass c, XLineDesk line){
		if(childs.containsKey(c.getSimpleName())){
			compilerError(XMessageLevel.ERROR, "class.duplicated", line, c.getSimpleName());
		}else{
			hasChildClasses = true;
			addChild(c);
			classes.add(c);
		}
	}
	
	public boolean hasChildClasses(){
		return hasChildClasses;
	}
	
	private XClassPtr getGenericClass1(XClassCompiler xClassCompiler, XTreeType type, XMethod method, XGenericInfo[] extra, boolean doError) {
		for(int i=1; i<9; i++){
			if(XPrimitive.getName(i).equals(type.name.name)){
				return new XClassPtrClass(type.name.name);
			}
		}
		if(extra!=null){
			for(XGenericInfo info:extra){
				if(info.getName().equals(type.name.name)){
					if(method==null){
						return new XClassPtrMethodGeneric(xClassCompiler.getName(), null, null, null, type.name.name);
					}else{
						return new XClassPtrMethodGeneric(xClassCompiler.getName(), method.getRealName(), method.getParams(), method.getReturnTypePtr(), type.name.name);
					}
				}
			}
		}
		String name = null;
		for(XClass c:classes){
			if(c.getSimpleName().equals(type.name.name)){
				name = c.getName();
			}
		}
		if(name==null)
			return null;
		if(type.typeParam==null){
			return new XClassPtrClass(name);
		}else{
			XClassPtr[] genericPtrs = new XClassPtr[type.typeParam.size()];
			for(int i=0; i<genericPtrs.length; i++){
				genericPtrs[i] = getGenericClass(xClassCompiler, type.typeParam.get(i), method, extra, true);
			}
			return new XClassPtrGeneric(name, genericPtrs);
		}
	}
	
	public XClassPtr getGenericClass(XClassCompiler xClassCompiler, XTreeType type, XMethod method, XGenericInfo[] extra, boolean doError) {
		XClassPtr classPtr = getGenericClass1(xClassCompiler, type, method, extra, doError);
		if(classPtr==null){
			if(getDeclaringClass().getOuterMethod()==null){
				return importHelper.getGenericClass(xClassCompiler, type, method, extra, doError);
			}else{
				return ((XMethodCompiler)getDeclaringClass().getOuterMethod()).getGenericClass(xClassCompiler, type, method, extra, doError);
			}
		}
		if(type.array>0){
			XClass c = classPtr.getXClass(xClassCompiler.getVirtualMachine());
			if(c==null || XPrimitive.getPrimitiveID(c)==XPrimitive.OBJECT){
				classPtr = new XClassPtrGeneric("xscript.lang.Array", new XClassPtr[]{classPtr});
			}else{
				classPtr = new XClassPtrClass("xscript.lang.Array"+XPrimitive.getWrapper(XPrimitive.getPrimitiveID(c)));
			}
			for(int i=1; i<type.array; i++){
				classPtr = new XClassPtrGeneric("xscript.lang.Array", new XClassPtr[]{classPtr});
			}
		}
		return classPtr;
	}
	
	public XClassPtr getGenericClass(XTreeType type, boolean doError){
		return getGenericClass((XClassCompiler)getDeclaringClass(), type, this, genericInfos, doError);
	}

	public XImportHelper getImportHelper() {
		return importHelper;
	}

	public XClassPtr getGenericReturnType() {
		return returnType;
	}

	public XVarType getDeclaringClassVarType() {
		XClass c = getDeclaringClass();
		String name = c.getName();
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

	public XClassCompiler getDeclaringClassCompiler() {
		return (XClassCompiler)getDeclaringClass();
	}
	
}
