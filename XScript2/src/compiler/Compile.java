package compiler;
import java.io.File;
import java.io.IOException;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileSourceProvider;
import xscript.compiler.XFileSourceProviderToZip;
import xscript.compiler.message.XMessageFormatter;
import xscript.compiler.tree.XTreeMakeEasy;
import xscript.compiler.tree.XTreePrinter;
import xscript.runtime.clazz.XZipClassLoader;


public class Compile {

	public static void main(String args[]) throws IOException{
		String loadExt = "xsc";
		String loadLoc = ".";
		String outLoc = ".";
		String rtLoc = "rt.zip";
		String saveExt = "xcbc";
		String compilerName = "xscript";
		boolean showTree = false;
		for(int i=0; i<args.length-1; i+=2){
			if(args[i].equals("-loadExt")){
				loadExt = args[i+1];
			}else if(args[i].equals("-loadLoc")){
				loadLoc = args[i+1];
			}else if(args[i].equals("-outLoc")){
				outLoc = args[i+1];
			}else if(args[i].equals("-rtLoc")){
				rtLoc = args[i+1];
			}else if(args[i].equals("-saveExt")){
				saveExt = args[i+1];
			}else if(args[i].equals("-compiler")){
				compilerName = args[i+1];
			}else if(args[i].equals("-tree")){
				showTree = args[i+1].equalsIgnoreCase("True");
			}
		}
		XCompiler compiler = new XCompiler(new XZipClassLoader(new File(rtLoc)));
		if(outLoc.endsWith(".zip")){
			compiler.registerSourceProvider(new XFileSourceProviderToZip(new File(loadLoc), new File(outLoc), loadExt, saveExt, compilerName));
		}else{
			compiler.registerSourceProvider(new XFileSourceProvider(new File(loadLoc), new File(outLoc), loadExt, saveExt, compilerName));
		}
		compiler.addTreeChanger(new XTreeMakeEasy());
		if(showTree)
			compiler.addTreeChanger(new XTreePrinter());
		compiler.compile();
		compiler.printMessages(new XMessageFormatter());
	}
	
}
