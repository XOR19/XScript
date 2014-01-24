package xscript.compiler;

public class XError {

	public static void shouldNeverCalled(){
		throw new AssertionError("Should never be happened :(");
	}
	
}
