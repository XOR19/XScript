import java.io.File;
import java.io.IOException;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProvider;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.instruction.XInstruction;
import xscript.runtime.method.XMethod;



public class Test {
	
	public static void main(String[] args) throws IOException{
		
		XCompiler compiler = new XCompiler(new XClassLoader(new File(".")));
		compiler.registerSourceProvider(new XFileSourceProvider(new File("."), "xsc", "xcbc", "xscript"));
		compiler.addTreeChanger(new XTreeMakeEasy());
		//compiler.addTreeChanger(new XTreePrinter());
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
		
		XVirtualMachine vm = new XVirtualMachine(new XClassLoader(new File(".")), 1024);
		XMethod m = vm.getClassProvider().getXClass("xscript.lang.Int").getMethod("toHex");
		int i=0;
		XInstruction inst;
		while((inst=m.getInstruction(i++))!=null){
			System.out.println((i-1)+":"+inst);
		}
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
	
}
