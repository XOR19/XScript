package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

import xscript.XUtils;
import xscript.values.XValue;

public class XTypeDataInt extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataInt(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataInt(runtime, obj);
		}
		
	};
	
	public XTypeDataInt(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Int", runtime.getBaseType(XUtils.NUMBER));
	}

	@Override
	public XValue alloc(XRuntime runtime, XValue type, List<XValue> list, Map<String, XValue> map) {
		if(list.isEmpty()){
			return map.get("i");
		}
		return list.get(0);
	}
	
}
