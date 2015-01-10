package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

public class XTypeDataNativeFunc extends XTypeData {

	public XTypeDataNativeFunc(XRuntime runtime, XObject obj) {
		super(runtime, obj, "NativeFunc");
	}

	@Override
	public XObjectData loadData(XRuntime runtime, XObject obj, ObjectInput in) throws IOException {
		String name = in.readUTF();
		XFunctionData function = runtime.getFunction(name);
		return new XObjectDataNativeFunc(name, function);
	}

	@Override
	public XObjectData createData(XRuntime runtime, XObject obj, Object[] args) {
		String name = (String)args[0];
		XFunctionData function = runtime.getFunction(name);
		return new XObjectDataNativeFunc(name, function);
	}

}
