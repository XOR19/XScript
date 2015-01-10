package xscript.object;

import java.io.IOException;
import java.io.ObjectOutput;

public interface XObjectData {
	
	void delete(XRuntime runtime);

	void setVisible(XRuntime runtime);

	void save(ObjectOutput out) throws IOException;
	
}
