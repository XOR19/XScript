package xscript.compiler.scopes;

import xscript.compiler.XGlobal;
import xscript.compiler.XJump;
import xscript.compiler.XVar;
import xscript.compiler.tree.XTree;


public class XModuleScope extends XBaseScope{

	public XModuleScope(){
		
	}
	
	private XModuleScope(XModuleScope thiz){
		super(thiz);
	}
	
	protected XVar create(XTree t, String name){
		return new XGlobal(t, name);
	}
	
	public XVar getOrCreate(String name){
		XVar var = locals.get(name);
		if(var!=null)
			return var;
		var = create(null, name);
		locals.put(name, var);
		return var;
	}
	
	public boolean getJump(String label, int type, XJump jump) {
		XScope scope = parent;
		while(scope!=null){
			jump.addPops(scope.pops);
			if(scope.getJump(label, type, jump)){
				return true;
			}
			if(scope == base){
				break;
			}
			scope = scope.parent;
		}
		return false;
	}
	
	public int getLocalsCount() {
		return 0;
	}
	
	public XScope lock() {
		return new XModuleScope(this);
	}
	
}
