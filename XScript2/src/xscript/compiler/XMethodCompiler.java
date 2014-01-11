package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XType;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.instruction.XInstructionInvokeSpecial;
import xscript.runtime.instruction.XInstructionReadLocal;
import xscript.runtime.method.XMethod;

public class XMethodCompiler extends XMethod {

	private XMethodDecl xMethodDecl;
	
	private List<XClass> classes;
	
	private XImportHelper importHelper;
	
	public XMethodCompiler(XClass declaringClass, int modifier, String name,
			XClassPtr returnType, xscript.runtime.XAnnotation[] annotations,
			XClassPtr[] params, xscript.runtime.XAnnotation[][] paramAnnotations,
			XClassPtr[] mThrows, XGenericInfo[] genericInfos, XMethodDecl xMethodDecl, 
			XImportHelper importHelper) {
		super(declaringClass, modifier, name, returnType, annotations, params,
				paramAnnotations, mThrows, genericInfos);
		this.xMethodDecl = xMethodDecl;
		this.importHelper = importHelper;
	}

	public void gen(){
		if(!isConstructor() && (xMethodDecl.block==null || xscript.runtime.XModifier.isAbstract(modifier) || xscript.runtime.XModifier.isNative(modifier)))
			return;
		classes = new ArrayList<XClass>();
		XCodeGen codeGen = null;
		if(isConstructor() && xMethodDecl==null){
			codeGen = new XCodeGen();
			for(XClassPtr superClasses:getDeclaringClass().getSuperClasses()){
				XMethodSearch search = new XMethodSearch(superClasses.getXClassNonNull(getDeclaringClass().getVirtualMachine()), false, "<init>", true, false);
				search.applyTypes(new XVarType[0]);
				search.applyReturn(XVarType.getVarTypeFor(new XClassPtrClass("void"), getDeclaringClass().getVirtualMachine()));
				XMethod m = search.getMethod();
				if(m==null){
					if(search.isEmpty()){
						compilerError(XMessageLevel.ERROR, "nomethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
					}else{
						compilerError(XMessageLevel.ERROR, "toomanymethodfor", new XLineDesk(0, 0, 0, 0), search.getDesk());
					}
				}else{
					codeGen.addInstruction(new XInstructionReadLocal(0), 0);
					codeGen.addInstruction(new XInstructionInvokeSpecial(m, new XClassPtr[0]), 0);
				}
			}
		}else{
			XStatementCompiler statementCompiler = new XStatementCompiler(null, null, this);
			xMethodDecl.accept(statementCompiler);
			codeGen = statementCompiler.getCodeGen();
		}
		classes = null;
		System.out.println(getName());
		codeGen.generateFinalCode();
		instructions = codeGen.getInstructions();
		lineEntries = codeGen.getLineEntries();
		catchEntries = codeGen.getCatchEntries();
		localEntries = codeGen.getLocalEntries();
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
			childs.put(c.getSimpleName(), c);
			classes.add(c);
		}
	}
	
	public XClassPtr getGenericClass(XType type, boolean doError){
		return importHelper.getGenericClass((XClassCompiler)getDeclaringClass(), type, genericInfos, doError);
	}

	public XImportHelper getImportHelper() {
		return importHelper;
	}

	public XClassPtr getGenericReturnType() {
		return returnType;
	}

	public XClassPtr getDeclaringClassGen() {
		XClass c = getDeclaringClass();
		String name = c.getName();
		if(c.getGenericParams()>0){
			XClassPtr generics[] = new XClassPtr[c.getGenericParams()];
			for(int i=0; i<generics.length; i++){
				generics[i] = new XClassPtrClassGeneric(name, c.getGenericInfo(i).getName());
			}
			return new XClassPtrGeneric(name, generics);
		}
		return new XClassPtrClass(name);
	}
	
}
