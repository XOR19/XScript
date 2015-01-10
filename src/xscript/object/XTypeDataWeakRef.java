package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

import xscript.XExec;
import xscript.XUtils;
import xscript.values.XValue;

public class XTypeDataWeakRef extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataWeakRef(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataWeakRef(runtime, obj);
		}
		
	};
	
	private static final String[] METHODS = {"get"};
	
	public XTypeDataWeakRef(XRuntime runtime, XObject obj) {
		super(runtime, obj, "WeakRef");
	}
	
	@Override
	public XObjectData loadData(XRuntime runtime, XObject obj, ObjectInput in) throws IOException {
		XValue value = XValue.read(in);
		return new XObjectDataWeakRef(value);
	}

	@Override
	public XObjectData createData(XRuntime runtime, XObject obj, Object[] args) {
		return new XObjectDataWeakRef((XValue)args[0]);
	}

	@Override
	public XValue invoke(XRuntime runtime, XExec exec, int id, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map){
		XObjectDataWeakRef weakRef = XUtils.getDataAs(runtime, thiz, XObjectDataWeakRef.class);
		switch(id){
		case 0:
			return weakRef.getRef();
		}
		return super.invoke(runtime, exec, id, thiz, params, list, map);
	}
	
	@Override
	public String[] getMethods() {
		return METHODS;
	}
	
}
