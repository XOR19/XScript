package xscript.runtime.clazz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import xscript.runtime.XAnnotation;
import xscript.runtime.XChecks;
import xscript.runtime.XModifier;
import xscript.runtime.XRuntimeException;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.method.XMethod;
import xscript.runtime.object.XObject;

public class XClass extends XPackage{

	public final int OUTERMODIFIER = XModifier.PUBLIC | XModifier.FINAL | XModifier.ABSTRACT;
	public final int INNERMODIFIER = XModifier.PUBLIC | XModifier.PRIVATE | XModifier.PROTECTED 
			| XModifier.FINAL | XModifier.ABSTRACT | XModifier.STATIC;
	
	public static final int STATE_CREATED=0;
	public static final int STATE_LOADING=1;
	public static final int STATE_LOADED=2;
	public static final int STATE_POST_LOADING=3;
	public static final int STATE_RUNNABLE=4;
	public static final int STATE_ERRORED=5;
	
	protected XVirtualMachine virtualMachine;
	
	protected int modifier;
	protected XClassPtr[] superClasses;
	protected boolean[] canBeDiamondExtender;
	
	protected XField[] fields;
	protected XMethod[] methods;
	
	protected XAnnotation[] annotations;
	
	protected XGenericInfo[] genericInfos;
	
	protected int classIndex;
	protected XClassTable[] classTable;
	protected XMethod[] virtualMethods;
	
	protected int staticFieldCount;
	protected int fieldCount;
	protected int methodCount;
	protected int enumCount;
	
	private byte[] staticData;
	
	protected int state;
	
	protected long classObject;
	
	public XClass(XVirtualMachine virtualMachine, String name, XPackage p) {
		super(name);
		state = STATE_CREATED;
		this.virtualMachine = virtualMachine;
		isArray = (p.getName()+"."+name).startsWith("xscript.lang.Array");
		if(isArray()){
			int primitiveID = XPrimitive.OBJECT;
			for(int i=0; i<9; i++){
				if(getSimpleName().equals("Array"+XPrimitive.getWrapper(i))){
					primitiveID = i;
					break;
				}
			}
			primitive = primitiveID;
			objectArray = primitiveID==XPrimitive.OBJECT;
			elementSize = XPrimitive.getSize(primitiveID);
		}else{
			objectArray = false;
			elementSize = 0;
			primitive = 0;
		}
	}
	
	@Override
	public void addChild(XPackage child) {
		if(getClass()==XClass.class)
			throw new UnsupportedOperationException();
		super.addChild(child);
	}
	
	public XClassPtr[] getSuperClasses(){
		return superClasses;
	}
	
	public XVirtualMachine getVirtualMachine(){
		return virtualMachine;
	}
	
	public int getModifier(){
		return modifier;
	}
	
	public XAnnotation[] getAnnotations(){
		return annotations;
	}
	
	public XClass getOuterClass(){
		return parent instanceof XClass?((XClass)parent):null;
	}
	
	public XPackage getPackage() {
		return parent instanceof XClass?((XClass)parent).getPackage():parent;
	}
	
	public XMethod getOuterMethod() {
		return parent instanceof XMethod?((XMethod)parent):null;
	}
	
	public XClass getOuterClassOrMethodDeclClass(){
		return parent instanceof XClass?((XClass)parent):parent instanceof XMethod?((XMethod)parent).getDeclaringClass():null;
	}
	
	public boolean isObjectClass(){
		return superClasses.length==0;
	}
	
	public boolean isAnnotation(){
		return superClasses.length!=0 && superClasses[0].getXClass(virtualMachine).getName().equals("xscript.lang.Annotation");
	}
	
	public boolean isEnum(){
		return isDirectEnum() || isIndirectEnum();
	}
	
	public boolean isDirectEnum(){
		return superClasses.length!=0 && superClasses[0].getXClass(virtualMachine).getName().equals("xscript.lang.Enum");
	}
	
	public boolean isIndirectEnum(){
		return superClasses.length==1 && superClasses[0].getXClass(virtualMachine).isDirectEnum();
	}

	public XClassTable getClassTable(XClass xClass){
		if(XPrimitive.getPrimitiveID(xClass)!=XPrimitive.OBJECT)
			return null;
		if(isObjectClass()){
			return classTable[0];
		}
		int ci = xClass.classIndex;
		if(ci<classTable.length){
			XClassTable ct = classTable[ci];
			if(ct!=null && ct.getXClass()==xClass)
				return ct;
		}
		return null;
	}
	
	public boolean canCastTo(XClass xClass){
		if(getState()!=STATE_RUNNABLE)
			return canCastTo2(this, xClass);
		return xClass.getClassTable(this)!=null;
	}
	
	public XGenericClass getGeneric(XGenericClass genericClass, int genericID) {
		if(genericClass.getXClass()==this){
			return genericClass.getGeneric(genericID);
		}
		XClassTable classTable = genericClass.getXClass().getClassTable(this);
		if(classTable==null)
			throw new XRuntimeException("Can't get generic %s of class %s in class %s", "", this, genericClass);
		return classTable.getGenericPtr(genericID).getXClass(virtualMachine, genericClass, null);
	}

	public int getObjectSize() {
		return fieldCount;
	}

	public int getGenericID(String genericName) {
		for(int i=0; i<genericInfos.length; i++){
			if(genericInfos[i].getName().equals(genericName)){
				return i;
			}
		}
		throw new XRuntimeException("Can't find generic class %s", genericName);
	}

	public XGenericInfo getGenericInfo(int id) {
		return genericInfos[id];
	}
	
	public int getGenericParams() {
		return genericInfos.length;
	}
	
	public void markObjectObjectsVisible(XObject object) {
		for(int i=0; i<fields.length; i++){
			if(!XModifier.isStatic(fields[i].getModifier())){
				if(fields[i].getTypePrimitive()==XPrimitive.OBJECT){
					long pointer = fields[i].get(object);
					XObject obj = virtualMachine.getObjectProvider().getObject(pointer);
					if(obj!=null)
						obj.markVisible();
				}
			}
		}
		if(objectArray){
			for(int i=0; i<object.getArrayLength(); i++){
				long pointer = object.getArrayElement(i);
				XObject obj = virtualMachine.getObjectProvider().getObject(pointer);
				if(obj!=null)
					obj.markVisible();
			}
		}
		for(int i=0; i<superClasses.length; i++){
			superClasses[i].getXClassNonNull(virtualMachine).markObjectObjectsVisible(object);
		}
	}
	
	@Override
	public void markVisible(){
		super.markVisible();
		for(int i=0; i<fields.length; i++){
			if(XModifier.isStatic(fields[i].getModifier())){
				if(fields[i].getTypePrimitive()==XPrimitive.OBJECT){
					long pointer = fields[i].get(null);
					XObject obj = virtualMachine.getObjectProvider().getObject(pointer);
					if(obj!=null)
						obj.markVisible();
				}
			}
		}
		XObject obj = virtualMachine.getObjectProvider().getObject(classObject);
		if(obj!=null)
			obj.markVisible();
	}

	public byte[] getStaticData() {
		return staticData;
	}

	public XMethod getVirtualMethod(int i) {
		return virtualMethods[i];
	}

	public XField getField(String name){
		for(int i=0; i<fields.length; i++){
			if(fields[i].getSimpleName().equals(name)){
				return fields[i];
			}
		}
		return null;
	}
	
	public XField getFieldAndParents(String name){
		XField field = getField(name);
		if(field!=null){
			return field;
		}
		for(int i=0; i<superClasses.length; i++){
			field = superClasses[i].getXClassNonNull(virtualMachine).getFieldAndParents(name);
			if(field!=null)
				return field;
		}
		return null;
	}
	
	public XMethod getMethod(String name) {
		for(int i=0; i<methods.length; i++){
			if(methods[i].getSimpleName().equals(name)){
				return methods[i];
			}
		}
		return null;
	}

	public void load(XInputStream inputStream) throws IOException {
		if(state==STATE_CREATED){
			try{
				state = STATE_LOADING;
				String className = inputStream.readUTF();
				if(!getName().equals(className))
					throw new XRuntimeException("Wrong class name %s expect %s", getName(), className);
				modifier = inputStream.readUnsignedShort();
				if(parent instanceof XClass){
					XChecks.checkModifier(this, modifier, INNERMODIFIER);
				}else if(parent instanceof XMethod){
					XChecks.checkModifier(this, modifier, INNERMODIFIER);
				}else{
					XChecks.checkModifier(this, modifier, OUTERMODIFIER);
				}
				annotations = new XAnnotation[inputStream.readUnsignedByte()];
				for(int i=0; i<annotations.length; i++){
					annotations[i] = new XAnnotation(inputStream);
				}
				
				superClasses = new XClassPtr[inputStream.readUnsignedByte()];
				canBeDiamondExtender = new boolean[superClasses.length];
				for(int i=0; i<superClasses.length; i++){
					superClasses[i] = XClassPtr.load(inputStream);
					canBeDiamondExtender[i] = true;
				}
				genericInfos = new XGenericInfo[inputStream.readUnsignedByte()];
				for(int i=0; i<genericInfos.length; i++){
					genericInfos[i] = new XGenericInfo(virtualMachine, inputStream);
				}
				int childCount = inputStream.readUnsignedByte();
				for(int i=0; i<childCount; i++){
					XClass xClass = new XClass(virtualMachine, inputStream.readUTF(), this);
					super.addChild(xClass);
					xClass.load(inputStream);
				}
				for(int i=0; i<superClasses.length; i++){
					superClasses[i].getXClassNonNull(virtualMachine);
				}
				checkDiamonds(new HashMap<XClass, XClass>());
				fields = new XField[inputStream.readUnsignedShort()];
				for(int i=0; i<fields.length; i++){
					super.addChild(fields[i] = new XField(this, inputStream));
				}
				methods = new XMethod[inputStream.readUnsignedShort()];
				for(int i=0; i<methods.length; i++){
					super.addChild(methods[i] = new XMethod(this, inputStream));
					methods[i].loadInnerClasses(inputStream);
					if(methods[i].isConstructor() && !XModifier.isStatic(methods[i].getModifier())){
						XClass[] superClasses = methods[i].getExplizitSuperInvokes();
						for(int j=0; j<superClasses.length; j++){
							for(int k=0; k<this.superClasses.length; k++){
								if(superClasses[j] == this.superClasses[k].getXClassNonNull(virtualMachine)){
									canBeDiamondExtender[k] = false;
									break;
								}
							}
						}
					}
				}
				if(isArray()){
					lengthField = getField("length");
				}
				staticData = new byte[staticFieldCount];
				state = STATE_LOADED;
				virtualMachine.getClassProvider().addClassForLoading(this);
			}catch(Throwable e){
				state = STATE_ERRORED;
				if(!(e instanceof XRuntimeException)){
					e = new XRuntimeException(e, "Error while loading class %s", getName());
				}
				throw (XRuntimeException)e;
			}
		}
	}
	
	protected void setupClassTable(){
		classTable = new XClassTable[0];
		List<XGenericClass> classes = new ArrayList<XGenericClass>();
		XGenericClass generics[] = new XGenericClass[genericInfos.length];
		for(int i=0; i<generics.length; i++){
			generics[i] = new XGenericClass(new XClassClassGeneric(virtualMachine, genericInfos[i].getName()));
		}
		addAllChilds(new XGenericClass(this, generics), classes, new ArrayList<XClass>());
		classIndex = 0;
		boolean again;
		do{
			again = false;
			for(XGenericClass gc : classes){
				int nextFreeID = gc.getXClass().getNextFreeID(classIndex);
				if(nextFreeID!=classIndex){
					classIndex = nextFreeID;
					again = true;
					break;
				}
			}
		}while(again);
		List<XMethod> methods = new ArrayList<XMethod>();
		XClassPtr[] superClasses = getSuperClasses();
		for(XClassPtr cp:superClasses){
			XMethod[] vmethods = cp.getXClass(virtualMachine).virtualMethods;
			for(XMethod vmethod:vmethods){
				addMethod(methods, vmethod, classes);
			}
		}
		for(XMethod m:this.methods){
			addMethod(methods, m, classes);
		}
		virtualMethods = methods.toArray(new XMethod[methods.size()]);
		int fieldStartID = 0;
		for(XGenericClass gc : classes){
			fieldStartID = gc.getXClass().makeClassTable(this, fieldStartID, gc);
		}
	}
	
	private static void addMethod(List<XMethod> methods, XMethod method, List<XGenericClass> generics){
		ListIterator<XMethod> i = methods.listIterator();
		while(i.hasNext()){
			int res = isMethodCompatible(method, i.next(), generics);
			if(res==1){
				i.set(method);
				return;
			}else if(res==2){
				return;
			}
		}
		methods.add(method);
	}
	
	private static int isMethodCompatible(XMethod m1, XMethod m2, List<XGenericClass> generics){
		if(m1==m2)
			return 2;
		if(!m1.getRealName().equals(m2.getRealName()))
			return 0;
		if(m1.getParamCount()!=m2.getParamCount())
			return 0;
		int ret = 1;
		if(!m1.getDeclaringClass().canCastTo(m2.getDeclaringClass())){
			XMethod tmp = m1;
			m1 = m2;
			m2 = tmp;
			ret = 2;
		}
		XGenericClass g1 = gcf(m1.getDeclaringClass(), generics);
		XGenericClass g2 = gcf(m2.getDeclaringClass(), generics);
		if(!m2.getReturnType(g2, null).canCastTo(m1.getReturnType(g1, null)))
			return 0;
		XClassPtr[] p1 = m1.getParams();
		XClassPtr[] p2 = m2.getParams();
		XVirtualMachine vm = m1.getDeclaringClass().getVirtualMachine();
		for(int i=0; i<p1.length; i++){
			if(!p2[i].getXClass(vm, g2, null).canCastTo(p1[i].getXClass(vm, g1, null)))
				return 0;
		}
		return ret;
	}
	
	private static XGenericClass gcf(XClass c, List<XGenericClass> generics){
		for(XGenericClass generic:generics){
			if(generic.getXClass()==c)
				return generic;
		}
		return null;
	}
	
	private static boolean canCastTo2(XClass from, XClass to){
		if(from==to)
			return true;
		for(XClassPtr classes : from.getSuperClasses()){
			if(canCastTo2(classes.getXClassNonNull(from.virtualMachine), to)){
				return true;
			}
		}
		return false;
	}
	
	private int makeClassTable(XClass c, int fieldStartID, XGenericClass gc){
		if(isObjectClass() && !c.isObjectClass())
			return fieldStartID;
		int[] methodIDs = new int[methodCount];
		for(int i=0; i<methods.length; i++){
			if(!XModifier.isStatic(methods[i].getModifier())){
				methodIDs[methods[i].getIndex()] = findIDFor(methods[i], gc, c);
			}
		}
		XClassTable ct = new XClassTable(c, fieldStartID, methodIDs, null);
		if(c.classIndex>=classTable.length){
			XClassTable[] newClassTable = new XClassTable[c.classIndex+1];
			System.arraycopy(classTable, 0, newClassTable, 0, classTable.length);
			classTable = newClassTable;
		}
		classTable[c.classIndex] = ct;
		return fieldStartID + fieldCount;
	}
	
	private int findIDFor(XMethod xMethod, XGenericClass gc, XClass c) {
		XMethod[] methods = c.virtualMethods;
		int id=0;
		for(XMethod m:methods){
			if(m==xMethod){
				return id;
			}
			id++;
		}
		return -1;
	}

	private int getNextFreeID(int freeID) {
		if(classTable.length<freeID && classTable[freeID]!=null){
			for(int i=freeID+1; i<classTable.length; i++){
				if(classTable[i]==null)
					return i;
			}
			return classTable.length;
		}
		return freeID;
	}

	private void addAllChilds(XGenericClass c, List<XGenericClass> classes, List<XClass> in){
		if(!classes.contains(c)){
			XClass cc = c.getXClass();
			for(XGenericClass gc:classes){
				if(cc==gc.getXClass())
					throw new XRuntimeException("Can't extend the same class with different generics", cc);
			}
			if(in.contains(cc))
				throw new XRuntimeException("Recusrion", cc);
			in.add(cc);
			XClassPtr[] superClasses = cc.getSuperClasses();
			for(XClassPtr cp:superClasses){
				addAllChilds(cp.getXClass(virtualMachine, c, null), classes, in);
			}
			in.remove(cc);
			classes.add(c);
		}
	}
	
	private void checkDiamonds(HashMap<XClass, XClass> checkedClasses) {
		for(int i=0; i<superClasses.length; i++){
			XClass xClass = superClasses[i].getXClassNonNull(virtualMachine);
			XClass first = checkedClasses.get(xClass);
			if(first == null){
				checkedClasses.put(xClass, this);
			}else{
				first.checkDiamondAbleFor(xClass);
			}
			xClass.checkDiamonds(checkedClasses);
		}
	}

	private void checkDiamondAbleFor(XClass xClass) {
		for(int i=0; i<superClasses.length; i++){
			if(superClasses[i].getXClassNonNull(virtualMachine)==xClass){
				if(canBeDiamondExtender[i])
					return;
				throw new XRuntimeException("Class %s can be used as diamond base", xClass);
			}
		}
		throw new XRuntimeException("Oh, this should never be happened, this is a fatal error :(");
	}

	protected void postLoad() {
		virtualMachine.getClassProvider().removeClassForLoading(this);
		if(state==STATE_LOADED){
			try{
				state = STATE_POST_LOADING;
				for(int i=0; i<superClasses.length; i++){
					XClass xClass = superClasses[i].getXClassNonNull(virtualMachine);
					xClass.postLoad();
					if(xClass.state!=STATE_RUNNABLE){
						throw new XRuntimeException("Class %s isn't inited correctly, so %s can't inited", xClass, this);
					}
				}
				setupClassTable();
				state = STATE_RUNNABLE;
				XMethod xMethod = getMethod(XMethod.STATIC_INIT+"()void");
				if(xMethod!=null)
					virtualMachine.getThreadProvider().importantInterrupt("Static "+getName(), xMethod, new XGenericClass[0], new long[0]);
			}catch(Throwable e){
				state = STATE_ERRORED;
				if(!(e instanceof XRuntimeException)){
					e = new XRuntimeException(e, "Error while post loading class %s", getName());
				}
				throw (XRuntimeException)e;
			}
		}
	}

	public int getStaticFieldIndex(int sizeInObject) {
		if(state!=STATE_LOADING)
			throw new XRuntimeException("You can't get a field index now");
		int ret = staticFieldCount;
		staticFieldCount += sizeInObject;
		return ret;
	}

	public int getFieldIndex(int sizeInObject) {
		if(state!=STATE_LOADING)
			throw new XRuntimeException("You can't get a field index now");
		int ret = fieldCount;
		fieldCount += sizeInObject;
		return ret;
	}

	public int getMethodIndex() {
		if(state!=STATE_LOADING)
			throw new XRuntimeException("You can't get a method index now");
		return methodCount++;
	}

	public int getEnumIndex() {
		if(!isEnum())
			throw new XRuntimeException("This is not a enum");
		if(state!=STATE_LOADING)
			throw new XRuntimeException("You can't get a enum index now");
		return enumCount ++;
	}
	
	public int getState() {
		return state;
	}
	
	public void save(XOutputStream outputStream) throws IOException{
		outputStream.writeUTF(getName());
		outputStream.writeShort(modifier);
		outputStream.writeByte(annotations.length);
		for(int i=0; i<annotations.length; i++){
			annotations[i].save(outputStream);
		}
		
		List<XClass> classChilds = new ArrayList<XClass>();
		
		for(XPackage p:childs.values()){
			if(p instanceof XClass){
				classChilds.add((XClass)p);
			}
		}
		
		outputStream.writeByte(superClasses.length);
		for(int i=0; i<superClasses.length; i++){
			superClasses[i].save(outputStream);
		}
		
		outputStream.writeByte(genericInfos.length);
		for(int i=0; i<genericInfos.length; i++){
			genericInfos[i].save(outputStream);
		}

		outputStream.writeByte(classChilds.size());
		for(XClass classChild:classChilds){
			outputStream.writeUTF(classChild.getSimpleName());
			classChild.save(outputStream);
		}
		
		outputStream.writeShort(fields.length);
		for(int i=0; i<fields.length; i++){
			fields[i].save(outputStream);
		}
		
		outputStream.writeShort(methods.length);
		for(int i=0; i<methods.length; i++){
			methods[i].save(outputStream);
		}
		
	}

	public XMethod[] getMethods() {
		return methods;
	}

	//Array things
	
	private XField lengthField;
	private final boolean objectArray;
	private final int elementSize;
	private final boolean isArray;
	private final int primitive;
	
	public XField getLengthField() {
		return lengthField;
	}
	
	public boolean isArray() {
		return isArray;
	}

	public int getArrayElementSize() {
		return elementSize;
	}
	
	public int getArrayPrimitive() {
		return primitive;
	}

	public String dump() {
		String out;
		if(parent.getClass() == XPackage.class){
			out = "package "+getParent().getName()+";\n";
		}else{
			out = "in "+getParent().getName()+";\n";
		}
		out += XModifier.getSource(modifier);
		if(isEnum()){
			out += "enum ";
		}else if(isAnnotation()){
			out += "@annotation ";
		}else{
			out += "class ";
		}
		out += getSimpleName();
		if(genericInfos.length>0){
			out += "<"+genericInfos[0].dump();
			for(int i=1; i<genericInfos.length; i++){
				out += ", "+genericInfos[i].dump();
			}
			out += ">";
		}
		if(superClasses.length>0){
			out += ":"+superClasses[0];
			for(int i=1; i<superClasses.length; i++){
				out += ", "+superClasses[i];
			}
		}
		out += "{\n";
		for(XField field:fields){
			out += field.dump()+"\n";
		}
		for(XMethod method:methods){
			out += method.dump()+"\n";
		}
		out += "}";
		return out;
	}

	public void onRequest() {}

	public XField[] getFields() {
		return fields;
	}

	public long getClassObject() {
		if(classObject==0){
			XClass c = virtualMachine.getClassProvider().getXClass("xscript.lang.Class");
			XGenericClass gc = new XGenericClass(c);
			classObject = virtualMachine.getObjectProvider().createObject(gc);
			long name = virtualMachine.getObjectProvider().createString(getName());
			XField nameF = c.getField("name");
			nameF.finalSet(virtualMachine.getObjectProvider().getObject(classObject), name);
		}
		return classObject;
	}
	
}
