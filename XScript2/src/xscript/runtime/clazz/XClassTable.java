package xscript.runtime.clazz;

import java.util.Arrays;

import xscript.runtime.genericclass.XClassPtr;

public class XClassTable {

	private final XClass xClass;
	private int fieldStartID;
	private int[] methodID;
	private final XClassPtr[] generics;
	
	protected XClassTable(XClass xClass, XClassPtr[] generics){
		this.xClass = xClass;
		this.generics = generics;
	}
	
	public void setFieldStartID(int id){
		this.fieldStartID = id;
	}
	
	public void setMethodID(int[] ids){
		this.methodID = ids;
	}
	
	public XClass getXClass(){
		return xClass;
	}
	
	public int getFieldStartID(){
		return fieldStartID;
	}
	
	public int getMethodID(int method){
		return methodID[method];
	}

	public XClassPtr getGenericPtr(int genericID) {
		return generics[genericID];
	}

	@Override
	public String toString() {
		return "XClass:"+xClass+"->"+Arrays.toString(generics);
	}
	
	
	
}
