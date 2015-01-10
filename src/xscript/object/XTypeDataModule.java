package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

public class XTypeDataModule extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataModule(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataModule(runtime, obj);
		}
		
	};
	
	public XTypeDataModule(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Module");
	}

	@Override
	public XObjectData loadData(XRuntime runtime, XObject obj, ObjectInput in) throws IOException {
		String name = in.readUTF();
		XConstPool constPool = runtime.loadModule(name);
		return new XObjectDataModule(name, constPool);
	}

	@Override
	public XObjectData createData(XRuntime runtime, XObject obj, Object[] args) {
		String name = (String)args[0];
		if(args.length==2){
			return new XObjectDataModule(name, (XConstPool)args[1]);
		}
		XConstPool constPool = runtime.loadModule(name);
		return new XObjectDataModule(name, constPool);
	}
	
}
