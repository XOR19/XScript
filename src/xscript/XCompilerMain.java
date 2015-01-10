package xscript;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

import xscript.compiler.XCompiler;
import xscript.compiler.XFileReader;
import xscript.compiler.XInternCompiler;

public final class XCompilerMain {

	private XCompilerMain(){}
	
	private static class SysOutDiagList implements DiagnosticListener<String>{

		@Override
		public void report(Diagnostic<? extends String> diagnostic) {
			System.err.println(diagnostic);
		}
		
	}
	
	public static void main(String[] args){
		Map<String, Object> options = new HashMap<String, Object>();
		int i = 0;
		XCompiler compiler = XInternCompiler.COMPILER;
		while(args.length>i){
			try{
				String source = args[i++];
				Reader reader = new FileReader(getInputFile(source));
				byte[] bytes = compiler.compile(options, new XFileReader(source, reader), new SysOutDiagList());
				File file = getOutputFile(source);
				OutputStream os = null;
				try{
					os = new FileOutputStream(file);
					os.write('X');
					os.write('S');
					os.write('C');
					os.write('M');
					os.write(bytes);
				}finally{
					if(os!=null)
						os.close();
				}
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}
	
	private static File getInputFile(String source){
		return new File(source+".xsm");
	}
	
	private static File getOutputFile(String source){
		return new File(source+".xcm");
	}
	
}
