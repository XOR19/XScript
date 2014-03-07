package xscript.compiler.classtypes;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrMethodGeneric;
import xscript.runtime.method.XMethod;

public class XMultibleTypeMethodGeneric extends XMultibleTypeGeneric {

	private XMethod m;
	private int id;
	
	public XMultibleTypeMethodGeneric(XMethod m, int id) {
		super(possibilidaded(m, id), baseClass(m));
		this.m = m;
		this.id = id;
	}

	private static XClass baseClass(XMethod m){
		return m.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass("xscript.lang.Object");
	}
	
	private static XVarType[] possibilidaded(XMethod m, int id){
		XGenericInfo info = m.getGenericInfo(id);
		if(info.isSuper()){
			return new XVarType[]{XVarType.getVarTypeFor(m.getDeclaringClass().getVirtualMachine().getClassProvider().getXClass("xscript.lang.Object"), new XVarType[0])};
		}
		XClassPtr[] cp = info.getTypeParams();
		if(cp.length==0){
			cp = new XClassPtr[1];
			cp[0] = new XClassPtrClass("xscript.lang.Object");
		}
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
	public boolean equals(Object other) {
		if(other instanceof XMultibleTypeMethodGeneric){
			XMultibleTypeMethodGeneric o = (XMultibleTypeMethodGeneric)other;
			return o.m == m && o.id==id;
		}
		return false;
	}
	
	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrMethodGeneric(m.getDeclaringClass().getName(), m.getRealName(), m.getParams(), m.getReturnTypePtr(), m.getGenericInfo(id).getName());
	}
	
}
