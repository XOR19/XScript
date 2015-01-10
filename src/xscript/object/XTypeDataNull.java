package xscript.object;

import java.io.IOException;
import java.io.ObjectInput;

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

}
