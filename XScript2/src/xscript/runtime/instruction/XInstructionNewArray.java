package xscript.runtime.instruction;

import java.io.IOException;

import xscript.runtime.XChecks;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XInputStream;
import xscript.runtime.clazz.XOutputStream;
import xscript.runtime.genericclass.XClassPtr;
import xscript.runtime.genericclass.XGenericClass;
import xscript.runtime.object.XObject;
import xscript.runtime.threads.XMethodExecutor;
import xscript.runtime.threads.XThread;

public class XInstructionNewArray extends XInstruction {

	private final int init;
	private XClassPtr xClass;
	
	public XInstructionNewArray(XClassPtr xClass, int init){
		this.init = init;
		this.xClass = xClass;
	}
	
	public XInstructionNewArray(XInputStream inputStream) throws IOException{
		init = inputStream.readUnsignedByte();
		xClass = XClassPtr.load(inputStream);
	}
	
	@Override
	public void run(XVirtualMachine vm, XThread thread, XMethodExecutor methodExecutor) {
		int[] size = new int[init];
		for(int i=init-1; i>=0; i--){
			size[i] = methodExecutor.iPop();
		}
		XGenericClass rClass = xClass.getXClass(vm, methodExecutor.getDeclaringClass(), methodExecutor);
		long pointer = createArray(rClass, 0, size, vm, methodExecutor);
		methodExecutor.oPush(pointer);
	}

	private long createArray(XGenericClass c, int pos, int[] size, XVirtualMachine vm, XMethodExecutor methodExecutor){
		XChecks.checkAccess(methodExecutor.getDeclaringClass().getXClass(), c.getXClass());
		int s = size[pos];
		long pointer = vm.getObjectProvider().createArray(c, s);
		pos++;
		if(pos<size.length){
			XObject obj = vm.getObjectProvider().getObject(pointer);
			XGenericClass cc = c.getGeneric(0);
			for(int i=0; i<s; i++){
				long ch = createArray(cc, pos, size, vm, methodExecutor);
				obj.setArrayElement(i, ch);
			}
		}
		return pointer;
	}
	
	@Override
	protected void save(XOutputStream outputStream) throws IOException {
		outputStream.writeByte(init);
		xClass.save(outputStream);
	}

	@Override
	public String getSource() {
		return "newArray "+xClass+" "+init;
	}

}
