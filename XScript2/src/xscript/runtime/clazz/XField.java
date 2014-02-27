package xscript.runtime.clazz;

import java.io.IOException;

import xscript.runtime.XAnnotation;
import xscript.runtime.XChecks;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.nativemethod.XNativeField;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XField extends XPackage {

	public static final int ENUMFIELD = 4096;
	
	public static final int STATICALLOWEDMODIFIFER = XModifier.FINAL | XModifier.PRIVATE | XModifier.PROTECTED | XModifier.PUBLIC | XModifier.STATIC | ENUMFIELD | XModifier.NATIVE;
	public static final int ALLOWEDMODIFIFER = XModifier.FINAL | XModifier.PRIVATE | XModifier.PROTECTED | XModifier.PUBLIC | XModifier.NATIVE;
	
	protected int modifier;
	protected XClassPtr type;
	protected XAnnotation[] annotations;
	protected int index;
	protected int enumID = -1;
	protected XNativeField nativeField;
	
	public XField(XClass declaringClass, XInputStream inputStream) throws IOException {
		super(inputStream.readUTF());
		parent = declaringClass;
		modifier = inputStream.readUnsignedShort();
		annotations = new XAnnotation[inputStream.readUnsignedByte()];
		for(int i=0; i<annotations.length; i++){
			annotations[i] = new XAnnotation(inputStream);
		}
		(type = XClassPtr.load(inputStream)).getXClass(declaringClass.getVirtualMachine());
		if(XModifier.isNative(modifier)){
			if(XModifier.isStatic(modifier)){
				XChecks.checkModifier(declaringClass, modifier, STATICALLOWEDMODIFIFER);
			}else{
				XChecks.checkModifier(declaringClass, modifier, ALLOWEDMODIFIFER);
			}
		}else{
			if(XModifier.isStatic(modifier)){
				index = declaringClass.getStaticFieldIndex(getSizeInObject());
				if((modifier & ENUMFIELD)!=0){
					enumID = declaringClass.getEnumIndex();
				}
				XChecks.checkModifier(declaringClass, modifier, STATICALLOWEDMODIFIFER);
			}else{
				index = declaringClass.getFieldIndex(getSizeInObject());
				XChecks.checkModifier(declaringClass, modifier, ALLOWEDMODIFIFER);
			}
		}
	}

	public XField(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations) {
		this(declaringClass, modifier, name, type, annotations, true);
	}
	
	protected XField(XClass declaringClass, int modifier, String name, XClassPtr type, XAnnotation[] annotations, boolean getIndex) {
		super(name);
		parent = declaringClass;
		this.modifier = modifier;
		this.annotations = annotations;
		this.type = type;
		if(getIndex){
			getIndex();
		}
	}

	public int getEnumID(){
		return enumID;
	}
	
	protected void getIndex(){
		XClass declaringClass = getDeclaringClass();
		if(XModifier.isNative(modifier)){
			if(XModifier.isStatic(modifier)){
				XChecks.checkModifier(declaringClass, modifier, STATICALLOWEDMODIFIFER);
			}else{
				XChecks.checkModifier(declaringClass, modifier, ALLOWEDMODIFIFER);
			}
		}else{
			if(XModifier.isStatic(modifier)){
				index = declaringClass.getStaticFieldIndex(getSizeInObject());
				if((modifier & ENUMFIELD)!=0){
					enumID = declaringClass.getEnumIndex();
				}
				XChecks.checkModifier(declaringClass, modifier, STATICALLOWEDMODIFIFER);
			}else{
				index = declaringClass.getFieldIndex(getSizeInObject());
				XChecks.checkModifier(declaringClass, modifier, ALLOWEDMODIFIFER);
			}
		}
	}
	
	@Override
	public void addChild(XPackage child) {
		throw new UnsupportedOperationException();
	}
	
	public XClass getDeclaringClass(){
		return (XClass)parent;
	}
	
	public int getModifier(){
		return modifier;
	}
	
	public XClassPtr getType() {
		return type;
	}
	
	public XGenericClass getType(XGenericClass genericDeclaringClass){
		return type.getXClass(getDeclaringClass().getVirtualMachine(), genericDeclaringClass, null);
	}
	
	public int getTypePrimitive(){
		XClass xClass = type.getXClass(getDeclaringClass().getVirtualMachine());
		if(xClass==null)
			return XPrimitive.OBJECT;
		return XPrimitive.getPrimitiveID(xClass);
	}
	
	public XAnnotation[] getAnnotations(){
		return annotations;
	}
	
	public int getSizeInObject(){
		return XPrimitive.getSize(getTypePrimitive());
	}
	
	public long get(XObject object) {
		return get(null, null, object);
	}
	
	public long get(XThread thread, XMethodExecutor methodExecutor, XObject object){
		if(XModifier.isNative(modifier)){
			return getDeclaringClass().virtualMachine.getNativeProvider().get(thread, methodExecutor, this, object);
		}
		int i;
		byte[] data;
		if(XModifier.isStatic(modifier)){
			data = getDeclaringClass().getStaticData();
			i = index;
		}else{
			XClass xClass = object.getXClass().getXClass();
			XClassTable classTable = getDeclaringClass().getClassTable(xClass);
			if(classTable==null)
				throw new XRuntimeException("Can't cast %s to %s", xClass, getDeclaringClass());
			data = object.getData();
			i = classTable.getFieldStartID()+index;
		}
		int size = getSizeInObject();
		long l = 0;
		for(int j=0; j<size; j++){
			l <<= 8;
			l |= data[i+j] & 0xFF;
		}
		return l;
	}

	public void set(XObject object, long value) {
		set(null, null, object, value);
	}
	
	public void set(XThread thread, XMethodExecutor methodExecutor, XObject object, long value) {
		if(XModifier.isFinal(modifier)){
			throw new XRuntimeException("Try to write final field %s", getName());
		}
		finalSet(thread, methodExecutor, object, value);
	}
	
	public void finalSet(XObject object, long value){
		finalSet(null, null, object, value);
	}
	
	public void finalSet(XThread thread, XMethodExecutor methodExecutor, XObject object, long value){
		if(XModifier.isNative(modifier)){
			getDeclaringClass().virtualMachine.getNativeProvider().set(thread, methodExecutor, this, object, value);
			return;
		}
		int i;
		byte[] data;
		if(XModifier.isStatic(modifier)){
			data = getDeclaringClass().getStaticData();
			i = index;
		}else{
			XClass xClass = object.getXClass().getXClass();
			XClassTable classTable = getDeclaringClass().getClassTable(xClass);
			if(classTable==null)
				throw new XRuntimeException("Can't cast %s to %s", xClass, getDeclaringClass());
			data = object.getData();
			i = classTable.getFieldStartID()+index;
			if(getTypePrimitive()==XPrimitive.OBJECT){
				XObject obj = getDeclaringClass().getVirtualMachine().getObjectProvider().getObject(value);
				if(obj!=null){
					XGenericClass type = getType(object.getXClass());
					XChecks.checkCast(obj.getXClass(), type);
				}
			}
		}
		int size = getSizeInObject();
		for(int j=size-1; j>=0; j--){
			data[i+j] = (byte) (value & 0xFF);
			value >>>= 8;
		}
	}

	public void save(XOutputStream outputStream) throws IOException {
		outputStream.writeUTF(getSimpleName());
		outputStream.writeShort(modifier);
		outputStream.writeByte(annotations.length);
		for(int i=0; i<annotations.length; i++){
			annotations[i].save(outputStream);
		}
		type.save(outputStream);
	}

	public String dump() {
		return XModifier.getSource(modifier)+type+" "+name+";";
	}

	public XNativeField getNativeField() {
		if(XModifier.isNative(modifier)){
			if(nativeField==null){
				nativeField = getDeclaringClass().getVirtualMachine().getNativeProvider().removeNativeField(getName());
			}
			return nativeField;
		}
		return null;
	}
	
	public void setNativeField(XNativeField nativeField){
		if(XModifier.isNative(modifier)){
			this.nativeField = nativeField;
		}
	}
	
}
