package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

import xscript.XUtils;

public class XTypeDataFloat extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataFloat(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataFloat(runtime, obj);
		}
		
	};
	
	public XTypeDataFloat(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Float", runtime.getBaseType(XUtils.NUMBER));
	}

}
