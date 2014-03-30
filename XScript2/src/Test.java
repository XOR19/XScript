import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProvider;
import xscript.compiler.XFileSourceProviderToZip;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.compiler.tree.XTreePrinter;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XZipClassLoader;

public class Test {
	
	private static String a(){
		return "";
	}
	
	public static void main(String[] args) throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{

		File f = new File(".");
		
		System.out.println(a()+"a"=="a");
		
		XCompiler compiler = new XCompiler(new XZipClassLoader(new File(f, "rt.zip")));
		XFileSourceProviderToZip fileSourceProviderToZip = new XFileSourceProviderToZip(f, new File(f, "rt.zip"), "xsc", "xcbc", "xscript");
		Field field = XFileSourceProvider.class.getDeclaredField("providedClasses");
		field.setAccessible(true);
		List<String> list = (List<String>) field.get(fileSourceProviderToZip);
		//list.clear();
		//list.add("xscript.lang.Generator");
		compiler.registerSourceProvider(fileSourceProviderToZip);
		compiler.addTreeChanger(new XTreeMakeEasy());
		//compiler.addTreeChanger(new XTreePrinter());
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
		
		XVirtualMachine vm = new XVirtualMachine(new XZipClassLoader(new File(f, "rt.zip")), 1024);
		XClass c = vm.getClassProvider().getXClass("test.Test");
		System.out.println(c.dump());
		
		XClass c2 = vm.getClassProvider().getXClass("test.Test2");
		System.out.println(c2.dump());
		
		//XClass c3 = vm.getClassProvider().getXClass("test.Test.test()void.A");
		//System.out.println(c3.dump());
		
		//vm.getClassProvider().getXClass("xscript.lang.annotation.RetentionPolicy");
		
		try {
			vm.invokeFunction("test.Test.test()void");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//vm.getThreadProvider().start("main", c.getMethod("test()void"), new XGenericClass[0], new long[0]);
		
		//for(int i=0; i<100; i++){
			vm.getThreadProvider().run(10, 1000);
		//}
		/*long startTest1 = System.nanoTime();
		for(int i=0; i<100; i++){
			long startTest2 = System.currentTimeMillis();
			if(vm.getThreadProvider().run(10, 100)>0)
				break;
			try {
				Thread.sleep(50-System.currentTimeMillis()+startTest2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long endTest1 = System.nanoTime();
		System.out.println((endTest1-startTest1)/1000000.0f+"ms");*/
		/*for(int i=0; i<100; i++){
		
			long startTest1 = System.nanoTime();
			vm.getThreadProvider().run(10, 100);
			long endTest1 = System.nanoTime();
			
			System.out.println((endTest1-startTest1)/1000000.0f+"ms");
			System.out.println(endTest1-startTest1+"ns");
		
		}*/
		
	}
	
}
