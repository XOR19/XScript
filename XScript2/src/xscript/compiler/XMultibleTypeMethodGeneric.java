package xscript.compiler;

import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.method.XMethod;

public class XMultibleTypeMethodGeneric extends XMultibleType {

	private XMethod m;
	private int id;
	
	public XMultibleTypeMethodGeneric(XMethod m, int id) {
		super(possibilidaded(m, id));
		this.m = m;
		this.id = id;
	}

	private static XVarType[] possibilidaded(XMethod m, int id){
		XGenericInfo info = m.getGenericInfo(id);
		if(info.isSuper()){
			return new XVarType[]{XVarType.getVarTypeFor(m.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass("xscript.lang.Object"), new XVarType[0])};
		}
		XClassPtr[] cp = info.getTypeParams();
		XVarType[] a = new XVarType[cp.length];
		for(int i=0; i<a.length; i++){
			a[i] = XVarType.getVarTypeFor(cp[i], m.getDeclaringClass().getVirtualMachine(), null, null);
		}
		return a;
	}

	@Override
	public String toString() {
		return m.getName()+":"+id;
	}

	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrMethodGeneric(m.getDeclaringClass().getName(), m.getName(), m.getGenericInfo(id).getName());
	}
	
}
