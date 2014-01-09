package xscript.runtime.method;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.threads.XMethodExecutor;

public class XCatchEntry {

	private int from;
	private int to;
	private XCatchType types[];
	
	public XCatchEntry(int from, int to, XCatchType types[]) {
		this.from = from;
		this.to = to;
		this.types = types;
	}

	public int getFrom(){
		return from;
	}
	
	public int getTo(){
		return to;
	}
	
	public boolean isIn(int programPointer) {
		return from<=programPointer && to>=programPointer;
	}
	
	public static class XCatchType{
		
		private int error;
		
		private XClassPtr type;
		
		public XCatchType(int error, XClassPtr type) {
			this.error = error;
			this.type = type;
		}
		
		public int getJumpPos() {
			return error;
		}

		public XClassPtr getType() {
			return type;
		}

		public String dump() {
			return type+"=>"+error;
		}
		
	}

	public XCatchInfo getCatchInfoFor(XGenericClass xClass, XVirtualMachine virtualMachine, XGenericClass genericClass, XMethodExecutor methodExecutor) {
		for(XCatchType type:types){
			if(xClass.canCastTo(type.type.getXClass(virtualMachine, genericClass, methodExecutor))){
				XCatchInfo ci = new XCatchInfo();
				ci.jumpPos = type.error;
				return ci;
			}
		}
		return null;
	}

	public XCatchType[] getTypes() {
		return types;
	}

	public String dump() {
		String out = from+"=>"+to+":"+types[0].dump();
		for(int i=1; i<types.length; i++){
			out += "\n"+from+"=>"+to+":"+types[i].dump();
		}
		return out;
	}


}
