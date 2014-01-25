import java.io.File;
import java.io.IOException;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProviderToZip;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XZipClassLoader;
import xscript.runtime.genericclass.XGenericClass;

public class Test {
	
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
		
		vm.getThreadProvider().start("main", c.getMethod("test()void"), new XGenericClass[0], new long[0]);
		
		vm.getThreadProvider().run(10, 100);
		
		/*for(int i=0; i<100; i++){
		
			long startTest1 = System.nanoTime();
			vm.getThreadProvider().run(10, 100);
			long endTest1 = System.nanoTime();
			
			System.out.println((endTest1-startTest1)/1000000.0f+"ms");
			System.out.println(endTest1-startTest1+"ns");
		
		}*/
		
	}
	
}
