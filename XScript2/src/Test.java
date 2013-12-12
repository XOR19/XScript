import xscript.compiler.message.XMessagePrinter;
import xscript.compiler.standart.XStandartTreeMaker;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTreePrinter;



public class Test {
	
	public static void main(String[] args){
		
		XStandartTreeMaker maker = new XStandartTreeMaker();
		XTree tree = maker.makeTree("public class A {public void A(A.c...b){a->a+3;}}", new XMessagePrinter());
		XTreePrinter treePrinter = new XTreePrinter();
		tree.accept(treePrinter);
		
	}
	
}
