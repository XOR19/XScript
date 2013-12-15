package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XType;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.instruction.XInstruction;
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
		if(xMethodDecl.block==null || xscript.runtime.XModifier.isAbstract(modifier) || xscript.runtime.XModifier.isNative(modifier))
			return;
		classes = new ArrayList<XClass>();
		XStatementCompiler statementCompiler = new XStatementCompiler(null, null, this);
		xMethodDecl.block.accept(statementCompiler);
		classes = null;
		List<XInstruction> lInstructions = statementCompiler.getInstructions();
		ListIterator<XInstruction> i = lInstructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumyDelete){
				for(XInstruction inst2:lInstructions){
					if(inst2 instanceof XInstructionDumy){
						((XInstructionDumy) inst2).deleteInstruction(this, lInstructions, inst);
					}
				}
				i.remove();
			}
		}
		i = lInstructions.listIterator();
		while(i.hasNext()){
			XInstruction inst = i.next();
			if(inst instanceof XInstructionDumy){
				i.set(((XInstructionDumy) inst).replaceWith(this, lInstructions));
			}
		}
		instructions = lInstructions.toArray(new XInstruction[lInstructions.size()]);
	}
	
	protected void compilerError(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		((XClassCompiler)getDeclaringClass()).compilerError(level, "method."+key, lineDesk, this, args);
	} 
	
	public void addClass(XClass c, XLineDesk line){
		if(childs.containsKey(c.getSimpleName())){
			compilerError(XMessageLevel.ERROR, "class.duplicated", line, c.getSimpleName());
		}else{
			childs.put(c.getSimpleName(), c);
			classes.add(c);
		}
	}
	
	public XClassPtr getGenericClass(XType type){
		return importHelper.getGenericClass((XClassCompiler)getDeclaringClass(), type, genericInfos);
	}

	public XImportHelper getImportHelper() {
		return importHelper;
	}

	public XClassPtr getGenericReturnType() {
		return returnType;
	}

	public XMethodDecl getMethodDecl() {
		return xMethodDecl;
	}
	
}
