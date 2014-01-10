package xscript.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import xscript.compiler.message.XMessageLevel;
import xscript.compiler.tree.XTree.XImport;
import xscript.compiler.tree.XTree.XType;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;
import xscript.runtime.genericclass.XClassPtrGeneric;


public class XImportHelper {

	private XCompiler compiler;
	
	private XClassCompiler xClassCompiler;
	
	private List<String> directImports = new ArrayList<String>();
	
	private List<String> indirectImports = new ArrayList<String>();
	
	private List<String> staticDirectImports = new ArrayList<String>();
	
	private List<String> staticIndirectImports = new ArrayList<String>();
	
	public XImportHelper(XCompiler compiler, XClassCompiler xClassCompiler) {
		this.compiler = compiler;
		this.xClassCompiler = xClassCompiler;
		indirectImports.add(xClassCompiler.getParent().getName());
	}

	public void addImport(XClassCompiler xClassCompiler, XImport xImport) {
		if(xClassCompiler!=this.xClassCompiler)
			throw new AssertionError();
		if(xImport.indirect){
			if(xImport.staticImport){
				staticIndirectImports.add(xImport.iimport);
			}else{
				indirectImports.add(xImport.iimport);
			}
		}else{
			if(xImport.staticImport){
				staticDirectImports.add(xImport.iimport);
			}else{
				directImports.add(xImport.iimport);
			}
		}
	}

	private String getChilds(XClassCompiler c, String name){
		Collection<XPackage> col = c.getChildren();
		for(XPackage p:col){
			if(p instanceof XClassCompiler){
				XClassCompiler cc = (XClassCompiler) p;
				if(cc.getName().endsWith(name)){
					return cc.getName();
				}
				String n = getChilds(cc, name);
				if(n!=null)
					return n;
			}
		}
		return null;
	}
	
	private XClassPtr getGenericClass1(XClassCompiler xClassCompiler, XType type, XGenericInfo[] extra, boolean doError) {
		for(int i=1; i<9; i++){
			if(XPrimitive.getName(i).equals(type.name.name)){
				return new XClassPtrClass(type.name.name);
			}
		}
		try{
			xClassCompiler.getGenericID(type.name.name);
			return new XClassPtrClassGeneric(xClassCompiler.getName(), type.name.name);
		}catch(Exception e){}
		String name = null;
		if(xClassCompiler.getName().endsWith(type.name.name)){
			name = xClassCompiler.getName();
		}
		if(name==null){
			if(this.xClassCompiler.getName().endsWith(type.name.name)){
				name = this.xClassCompiler.getName();
			}
		}
		if(name==null){
			name = getChilds(this.xClassCompiler, type.name.name);
		}
		if(name==null){
			for(String s:directImports){
				if(s.endsWith(type.name.name)){
					try{
						xClassCompiler.getVirtualMachine().getClassProvider().getXClass(s);
						name = s;
						break;
					}catch(Exception e){}
				}
			}
		}
		if(name==null){
			for(String s:indirectImports){
				try{
					xClassCompiler.getVirtualMachine().getClassProvider().getXClass(s+"."+type.name.name);
					name = s+"."+type.name.name;
					break;
				}catch(Exception e){}
			}
		}
		if(name==null){
			try{
				xClassCompiler.getVirtualMachine().getClassProvider().getXClass(type.name.name);
				name = type.name.name;
			}catch(Exception e){
				if(doError){
					xClassCompiler.compilerError(XMessageLevel.ERROR, "classnotfound", type.line, type.name.name);
					return new XClassPtrErrored(type.name.name);
				}
				return null;
			}
		}
		if(type.typeParam==null){
			return new XClassPtrClass(name);
		}else{
			XClassPtr[] genericPtrs = new XClassPtr[type.typeParam.size()];
			for(int i=0; i<genericPtrs.length; i++){
				genericPtrs[i] = getGenericClass(xClassCompiler, type.typeParam.get(i), extra, true);
			}
			return new XClassPtrGeneric(name, genericPtrs);
		}
	}
	
	public XClassPtr getGenericClass(XClassCompiler xClassCompiler, XType type, XGenericInfo[] extra, boolean doError) {
		XClassPtr classPtr = getGenericClass1(xClassCompiler, type, extra, doError);
		if(classPtr==null)
			return null;
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
	
}
