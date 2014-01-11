package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.runtime.XModifier;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrGeneric;
import xscript.runtime.method.XMethod;

public class XMethodSearch {

	private XClass[] declaringClass;
	private boolean shouldBeStatic;
	private boolean specialInvoke;
	private String name;
	private XVarType[] types;
	private XClassPtr[] generics;
	private XVarType expectedReturn;
	@SuppressWarnings("unchecked")
	private List<XMethod> posibleMethods[] = new List[]{new ArrayList<XMethod>(), new ArrayList<XMethod>(), new ArrayList<XMethod>(), new ArrayList<XMethod>(), new ArrayList<XMethod>()};
	
	public XMethodSearch(XClass declaringClass, boolean shouldBeStatic, String name, boolean specialInvoke, boolean lookIntoParents) {
		this.declaringClass = new XClass[]{declaringClass};
		this.shouldBeStatic = shouldBeStatic;
		this.name = name;
		this.specialInvoke = specialInvoke;
		if(declaringClass!=null){
			addClassMethods(declaringClass, new ArrayList<XClass>(), lookIntoParents);
			search();
		}
	}

	public XMethodSearch(XClass[] declaringClass, boolean shouldBeStatic, String name, boolean specialInvoke, boolean lookIntoParents) {
		this.declaringClass = declaringClass;
		this.shouldBeStatic = shouldBeStatic;
		this.name = name;
		this.specialInvoke = specialInvoke;
		if(declaringClass!=null){
			for(int i=0; i<declaringClass.length; i++)
				addClassMethods(declaringClass[i], new ArrayList<XClass>(), lookIntoParents);
			search();
		}
	}
	
	
	private void addClassMethods(XClass c, List<XClass> classesAlreadyDone, boolean lookIntoParents){
		if(!classesAlreadyDone.contains(c)){
			classesAlreadyDone.add(c);
			for(XMethod m:c.getMethods()){
				addMethod(m);
			}
			if(lookIntoParents){
				for(XClassPtr pc:c.getSuperClasses()){
					addClassMethods(pc.getXClassNonNull(c.getVirtualMachine()), classesAlreadyDone, true);
				}
			}
		}
	}
	
	private void addMethod(XMethod method){
		posibleMethods[0].add(method);
	}
	
	public void applyGenerics(XClassPtr... generics) {
		if(this.generics!=null)
			throw new IllegalArgumentException();
		this.generics = generics;
		search();
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
		ListIterator<XMethod> i = posibleMethods[0].listIterator();
		while(i.hasNext()){
			XMethod m = i.next();
			int ret = isMethodOk(m);
			if(ret<=0){
				i.remove();
			}else{
				posibleMethods[ret].add(m);
			}
		}
	}
	
	private int isMethodOk(XMethod m){
		if(!m.getRealName().equals(name)){
			return 0;
		}
		boolean casts = false;
		boolean varargs = false;
		if(types!=null){
			int c = m.getParamCount();
			if(c!=types.length){
				if(XModifier.isVarargs(m.getModifier())){
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
					XVarType vt = XVarType.getVarTypeFor(m.getParams()[i], m.getDeclaringClass().getVirtualMachine());
					if(!types[i].canCastTo(vt)){
						return 0;
					}
					if(!types[i].equals(vt)){
						casts = true;
					}
				}
			}
			if(varargs){
				XClassPtr pr = m.getParams()[c-1];
				XClass cc = pr.getXClassNonNull(m.getDeclaringClass().getVirtualMachine());
				XVarType type;
				if(cc.getArrayPrimitive()==XPrimitive.OBJECT){
					type = XVarType.getVarTypeFor(((XClassPtrGeneric)pr).getGeneric(0), m.getDeclaringClass().getVirtualMachine());
				}else{
					type = XVarType.getVarTypeFor(new XClassPtrClass(XPrimitive.getName(cc.getArrayPrimitive())), m.getDeclaringClass().getVirtualMachine());
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
			int c = m.getGenericParams();
			if(c!=generics.length){
				return 0;
			}
		}
		if(expectedReturn!=null){
			if(!XVarType.getVarTypeFor(m.getReturnTypePtr(), m.getDeclaringClass().getVirtualMachine()).canCastTo(expectedReturn)){
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
	
	public XMethod getMethod(){
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

	public XClassPtr[] getGenerics() {
		if(generics==null){
			XMethod m = getMethod();
			if(m!=null){
				XClassPtr[] generics = new XClassPtr[m.getGenericParams()];
				
				return generics;
			}
		}
		return generics;
	}

	public XClass[] getDeclaringClass() {
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
		return declaringClass[0].getName()+"."+name+s;
	}

	public XVarType[] getTypes() {
		return types;
	}
	
}
