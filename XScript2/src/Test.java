import java.io.File;
import java.io.IOException;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProviderToZip;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XZipClassLoader;

public class Test {
	
	public int var;
	
	public static void main(String[] args) throws IOException{
		
		File f = new File(".");
		
		XCompiler compiler = new XCompiler(new XZipClassLoader(new File(f, "rt.zip")));
		compiler.registerSourceProvider(new XFileSourceProviderToZip(f, new File(f, "rt.zip"), "xsc", "xcbc", "xscript"));
		compiler.addTreeChanger(new XTreeMakeEasy());
		
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
		
		XVirtualMachine vm = new XVirtualMachine(new XZipClassLoader(new File(f, "rt.zip")), 1024);
		XClass c = vm.getClassProvider().getXClass("test.Test");
		System.out.println(c.dump());
		c = vm.getClassProvider().getXClass("test.Test.test()void.I");
		System.out.println(c.dump());
		
	}
	
}
