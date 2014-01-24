package xscript.compiler;

import java.util.List;

import xscript.compiler.classtypes.XVarType;
import xscript.compiler.dumyinstruction.XInstructionDumyInvokeConstructor;
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
import xscript.runtime.instruction.XInstructionOPop;
import xscript.runtime.instruction.XInstructionPop;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.instruction.XInstructionSetLocalField;
import xscript.runtime.method.XMethod;

public class XMethodCompiler extends XMethod {

	private XTreeMethodDecl xMethodDecl;
	
	private XImportHelper importHelper;
	
	private XCodeGen codeGen;
	
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

	public void compile(){
		if(!isConstructor() && (xMethodDecl.block==null || xscript.runtime.XModifier.isAbstract(modifier) || xscript.runtime.XModifier.isNative(modifier)))
			return;
		XStatementCompiler statementCompiler = new XStatementCompiler(null, null, this);
		xMethodDecl.accept(statementCompiler);
		codeGen = statementCompiler.getCodeGen();
		System.out.println(getName());
	}
	
	public void change(){
		if(codeGen!=null && isConstructor()){
			XClassCompiler c = (XClassCompiler) getDeclaringClass();
			if(!XModifier.isStatic(modifier) && c.getOuterMethod()!=null){
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
							for(int j=0; j<numVars; j++){
								codeGen.addInstruction(last+2+j, new XInstructionReadLocal(j+2), 0);
							}
							i+=numVars;
						}
						last = i;
					}
				}
				if(!selvInvoke){
					for(int j=0; j<numVars; j++){
						XField f = syntheticVars.get(j);
						if(f.getDeclaringClass()==getDeclaringClass()){
							codeGen.addInstruction(last+1+j*3, new XInstructionReadLocal(j+2), 0);
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
					var.id = j+2;
					codeGen.addVariable(var);
				}
			}
		}
	}
	
	public void gen(){
		if(codeGen!=null){
			codeGen.generateFinalCode();
			instructions = codeGen.getInstructions();
			lineEntries = codeGen.getLineEntries();
			catchEntries = codeGen.getCatchEntries();
			localEntries = codeGen.getLocalEntries();
			codeGen = null;
		}
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		Object[] o = new Object[args.length+1];
		o[0] = this;
		System.arraycopy(args, 0, o, 1, args.length);
		((XClassCompiler)getDeclaringClass()).compilerError(level, "method."+key, lineDesk, o);
	} 
	
	public void addClass(XClass c, XLineDesk line){
		if(childs.containsKey(c.getSimpleName())){
			compilerError(XMessageLevel.ERROR, "class.duplicated", line, c.getSimpleName());
		}else{
			addChild(c);
			classes.add(c);
		}
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
