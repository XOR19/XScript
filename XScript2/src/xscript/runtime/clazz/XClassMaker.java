package xscript.runtime.clazz;

import xscript.runtime.XRuntimeException;


public abstract class XClassMaker extends XPackage {

	public XClassMaker(String name) {
		super(name);
	}

	public abstract XClass makeClass();

	public abstract void onReplaced(XClass xClass);

	@Override
	public void addChild(XPackage child){
		throw new XRuntimeException("Can't add Package here");
	}
	
	@Override
	public XPackage getChild(String name){
		return this;
	}
	
}
