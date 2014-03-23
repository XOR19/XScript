package xscript.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import xscript.runtime.clazz.XPackage;

public class XCompiler extends XVirtualMachine{

	private static HashMap<String, XTreeMaker> treeMakers = new HashMap<String, XTreeMaker>();
	
	private List<XVisitor> treeChangers = new ArrayList<XVisitor>();
	
	private List<XMessageElement> messageList = new ArrayList<XMessageElement>();
	
	private List<String> classes2Compile = new ArrayList<String>();
	
	private List<XClassCompiler> classes2Compile1 = new ArrayList<XClassCompiler>();
	
	private List<XClassCompiler> classes2Compile2 = new ArrayList<XClassCompiler>();
	
	private HashMap<XClassCompiler, XSourceProvider> classes2Save = new HashMap<XClassCompiler, XSourceProvider>();
	
	private List<XSourceProvider> sourceProviders = new ArrayList<XSourceProvider>();
	
	private List<String> predefIndirectImports = new ArrayList<String>();
	
	private List<String> predefStaticIndirectImports = new ArrayList<String>();
	
	private boolean errored;
	
	static{
		treeMakers.put("xscript", new XStandartTreeMaker());
	}
	
	public XCompiler(XClassLoader standartClassLoade){
		super(standartClassLoade, 0);
		predefIndirectImports.add("xscript.lang");
	}

	public void registerSourceProvider(XSourceProvider sourceProvider) {
		if(!sourceProviders.contains(sourceProvider)){
			sourceProviders.add(sourceProvider);
			for(String c:sourceProvider.getProvidedClasses()){
				classes2Compile.add(c);
				getClassProvider().addClassMaker(new XCompilerClassMaker(c.substring(c.lastIndexOf('.')+1), this, sourceProvider), c);
			}
		}
	}
	
	public void addTreeChanger(XVisitor treeChanger){
		if(!treeChangers.contains(treeChanger))
			treeChangers.add(treeChanger);
	}
	
	private void compile1(){
		while(!classes2Compile1.isEmpty()){
			XClassCompiler cc = classes2Compile1.remove(0);
			try{
				cc.gen();
			}catch(Throwable e){
				e.printStackTrace();
				postMessage(XMessageLevel.ERROR, cc.getName(), "errored", new XLineDesk(0, 0, 0, 0), e.getMessage());
			}
		}
	}
	
	public boolean compile(){
		while(!classes2Compile.isEmpty()){
			String name = classes2Compile.remove(0);
			try{
				getClassProvider().getXClass(name);
			}catch(Throwable e){
				e.printStackTrace();
				postMessage(XMessageLevel.ERROR, name, "errored", new XLineDesk(0, 0, 0, 0), e.getMessage());
			}
			compile1();
		}
		while(!classes2Compile2.isEmpty()){
			XClassCompiler cc = classes2Compile2.remove(0);
			try{
				cc.onRequest();
			}catch(Throwable e){
				e.printStackTrace();
				postMessage(XMessageLevel.ERROR, cc.getName(), "errored", new XLineDesk(0, 0, 0, 0), e.getMessage());
			}
			compile1();
		}
		for(XSourceProvider sp:sourceProviders){
			sp.startSave();
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
		for(XSourceProvider sp:sourceProviders){
			sp.endSave();
		}
		return !errored;
	}
	
	public void printMessages(XMessageFormatter formatter){
		for(XMessageElement messageElement:messageList){
			System.err.println(formatter.format(messageElement));
		}
	}
	
	public List<XMessageElement> getMessageList(){
		return messageList;
	}
	
	protected void postMessage(XMessageLevel level, String className, String key, XLineDesk lineDesk, Object...args) {
		messageList.add(new XMessageElement(level, className, lineDesk, key, args));
		if(level==XMessageLevel.ERROR)
			errored = true;
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
		public XClass makeClass(XPackage p) {
			String name = getName();
			XMessageClass messageClass = new XMessageClass(compiler, name);
			XClassCompiler cc = new XClassCompiler(compiler, getSimpleName(), messageClass, null, p, null);
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
		}
		
	}

	public List<String> getPredefIndirectImports() {
		return predefIndirectImports;
	}

	public List<String> getPredefStaticIndirectImports() {
		return predefStaticIndirectImports;
	}
	
	public void addPredefIndirectImport(String name){
		if(!predefIndirectImports.contains(name)){
			predefIndirectImports.add(name);
		}
	}
	
	public void addPredefStaticIndirectImports(String name){
		if(!predefStaticIndirectImports.contains(name)){
			predefStaticIndirectImports.add(name);
		}
	}
	
	protected void toCompile(XClassCompiler xClassCompiler) {
		classes2Compile1.add(xClassCompiler);
	}
	
	protected void childToCompile(XClassCompiler xClassCompiler) {
		classes2Compile2.add(xClassCompiler);
	}
	
}
