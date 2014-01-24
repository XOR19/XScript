package xscript.compiler;

import xscript.compiler.token.XLineDesk;
import xscript.runtime.XAnnotation;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.genericclass.XClassPtr;

public class XFieldCompiler extends XField {
	
	private int reads;
	
	private int writes;
	
	private XLineDesk declLine;
	
	public XFieldCompiler(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations, XLineDesk declLine) {
		super(declaringClass, modifier, name, type, annotations, true);
		this.declLine = declLine;
	}
	
	protected XFieldCompiler(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations, XLineDesk declLine, boolean getIndex) {
		super(declaringClass, modifier, name, type, annotations, getIndex);
		this.declLine = declLine;
	}

	public void incReads() {
		reads++;
	}
	
	public int getReads(){
		return reads;
	}
	
	public void incWrites() {
		writes++;
	}
	
	public int getWrites(){
		return writes;
	}
	
	public XLineDesk getDeclLine(){
		return declLine;
	}
	
}
