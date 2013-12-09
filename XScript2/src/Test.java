import xscript.compiler.XMessageList;
import xscript.compiler.XMessagePrinter;
import xscript.compiler.XTree;
import xscript.compiler.XTreePrinter;
import xscript.compiler.standart.XStandartTreeMaker;



public class Test {
	
	public static void main(String[] args){
		
		XStandartTreeMaker maker = new XStandartTreeMaker();
		XTree tree = maker.makeTree("public class A {public void v(){x=(b)->(a<b)+4;}}", new XMessagePrinter());
		XTreePrinter treePrinter = new XTreePrinter();
		tree.accept(treePrinter);
		
	}
	
}
