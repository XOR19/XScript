package xscript.compiler;

import java.util.Map;

import javax.tools.DiagnosticListener;

import xscript.compiler.parser.XTokenizer;
import xscript.compiler.tree.XTree;
import xscript.compiler.treemaker.XTreeMaker;

public class XInternCompiler implements XCompiler {

	public static final XInternCompiler COMPILER = new XInternCompiler();
	
	private XInternCompiler(){}
	
	@Override
	public byte[] compile(Map<String, Object> options, XFileReader reader, DiagnosticListener<String> diagnosticListener) {
		XCompilerOptions compilerOptions = new XCompilerOptions();
		if(options!=null)
			compilerOptions.from(options);
		XTokenizer tokenizer = new XTokenizer(reader, diagnosticListener);
		XTreeMaker treeMaker = new XTreeMaker(tokenizer, diagnosticListener);
		XTree tree = treeMaker.makeModule();
		XTreeCompiler treeCompiler = new XTreeCompiler(diagnosticListener);
		tree.accept(treeCompiler);
		return treeCompiler.getBytes();
	}

}
