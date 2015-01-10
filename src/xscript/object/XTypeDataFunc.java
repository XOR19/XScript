package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

import xscript.XClosure;
import xscript.values.XValue;

public class XTypeDataFunc extends XTypeData {

	public static final XTypeDataFactory FACTORY = new XTypeDataFactory(){

		@Override
		protected XTypeData create(XRuntime runtime, XObject obj, String name, Object[] args) {
			return new XTypeDataFunc(runtime, obj);
		}

		@Override
		protected XTypeData load(XRuntime runtime, XObject obj, String name, ObjectInput in) throws IOException {
			return new XTypeDataFunc(runtime, obj);
		}
		
	};
	
	public XTypeDataFunc(XRuntime runtime, XObject obj) {
		super(runtime, obj, "Func");
	}

	@Override
	public XObjectData loadData(XRuntime runtime, XObject obj, ObjectInput in) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XObjectData createData(XRuntime runtime, XObject obj, Object[] args) {
		// TODO Auto-generated method stub
		return new XObjectDataFunc(runtime, (String)args[0], (String[])args[1], (Integer)args[2], (Integer)args[3], (Integer)args[4], (XValue)args[5], (XValue)args[6], (XValue)args[7], (Integer)args[8], (XClosure[])args[9]);
	}

	@Override
	public XValue getAttr(XRuntime runtime, XValue value, XObject obj, int attrID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XValue setAttr(XRuntime runtime, XValue value, XObject obj, int attrID, XValue v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XValue delAttr(XRuntime runtime, XValue value, XObject obj, int attrID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
