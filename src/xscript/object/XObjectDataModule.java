package xscript.object;

import java.io.IOException;
import java.io.ObjectOutput;

public class XObjectDataModule implements XObjectData {
	
	private String name;
	
	private XConstPool constPool;
	
	private String fileName;
	
	public XObjectDataModule(String name, XConstPool constPool){
		if(constPool==null)
			throw new NullPointerException();
		this.name = name;
		this.constPool = constPool;
		fileName = name;
	}
	
	@Override
	public void delete(XRuntime runtime) {}

	@Override
	public void setVisible(XRuntime runtime) {}

	@Override
	public void save(ObjectOutput out) throws IOException {
		out.writeUTF(name);
	}

	public XConstPool getConstPool(){
		return constPool;
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean isPackage() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
