package xscript.compiler;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.tree.XTree.XImport;
import xscript.compiler.tree.XTree.XType;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;


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
	}

	public void addImport(XClassCompiler xClassCompiler, XImport xImport) {
		if(xClassCompiler!=this.xClassCompiler)
			throw new AssertionError();
		if(xImport.indirect){
			if(xImport.staticImport){
				indirectImports.add(xImport.iimport);
			}else{
				staticIndirectImports.add(xImport.iimport);
			}
		}else{
			if(xImport.staticImport){
				directImports.add(xImport.iimport);
			}else{
				staticDirectImports.add(xImport.iimport);
			}
		}
	}

	public XClass getXClass(String name){
		return null;
	}

	public XClassPtr getGenericClass(XClassCompiler xClassCompiler2,
			XType type, XGenericInfo[] extra) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
