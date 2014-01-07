package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import xscript.compiler.message.XMessageElement;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.standart.XStandartTreeMaker;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XMessageListSetter;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTreeMaker;
import xscript.compiler.tree.XVisitor;
import xscript.runtime.XVirtualMachine;
import xscript.runtime.clazz.XClass;
import xscript.runtime.clazz.XClassLoader;
import xscript.runtime.clazz.XClassMaker;
import xscript.runtime.clazz.XOutputStream;

public class XCompiler extends XVirtualMachine{

	private static HashMap<String, XTreeMaker> treeMakers = new HashMap<String, XTreeMaker>();
	
	private List<XVisitor> treeChangers = new ArrayList<XVisitor>();
	
	private List<XMessageElement> messageList = new ArrayList<XMessageElement>();
	
	private List<String> classes2Compile = new ArrayList<String>();
	
	private List<XClassCompiler> classes2Compile1 = new ArrayList<XClassCompiler>();
	
	private HashMap<XClassCompiler, XSourceProvider> classes2Save = new HashMap<XClassCompiler, XSourceProvider>();
	
	static{
		treeMakers.put("xscript", new XStandartTreeMaker());
	}
	
	public XCompiler(XClassLoader standartClassLoade){
		super(standartClassLoade, 0);
	}

	public void registerSourceProvider(XSourceProvider sourceProvider) {
		for(String c:sourceProvider.getProvidedClasses()){
			classes2Compile.add(c);
			getClassProvider().addClassMaker(new XCompilerClassMaker(c.substring(c.lastIndexOf('.')+1), this, sourceProvider), c);
		}
	}
	
	public void addTreeChanger(XVisitor treeChanger){
		if(!treeChangers.contains(treeChanger))
			treeChangers.add(treeChanger);
	}
	
	public void compile(){
		while(!classes2Compile.isEmpty()){
			String name = classes2Compile.remove(0);
			try{
				getClassProvider().getXClass(name);
			}catch(Throwable e){
				e.printStackTrace();
				postMessage(XMessageLevel.ERROR, name, "errored", new XLineDesk(0, 0, 0, 0), e.getMessage());
			}
			while(!classes2Compile1.isEmpty()){
				classes2Compile1.remove(0).gen();
			}
		}
		for(Entry<XClassCompiler, XSourceProvider> e:classes2Save.entrySet()){
			String name = e.getKey().getName();
			try{
				XOutputStream outputStream = new XOutputStream();
				e.getKey().save(outputStream);
				e.getValue().saveClass(name, outputStream.toByteArray());
			}catch(Throwable t){
				t.printStackTrace();
				postMessage(XMessageLevel.ERROR, name, "errored", new XLineDesk(0, 0, 0, 0), t.getMessage());
			}
		}
	}
	
	public void printMessages(XMessageFormatter formatter){
		for(XMessageElement messageElement:messageList){
			System.err.println(formatter.format(messageElement));
		}
	}
	
	protected void postMessage(XMessageLevel level, String className, String key, XLineDesk lineDesk, Object...args) {
		messageList.add(new XMessageElement(level, className, lineDesk, key, args));
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
	
	private static class XCompilerClassMaker extends XClassMaker{

		private XCompiler compiler;
		
		private XSourceProvider provider;
		
		public XCompilerClassMaker(String name, XCompiler compiler, XSourceProvider provider) {
			super(name);
			this.compiler = compiler;
			this.provider = provider;
		}

		@Override
		public XClass makeClass() {
			String name = getName();
			XMessageClass messageClass = new XMessageClass(compiler, name);
			XClassCompiler cc = new XClassCompiler(compiler, getSimpleName(), messageClass, null);
			compiler.classes2Save.put(cc, provider);
			return cc;
		}

		@Override
		public void onReplaced(XClass xClass) {
			XClassCompiler classCompiler = (XClassCompiler) xClass;
			String name = classCompiler.getName();
			XTreeMaker treeMaker = treeMakers.get(provider.getClassCompiler(name));
			XTree tree = treeMaker.makeTree(provider.getClassSource(name), classCompiler.getMessageList());
			for(XVisitor visitor:compiler.treeChangers){
				if(visitor instanceof XMessageListSetter){
					((XMessageListSetter) visitor).setMessageList(classCompiler.getMessageList());
				}
				tree.accept(visitor);
			}
			tree.accept(classCompiler);
			compiler.classes2Compile1.add(classCompiler);
		}
		
	}
	
}
