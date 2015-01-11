package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

import xscript.values.XValue;

public class XTypeDataBool extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataBool(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataBool(runtime, obj);
		}
		
	};
	
	public XTypeDataBool(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Bool");
	}

	@Override
	public XValue alloc(XRuntime runtime, XValue type, List<XValue> list, Map<String, XValue> map) {
		if(list.isEmpty()){
			return map.get("b");
		}
		return list.get(0);
	}
	
}
