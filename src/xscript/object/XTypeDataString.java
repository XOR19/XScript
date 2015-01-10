package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;
import java.util.List;
import java.util.Map;

import xscript.XExec;
import xscript.XUtils;
import xscript.values.XValue;
import xscript.values.XValueInt;

public class XTypeDataString extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataString(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataString(runtime, obj);
		}
		
	};
	
	private static final String[] METHODS = {"__str__", "__add__(other)", "indexOf(ch)", "substring(start)"};
	
	public XTypeDataString(XRuntime runtime, XObject obj) {
		super(runtime, obj, "String");
	}

	@Override
	public XObjectData loadData(XRuntime runtime, XObject obj, ObjectInput in) throws IOException {
		String string = in.readUTF();
		return new XObjectDataString(string);
	}

	@Override
	public XObjectData createData(XRuntime runtime, XObject obj, Object[] args) {
		return new XObjectDataString((String)args[0]);
	}

	@Override
	public XValue getAttr(XRuntime runtime, XValue value, XObject obj, int attrID) {
		// TODO Auto-generated method stub
		return super.getAttr(runtime, value, obj, attrID);
	}

	@Override
	public XValue setAttr(XRuntime runtime, XValue value, XObject obj, int attrID, XValue v) {
		// TODO Auto-generated method stub
		return super.setAttr(runtime, value, obj, attrID, v);
	}

	@Override
	public XValue delAttr(XRuntime runtime, XValue value, XObject obj, int attrID) {
		// TODO Auto-generated method stub
		return super.delAttr(runtime, value, obj, attrID);
	}

	@Override
	public String[] getAttributes() {
		// TODO Auto-generated method stub
		return super.getAttributes();
	}

	@Override
	public XValue invoke(XRuntime runtime, XExec exec, int id, XValue thiz, XValue[] params, List<XValue> list, Map<String, XValue> map) {
		switch (id) {
		case 0:
			return thiz;
		case 1:
			return add(runtime, thiz, params[0]);
		case 2:
			return indexOf(runtime, thiz, params[0]);
		case 3:
			return substring(runtime, thiz, params[0], list);
		default:
			break;
		}
		return super.invoke(runtime, exec, id, thiz, params, list, map);
	}

	private XValue add(XRuntime runtime, XValue thiz, XValue other){
		String s = XUtils.getString(runtime, thiz);
		String s2 = XUtils.getString(runtime, other);
		s += s2;
		return runtime.alloc(s);
	}
	
	private XValue indexOf(XRuntime runtime, XValue thiz, XValue index){
		String s = XUtils.getString(runtime, thiz);
		if(index.isInt()){
			return XValueInt.valueOf(s.indexOf((int)index.getInt()));
		}
		String s2 = XUtils.getString(runtime, index);
		return XValueInt.valueOf(s.indexOf(s2));
	}
	
	private XValue substring(XRuntime runtime, XValue thiz, XValue start, List<XValue> list) {
		int s = (int)start.getInt();
		String str = XUtils.getString(runtime, thiz);
		String ss;
		if(list.isEmpty()){
			ss = str.substring(s);
		}else{
			int e = (int)list.get(0).getInt();
			ss = str.substring(s, e);
		}
		return runtime.alloc(ss);
	}
	
	@Override
	public String[] getMethods() {
		return METHODS;
	}
	
}
