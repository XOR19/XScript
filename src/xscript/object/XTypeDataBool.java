package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

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

}
