import java.io.File;
import java.io.IOException;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProvider;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.runtime.clazz.XClassLoader;



public class Test {
	
	public static void main(String[] args) throws IOException{
		
		XCompiler compiler = new XCompiler(new XClassLoader(new File(".")));
		compiler.registerSourceProvider(new XFileSourceProvider(new File("."), "xsc", "xscript"));
		compiler.addTreeChanger(new XTreeMakeEasy());
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
		
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
