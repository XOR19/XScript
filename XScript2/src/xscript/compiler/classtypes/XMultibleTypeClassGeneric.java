package xscript.compiler.classtypes;

import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XGenericInfo;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XClassPtrClass;
import xscript.runtime.genericclass.XClassPtrClassGeneric;

public class XMultibleTypeClassGeneric extends XMultibleTypeGeneric {

	private XClass c;
	private int id;
	
	public XMultibleTypeClassGeneric(XClass c, int id) {
		super(possibilidaded(c, id));
		this.c = c;
		this.id = id;
	}

	private static XVarType[] possibilidaded(XClass c, int id){
		XGenericInfo info = c.getGenericInfo(id);
		if(info.isSuper()){
			return new XVarType[]{XVarType.getVarTypeFor(c.getVirtualMachine().getClassProvider().getXClass("xscript.lang.Object"), new XVarType[0])};
		}
		XClassPtr[] cp = info.getTypeParams();
		if(cp.length==0){
			cp = new XClassPtr[1];
			cp[0] = new XClassPtrClass("xscript.lang.Object");
		}
		XVarType[] a = new XVarType[cp.length];
		for(int i=0; i<a.length; i++){
			a[i] = XVarType.getVarTypeFor(cp[i], c.getVirtualMachine(), null, null);
		}
		return a;
	}
	
	@Override
	public String toString() {
		return c.getName()+":"+id;
	}

	@Override
	public XClassPtr getXClassPtr() {
		return new XClassPtrClassGeneric(c.getName(), c.getGenericInfo(id).getName());
	}
	
}
