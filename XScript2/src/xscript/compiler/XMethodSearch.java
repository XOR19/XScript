package xscript.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import xscript.runtime.XModifier;
import xscript.runtime.clazz.XClass;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.method.XMethod;

public class XMethodSearch {

	private XClass declaringClass;
	private boolean shouldBeStatic;
	private boolean specialInvoke;
	private String name;
	private XClassPtr[] types;
	private XClassPtr[] generics;
	private XClassPtr expectedReturn;
	private List<XMethod> posibleMethods = new ArrayList<XMethod>();
	
	public XMethodSearch(XClass declaringClass, boolean shouldBeStatic, String name, boolean specialInvoke, boolean lookIntoParents) {
		this.declaringClass = declaringClass;
		this.shouldBeStatic = shouldBeStatic;
		this.name = name;
		this.specialInvoke = specialInvoke;
		if(declaringClass!=null){
			addClassMethods(declaringClass, new ArrayList<XClass>(), lookIntoParents);
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
		posibleMethods.add(method);
	}
	
	public void applyGenerics(XClassPtr... generics) {
		if(this.generics!=null)
			throw new IllegalArgumentException();
		this.generics = generics;
		search();
	}

	public void applyTypes(XClassPtr... types) {
		if(this.types!=null)
			throw new IllegalArgumentException();
		this.types = types;
		search();
	}

	public void applyReturn(XClassPtr expectedReturn) {
		if(this.expectedReturn!=null)
			throw new IllegalArgumentException();
		this.expectedReturn = expectedReturn;
		search();
	}
	
	private void search(){
		ListIterator<XMethod> i = posibleMethods.listIterator();
		while(i.hasNext()){
			XMethod m = i.next();
			if(!isMethodOk(m))
				i.remove();
		}
	}
	
	private boolean isMethodOk(XMethod m){
		if(!m.getRealName().equals(name)){
			return false;
		}
		if(types!=null){
			int c = m.getParamCount();
			if(c!=types.length){
				if(XModifier.isVarargs(m.getModifier())){
					if(c-1>types.length){
						return false;
					}
				}else{
					return false;
				}
			}
		}
		if(generics!=null){
			int c = m.getGenericParams();
			if(c!=generics.length){
				return false;
			}
		}
		if(expectedReturn!=null){
			if(!m.getReturnTypePtr().equals(expectedReturn)){
				return false;
			}
		}
		return true;
	}
	
	public boolean isTypesSetted(){
		return types!=null;
	}
	
	public boolean isGenericsSetted(){
		return generics!=null;
	}
	
	public XMethod getMethod(){
		if(posibleMethods.size()==1)
			return posibleMethods.get(0);
		return null;
	}
	
	public boolean isEmpty(){
		return posibleMethods.isEmpty();
	}
	
	public boolean notIdentified(){
		return posibleMethods.size()>1;
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

	public XClass getDeclaringClass() {
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
		return declaringClass.getName()+"."+name+s;
	}
	
}
