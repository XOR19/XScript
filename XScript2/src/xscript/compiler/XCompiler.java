package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class XCompiler {

	private static HashMap<String, XTreeMaker> treeMakers = new HashMap<String, XTreeMaker>();
	
	private List<XMessageElement> messageList = new ArrayList<XMessageElement>();
	
	private HashMap<String, XSourceProvider> classes2Compile = new HashMap<String, XSourceProvider>();
	
	private HashMap<String, XTree> classTree = new HashMap<String, XTree>();
	
	public XCompiler(){
		
	}
	
	public void registerSourceProvider(XSourceProvider sourceProvider) {
		for(String c:sourceProvider.getProvidedClasses()){
			classes2Compile.put(c, sourceProvider);
		}
	}
	
	public void compile(){
		Iterator<Entry<String, XSourceProvider>> iterator = classes2Compile.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, XSourceProvider> e = iterator.next();
			iterator.remove();
			XSourceProvider provider = e.getValue();
			String className = e.getKey();
			classTree.put(className, makeTreeFor(provider.getClassCompiler(className), className, provider.getClassSource(className)));
		}
	}
	
	public void printMessages(XMessageFormatter formatter){
		for(XMessageElement messageElement:messageList){
			System.err.println(formatter.format(messageElement));
		}
	}
	
	protected XTree makeTreeFor(String lang, String className, String source){
		XTreeMaker treeMaker = treeMakers.get(lang);
		return treeMaker.makeTree(source, new XMessageClass(className));
	}
	
	public static void registerTreeMaker(String lang, XTreeMaker treeMaker){
		if(treeMakers.containsKey(lang)){
			throw new IllegalArgumentException("Treemaker for lang "+lang+" allready exist");
		}
		treeMakers.put(lang, treeMaker);
	}
	
	public static void unregisterTreeMaker(String lang){
		treeMakers.remove(lang);
	}
	
	private class XMessageClass implements XMessageList{

		private String className;
		
		public XMessageClass(String className) {
			this.className = className;
		}

		@Override
		public void postMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object[] args) {
			messageList.add(new XMessageElement(level, className, lineDesk, key, args));
		}
		
	}
	
}
