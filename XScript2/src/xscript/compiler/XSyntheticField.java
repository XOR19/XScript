package xscript.compiler;

import xscript.runtime.XAnnotation;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPackage;
import xscript.runtime.genericclass.XClassPtr;

public class XSyntheticField extends XFieldCompiler {

private String rName;
	
	private int i = 1;
	
	public XSyntheticField(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations) {
		super(declaringClass, modifier, "$"+name, type, annotations, false);
		rName = name;
	}

	@Override
	protected void getIndex() {
		super.getIndex();
	}

	public void checkName(XPackage p){
		while(p.getChild(this.name)!=null){
			this.name = "$"+rName+"_"+i;
			i++;
		}
	}
	
	public void inc(){
		parent.remove(this.name);
		checkName(parent);
		parent.addChild(this);
	}

}
