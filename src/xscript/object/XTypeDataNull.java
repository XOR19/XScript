package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

import xscript.values.XValue;
import xscript.values.XValueNull;

public class XTypeDataNull extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataNull(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataNull(runtime, obj);
		}
		
	};
	
	public XTypeDataNull(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Null");
	}

	@Override
	public XValue alloc(XRuntime runtime, XValue type, List<XValue> list, Map<String, XValue> map) {
		return XValueNull.NULL;
	}
	
}
