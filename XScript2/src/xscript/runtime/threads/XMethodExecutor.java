package xscript.runtime.threads;

import java.util.ArrayList;
import java.util.List;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XPrimitive;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XCatchInfo;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;

public class XMethodExecutor implements XGenericMethodProvider {

	private XMethodExecutor parent;
	private XGenericClass declaringClass;
	private XMethod method;
	private XGenericClass[] generics;
	private int stackPointer;
	private int[] stack;
	private int objectStackPointer;
	private long[] objectStack;
	private long[] local;
	private long ret;
	private int programPointer;
	private int[] catchStackPointer;
	private int[] catchObjectStackPointer;
	private boolean topConstructor = false;
	private List<XClass> classes;
	
	public XMethodExecutor(XMethodExecutor parent, XMethod method, XGenericClass[] generics, long[] params) {
		this(parent, method, generics, params, null);
	}
	
	public XMethodExecutor(XMethodExecutor parent, XMethod method, XGenericClass[] generics, long[] params, List<XClass> classes) {
		this.parent = parent;
		this.method = method;
		if(classes==null && method.isConstructor() && !XModifier.isStatic(method.getModifier())){
			topConstructor = true;
			classes = new ArrayList<XClass>();
			classes.add(method.getDeclaringClass());
		}
		this.classes = classes;
		if(XModifier.isNative(method.getModifier()))
			throw new XRuntimeException("Can't run native method %s", method);
		this.generics = generics;
		if(generics==null){
			if(method.getGenericParams()!=0)
				throw new XRuntimeException("Can't create a generic method %s without generic params, need %s generic params", method, method.getGenericParams());
		}else if(generics.length!=method.getGenericParams()){
			throw new XRuntimeException("Can't create a generic method %s with %s generic params, need %s generic params", method, generics.length, method.getGenericParams());
		}
		int pl = params.length;
		stack = new int[method.getMaxStackSize()];
		objectStack = new long[method.getMaxObjectStackSize()];
		local = new long[method.getMaxLocalSize()];
		if(!XModifier.isStatic(method.getModifier())){
			if(method.getDeclaringClass().getVirtualMachine().getObjectProvider().getObject(params[0])==null)
				throw new XRuntimeException("Null Pointer");
			pl--;
		}
		for(int i=0; i<params.length; i++){
			local[i] = params[i];
		}
		if(pl!=method.getParamCount()){
			throw new XRuntimeException("Wrong number of arguments got %s but need %s", method.getParamCount(), pl);
		}
		catchStackPointer = new int[method.getExceptionHanles()];
		catchObjectStackPointer = new int[method.getExceptionHanles()];
	}

	public List<XClass> getInitializizedClasses(){
		return classes;
	}
	
	@Override
	public XGenericClass getGeneric(int genericID) {
		return generics[genericID];
	}

	public XGenericClass getDeclaringClass(){
		return declaringClass;
	}
	
	public long getThis(){
		if(XModifier.isStatic(method.getModifier()))
			return 0;
		return local[0];
	}
	
	public long[] getReturn(){
		long[] l = new long[2];
		l[0] = ret;
		l[1] = method.getReturnTypePrimitive();
		return l;
	}
	
	public int iPop(){
		if(stackPointer==0)
			throw new XRuntimeException("Stack underflow");
		return stack[--stackPointer];
	}
	
	public long oPop(){
		if(objectStackPointer==0)
			throw new XRuntimeException("Stack underflow");
		return objectStack[--objectStackPointer];
	}
	
	public long pop(int primitiveID) {
		if(primitiveID==XPrimitive.OBJECT){
			return oPop();
		}else if(primitiveID==XPrimitive.DOUBLE || primitiveID==XPrimitive.LONG){
			return lPop();
		}
		return iPop();
	}
	
	public long lPop(){
		long l = iPop();
		long l2 = iPop();
		return l2<<32 | l;
	}
	
	public boolean zPop() {
		 return iPop()!=0;
	}
	
	public float fPop() {
		return Float.intBitsToFloat(iPop());
	}
	
	public double dPop() {
		return Double.longBitsToDouble(lPop());
	}
	
	public int iRead(int pos){
		pos = stackPointer-pos-1;
		if(pos<0)
			throw new XRuntimeException("Stack underflow");
		if(pos>=stackPointer)
			throw new XRuntimeException("Stack overflow");
		return stack[pos];
	}
	
	public long oRead(int pos){
		pos = objectStackPointer-pos-1;
		if(pos<0)
			throw new XRuntimeException("Stack underflow");
		if(pos>=objectStackPointer)
			throw new XRuntimeException("Stack overflow");
		return objectStack[pos];
	}
	
	public long read(int primitiveID, int pos) {
		if(primitiveID==XPrimitive.OBJECT){
			return oRead(pos);
		}else if(primitiveID==XPrimitive.DOUBLE || primitiveID==XPrimitive.LONG){
			return lRead(pos);
		}
		return iRead(pos);
	}
	
	public long lRead(int pos){
		long l = iRead(pos+1);
		long l2 = iRead(pos);
		return l2<<32 | l;
	}
	
	public boolean zRead(int pos) {
		 return iRead(pos)!=0;
	}
	
	public float fRead(int pos) {
		return Float.intBitsToFloat(iRead(pos));
	}
	
	public double dRead(int pos) {
		return Double.longBitsToDouble(lRead(pos));
	}
	
	public void iPush(int value) {
		if(stackPointer==stack.length)
			throw new XRuntimeException("Stack overflow");
		stack[stackPointer++] = value;
	}
	
	public void oPush(long value) {
		if(objectStackPointer==objectStack.length)
			throw new XRuntimeException("Stack overflow");
		objectStack[objectStackPointer++] = value;
	}

	public void push(long value, int primitiveID) {
		if(primitiveID==XPrimitive.OBJECT){
			oPush(value);
		}else if(primitiveID==XPrimitive.DOUBLE || primitiveID==XPrimitive.LONG){
			lPush(value);
		}else{
			iPush((int)value);
		}
	}
	
	public void lPush(long value) {
		long l = value&0xFFFFFFFF;
		long l2 = value>>32;
		iPush((int) l2);
		iPush((int) l);
	}
	
	public void zPush(boolean value) {
		iPush(value?-1:0);
	}
	
	public void fPush(float value) {
		iPush(Float.floatToIntBits(value));
	}
	
	public void dPush(double value) {
		lPush(Double.doubleToLongBits(value));
	}

	public long getLocal(int local) {
		if(local<0 || local>=this.local.length)
			throw new XRuntimeException("Local out of bounds %s", local);
		return this.local[local];
	}

	public void setLocal(int local, long value) {
		if(local<0 || local>=this.local.length)
			throw new XRuntimeException("Local out of bounds %s", local);
		this.local[local] = value;
	}

	public void setReturn(long value){
		ret = value;
	}
	
	public void setProgramPointer(int programPointer){
		this.programPointer = programPointer;
	}
	
	public XInstruction getNextInstruction(){
		return method.getInstruction(programPointer++);
	}
	
	public void markVisible(){
		if(parent!=null){
			parent.markVisible();
		}
		if(method.getReturnTypePrimitive()==XPrimitive.OBJECT){
			XObject obj = method.getDeclaringClass().getVirtualMachine().getObjectProvider().getObject(ret);
			if(obj!=null)
				obj.markVisible();
		}
		for(int i=0; i<objectStackPointer; i++){
			XObject obj = method.getDeclaringClass().getVirtualMachine().getObjectProvider().getObject(objectStack[i]);
			if(obj!=null)
				obj.markVisible();
		}
	}

	public XMethodExecutor getParent() {
		return parent;
	}

	@Override
	public XMethod getMethod() {
		return method;
	}

	public boolean jumpToExceptionHandlePoint(XGenericClass xClass, long exception) {
		XCatchInfo ci = method.getExceptionHandlePoint(programPointer, xClass, declaringClass, this);
		if(ci==null){
			return false;
		}
		programPointer = ci.jumpPos;
		stackPointer = catchStackPointer[ci.index];
		objectStackPointer = catchObjectStackPointer[ci.index];
		objectStack[objectStackPointer++] = exception;
		return true;
	}

	public XClassPtr getLocalType(int local) {
		return method.getLocalType(programPointer-1, local);
	}
	
	public void saveStackSize(int index) {
		catchStackPointer[index] = stackPointer;
		catchObjectStackPointer[index] = objectStackPointer;
	}
	
	public int getLine(){
		return method.getLine(programPointer-1);
	}
	
}
