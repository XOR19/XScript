package xscript.compiler;

import xscript.runtime.XAnnotation;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XField;
import xscript.runtime.genericclass.XClassPtr;

public class XFieldCompiler extends XField {
	
	private int reads;
	
	private int writes;
	
	public XFieldCompiler(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations) {
		super(declaringClass, modifier, name, type, annotations, true);
	}
	
	protected XFieldCompiler(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations, boolean getIndex) {
		super(declaringClass, modifier, name, type, annotations, getIndex);
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
	
}
