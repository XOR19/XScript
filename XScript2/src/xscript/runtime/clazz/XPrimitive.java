package xscript.runtime.clazz;

import java.io.IOException;

import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;


public class XPrimitive extends XClass {

	public static final int OBJECT = 0;
	public static final int BOOL = 1;
	public static final int BYTE = 2;
	public static final int CHAR = 3;
	public static final int SHORT = 4;
	public static final int INT = 5;
	public static final int LONG = 6;
	public static final int FLOAT = 7;
	public static final int DOUBLE = 8;
	public static final int VOID = 9;
	
	public static final String OBJECT_NAME = "Object";
	public static final String BOOL_NAME = "bool";
	public static final String BYTE_NAME = "byte";
	public static final String CHAR_NAME = "char";
	public static final String SHORT_NAME = "short";
	public static final String INT_NAME = "int";
	public static final String LONG_NAME = "long";
	public static final String FLOAT_NAME = "float";
	public static final String DOUBLE_NAME = "double";
	public static final String VOID_NAME = "void";
	
	private static final String[] NAME = {OBJECT_NAME, BOOL_NAME, BYTE_NAME, CHAR_NAME, SHORT_NAME, INT_NAME, LONG_NAME, FLOAT_NAME, DOUBLE_NAME, VOID_NAME};
	private static final String[] WRAPPER = {"", "Bool", "Byte", "Char", "Short", "Int", "Long", "Float", "Double", "Void"};
	
	private static final int[] SIZE = {4, 1, 1, 2, 2, 4, 8, 4, 8, 0};
	
	private int primitiveID;
	
	protected XPrimitive(XVirtualMachine virtualMachine, int primitiveID, XPackage p) {
		super(virtualMachine, NAME[primitiveID], p);
		this.primitiveID = primitiveID;
		state = XClass.STATE_RUNNABLE;
		modifier = XModifier.FINAL | XModifier.PUBLIC;
	}

	@Override
	public void save(XOutputStream outputStream) throws IOException {
		throw new XRuntimeException("Can't save Primitive Classes");
	}
	
	@Override
	public int getGenericParams() {
		return 0;
	}
	
	@Override
	public void addChild(XPackage child) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public XClassTable getClassTable(XClass xClass) {
		return null;
	}

	@Override
	public XField getField(String name) {
		return null;
	}

	@Override
	public XField getFieldAndParents(String name) {
		return null;
	}

	@Override
	public XMethod getMethod(String name) {
		return null;
	}

	@Override
	public void markVisible(){
		
	}
	
	public void load(XInputStreamSave dis) throws IOException {
		
	}
	
	public void save(XOutputStreamSave dos) throws IOException {
		
	}
	
	@Override
	public boolean canCastTo(XClass xClass) {
		return xClass == this;
	}

	public static int getPrimitiveID(XClass xClass){
		if(xClass instanceof XPrimitive){
			return ((XPrimitive) xClass).primitiveID;
		}
		return 0;
	}
	
	public static int getSize(XClass xClass) {
		return SIZE[getPrimitiveID(xClass)];
	}

	public static String getWrapper(int primitiveID){
		return WRAPPER[primitiveID];
	}

	public static String getName(int primitiveID) {
		return NAME[primitiveID];
	}

	public static int getSize(int primitiveID) {
		return SIZE[primitiveID];
	}

	public static XGenericClass getXClass(XVirtualMachine vm, long l, int i) {
		switch (i) {
		case OBJECT:
			XObject obj = vm.getObjectProvider().getObject(l);
			if(obj==null)
				return null;
			return obj.getXClass();
		case BOOL:
			return new XGenericClass(vm.getClassProvider().BOOL);
		case BYTE:
			return new XGenericClass(vm.getClassProvider().BYTE);
		case CHAR:
			return new XGenericClass(vm.getClassProvider().CHAR);
		case SHORT:
			return new XGenericClass(vm.getClassProvider().SHORT);
		case INT:
			return new XGenericClass(vm.getClassProvider().INT);
		case LONG:
			return new XGenericClass(vm.getClassProvider().LONG);
		case FLOAT:
			return new XGenericClass(vm.getClassProvider().FLOAT);
		case DOUBLE:
			return new XGenericClass(vm.getClassProvider().DOUBLE);
		case VOID:
			return new XGenericClass(vm.getClassProvider().VOID);
		}
		throw new XRuntimeException("Unknown primitiveID %s", i);
	}

	public static int getPrimitiveID(String param) {
		for(int i=1; i<NAME.length; i++){
			if(NAME[i].equals(param)){
				return i;
			}
		}
		return -1;
	}
	
}
