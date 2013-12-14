package xscript.runtime.clazz;

public abstract class XClassMaker extends XPackage {

	public XClassMaker(String name) {
		super(name);
	}

	public abstract XClass makeClass();

	public abstract void onReplaced(XClass xClass);

}
