package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.runtime.XModifier;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.method.XMethod;

public class XMethodSearch {

	private XVirtualMachine vm;
	private XVarType declaringClass;
	private boolean lookIntoParents;
	private boolean shouldBeStatic;
	private boolean specialInvoke;
	private String name;
	private XVarType[] types;
	private XVarType[] generics;
	private XVarType expectedReturn;
	@SuppressWarnings("unchecked")
	private List<XCompilerMethod> posibleMethods[] = new List[]{new ArrayList<XCompilerMethod>(), new ArrayList<XCompilerMethod>(), new ArrayList<XCompilerMethod>(), new ArrayList<XCompilerMethod>(), new ArrayList<XCompilerMethod>()};
	
	public XMethodSearch(XVirtualMachine vm, XVarType varType, boolean shouldBeStatic, String name, boolean specialInvoke, boolean lookIntoParents) {
		this.vm = vm;
		this.declaringClass = varType;
		this.shouldBeStatic = shouldBeStatic;
		this.name = name;
		this.specialInvoke = specialInvoke;
		this.lookIntoParents = lookIntoParents;
		reset();
	}
	
	private void addClassMethods(XKnownType kt){
		XClass c = kt.getXClass();
		for(XMethod m:c.getMethods()){
			addMethod(m, kt);
		}
	}
	
	private void addMethod(XMethod method, XKnownType kt){
		XVarType generics[];
		if(kt instanceof XSingleType){
			generics = ((XSingleType) kt).generics;
		}else{
			generics = new XVarType[0];
		}
		if(this.generics!=null && method.getGenericParams() != this.generics.length)
			return;
		XCompilerMethod m = new XCompilerMethod();
		m.method = method;
		m.params = new XVarType[method.getParamCount()];
		for(int i=0; i<m.params.length; i++){
			m.params[i] = XVarType.getVarTypeFor(method.getParams()[i], vm, generics, this.generics);
		}
		m.mThrows = new XVarType[method.getThrows().length];
		for(int i=0; i<m.mThrows.length; i++){
			m.mThrows[i] = XVarType.getVarTypeFor(method.getThrows()[i], vm, generics, this.generics);
		}
		m.returnType =  XVarType.getVarTypeFor(method.getReturnTypePtr(), vm, generics, this.generics);
		posibleMethods[0].add(m);
	}
	
	private void reset(){
		if(declaringClass!=null){
			posibleMethods[0].clear();
			if(lookIntoParents){
				List<XKnownType> superClasses = declaringClass.getSuperClassesAndThis();
				for(XKnownType kt:superClasses){
					addClassMethods(kt);
				}
			}else if(declaringClass instanceof XKnownType){
				addClassMethods((XKnownType)declaringClass);
			}
			search();
		}
	}
	
	public void applyGenerics(XVarType... generics) {
		if(this.generics!=null)
			throw new IllegalArgumentException();
		this.generics = generics;
		reset();
	}

	public void applyTypes(XVarType... types) {
		if(this.types!=null)
			throw new IllegalArgumentException();
		this.types = types;
		search();
	}

	public void applyReturn(XVarType expectedReturn) {
		if(this.expectedReturn!=null)
			throw new IllegalArgumentException();
		this.expectedReturn = expectedReturn;
		search();
	}
	
	private void search(){
		for(int i=1; i<posibleMethods.length; i++){
			posibleMethods[i].clear();
		}
		ListIterator<XCompilerMethod> i = posibleMethods[0].listIterator();
		while(i.hasNext()){
			XCompilerMethod m = i.next();
			int ret = isMethodOk(m);
			if(ret<=0){
				i.remove();
			}else{
				posibleMethods[ret].add(m);
			}
		}
	}
	
	private int isMethodOk(XCompilerMethod m){
		if(!m.method.getRealName().equals(name)){
			return 0;
		}
		boolean casts = false;
		boolean varargs = false;
		if(types!=null){
			int c = m.params.length;
			if(c!=types.length){
				if(XModifier.isVarargs(m.method.getModifier())){
					varargs = true;
					if(c-1>=types.length){
						return 0;
					}
				}else{
					return 0;
				}
			}
			for(int i=0; i<(varargs?c-1:c); i++){
				if(!(types[i] instanceof XErroredType)){
					XVarType vt = m.params[i];
					if(!types[i].canCastTo(vt)){
						return 0;
					}
					if(!types[i].equals(vt)){
						casts = true;
					}
				}
			}
			if(varargs){
				XVarType cp = m.params[c-1];
				XClass cc = cp.getXClass();
				XVarType type;
				if(cc!=null && cc.getArrayPrimitive()==XPrimitive.OBJECT){
					type = cp.getGeneric(0);
				}else{
					type = XVarType.getVarTypeFor(new XClassPtrClass(XPrimitive.getName(cc.getArrayPrimitive())), vm, null, null);
				}
				for(int i=c-1; i<types.length; i++){
					if(!(types[i] instanceof XErroredType)){
						if(!types[i].canCastTo(type)){
							return 0;
						}
						if(!types[i].equals(type)){
							casts = true;
						}
					}
				}
			}
		}
		if(generics!=null){
			int c = m.method.getGenericParams();
			if(c!=generics.length){
				return 0;
			}
		}
		if(expectedReturn!=null){
			if(!m.returnType.canCastTo(expectedReturn)){
				return 0;
			}
		}
		return casts?varargs?4:2:varargs?3:1;
	}
	
	public boolean isTypesSetted(){
		return types!=null;
	}
	
	public boolean isGenericsSetted(){
		return generics!=null;
	}
	
	public XCompilerMethod getMethod(){
		for(int i=1; i<posibleMethods.length; i++){
			if(posibleMethods[i].size()>0){
				if(posibleMethods[i].size()==1)
					return posibleMethods[i].get(0);
				return null;
			}
		}
		return null;
	}
	
	public boolean isEmpty(){
		return posibleMethods[0].isEmpty();
	}
	
	public boolean notIdentified(){
		return posibleMethods[0].size()>1;
	}
	
	public boolean shouldBeStatic(){
		return shouldBeStatic;
	}

	public boolean specialInvoke(){
		return specialInvoke;
	}
	
	@Override
	public String toString() {
		String out = name;
		return out;
	}

	public XVarType getDeclaringClass() {
		return declaringClass;
	}

	public String getDesk() {
		String s = "";
		if(types==null){
			s += "(???)";
		}else{
			s += "(";
			if(types.length>0){
				s += types[0];
				for(int i=1; i<types.length; i++){
					s += ", "+types[i];
				}
			}
			s += ")";
		}
		if(expectedReturn==null){
			s += "???";
		}else{
			s += expectedReturn;
		}
		return declaringClass+"."+name+s;
	}

	public XVarType[] getTypes() {
		return types;
	}

	public XVarType getReturnType() {
		return expectedReturn;
	}
	
	public XVarType[] getGenerics() {
		return generics;
	}
	
}
