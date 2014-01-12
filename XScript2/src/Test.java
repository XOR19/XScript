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
	
	public static void main(String[] args) throws IOException{
		
		File f = new File(".");
		
		XCompiler compiler = new XCompiler(new XZipClassLoader(new File(f, "rt.zip")));
		compiler.registerSourceProvider(new XFileSourceProviderToZip(f, new File(f, "rt.zip"), "xsc", "xcbc", "xscript"));
		compiler.addTreeChanger(new XTreeMakeEasy());
		//compiler.addTreeChanger(new XTreePrinter());
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
		
		XVirtualMachine vm = new XVirtualMachine(new XZipClassLoader(new File(f, "rt.zip")), 1024);
		XClass c = vm.getClassProvider().getXClass("test.Test");
		System.out.println(c.dump());
		/*XStandartTreeMaker maker = new XStandartTreeMaker();
		XTree tree = maker.makeTree("public class A {public void A(A.c...b){a=1<4?a:b;}}", new XMessagePrinter());
		XTreePrinter treePrinter = new XTreePrinter();
		System.out.flush();
		System.err.flush();
		tree.accept(treePrinter);
		System.out.flush();
		System.err.flush();
		tree.accept(new XTreeMakeEasy(new XMessagePrinter()));
		System.out.flush();
		System.err.flush();
		tree.accept(treePrinter);*/
	}
	
	class A{
		<T> T test(){
			class I{
				T t;
			}
			return new I().t;
		}
	}
	
}
