package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

import xscript.XUtils;

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

}
