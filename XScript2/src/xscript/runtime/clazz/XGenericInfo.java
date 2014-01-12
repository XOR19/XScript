package xscript.runtime.clazz;

import java.io.IOException;

import xscript.runtime.XVirtualMachine;
import xscript.runtime.genericclass.XClassPtr;

public class XGenericInfo {

	private String genericName;
	
	private XClassPtr[] typeParams;
	
	private boolean isSuper;
	
	public XGenericInfo(String genericName, XClassPtr typeParams[], boolean isSuper){
		this.genericName = genericName;
		this.typeParams = typeParams;
		this.isSuper = isSuper;
	}
	
	public XGenericInfo(XVirtualMachine virtualMachine, XInputStream inputStream) throws IOException {
		genericName = inputStream.readUTF();
		typeParams = new XClassPtr[inputStream.readUnsignedByte()];
		for(int i=0; i<typeParams.length; i++){
			(typeParams[i] = XClassPtr.load(inputStream)).getXClass(virtualMachine);
		}
		isSuper = inputStream.readBoolean();
	}

	public String getName() {
		return genericName;
	}

	public void save(XOutputStream outputStream) throws IOException {
		outputStream.writeUTF(genericName);
		outputStream.writeByte(typeParams.length);
		for(int i=0; i<typeParams.length; i++){
			typeParams[i].save(outputStream);
		}
		outputStream.writeBoolean(isSuper);
	}

	public boolean isSuper() {
		return isSuper;
	}
	
	public XClassPtr[] getTypeParams() {
		return typeParams;
	}

	public String dump() {
		String out = genericName;
		if(typeParams.length>0){
			if(isSuper){
				out += " super ";
			}else{
				out += ":";
			}
			out += typeParams[0];
			for(int i=1; i<typeParams.length; i++){
				out += " & "+typeParams[0];
			}
		}
		return out;
	}
	
}
