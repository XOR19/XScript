package xscript.compiler.main;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import xscript.compiler.XFileReader;
import xscript.compiler.XInternCompiler;


public class Main {

	public static final int OK = 0;
	public static final int ERROR = 1;
	public static final int CMDERR = 2;
	public static final int SYSERR = 3;
	public static final int ABNORMAL = 4;
	
	public static void main(String[] args){
		System.exit(process(args));
	}
	
	public static int process(String[] args){
		return new Main(args).process();
	}

	private final ArgReader args;
	
	private final Log log;
	
	private final OptionHelper helper = new OptionHelper() {
		
		@Override
		public Log getLog() {
			return log;
		}

		@Override
		public String getOwnName() {
			return "xscriptc";
		}
		
	};
	
	private boolean errored;
	
	private File outputTo;
	
	private List<File> sourceDirs = new LinkedList<File>();
	
	private Set<String> compiled = new HashSet<String>();
	
	private final Map<String, Object> compilerOptions = new HashMap<String, Object>();
	
	private Main(String[] args) {
		if(args==null||args.length==0){
			this.args = null;
		}else{
			this.args = new ArgReader(args);
		}
		//ResourceBundle lang = ResourceBundle.getBundle("lang");
		log = new Log();
		sourceDirs.add(new File("."));
	}
	
	private int process() {
		try{
			if(args==null){
				Option.HELP.process(helper, null);
				return CMDERR;
			}
			
			while(args.hasNext()){
				String arg = args.next();
				if(!arg.isEmpty()){
					if(arg.charAt(0)=='-'){
						if(!processCommand(arg)){
							return CMDERR;
						}
					}else{
						processFile(arg);
					}
				}
			}
			
			return errored?ERROR:OK;
		}catch(IOException e){
			log.println("err.io", e.getMessage());
			return SYSERR;
		}catch(Throwable e){
			log.println("err.abn", e);
			return ABNORMAL;
		}finally{
			log.flush();
		}
	}
	
	private boolean processCommand(String arg) throws IOException{
		Option o = Option.getOption(arg);
		if(o==null){
			log.println("err.invalid.flag", arg);
			return false;
		}else{
			String a;
			if(o.hasArg()){
				if(args.hasNext()){
					a = args.next();
				}else{
					log.println("err.req.arg", arg);
					return false;
				}
			}else{
				a = null;
			}
			return o.process(helper, a);
		}
	}
	
	private void processFile(String file){
		String[] split = file.split("[\\./\\\\]");
		for(File sources:sourceDirs){
			check(sources, split, 0, null);
		}
	}
	
	private void check(File f, String[] splits, int i, String file){
		if(splits.length-1==i){
			String s = splits[i];
			s = Pattern.quote(s);
			final Pattern pattern = Pattern.compile(s.replace("*", ".*").replace("?", ".")+"\\.xsc");
			File[] files = f.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if(pathname.isFile()){
						return pattern.matcher(pathname.getName()).matches();
					}
					return false;
				}
			});
			for(File f2:files){
				String file2;
				String n = f2.getName();
				int index = n.lastIndexOf('.');
				if(index!=-1){
					n = n.substring(0, index);
				}
				if(file==null){
					file2 = n;
				}else{
					file2 = file + "." + n;
				}
				if(compiled.contains(file2))
					continue;
				compiled.add(file2);
				processFile(file, file2, f2);
			}
		}else{
			f = new File(f, splits[i]);
			if(file==null){
				file = splits[i];
			}else{
				file += "."+splits[i];
			}
			if(f.exists()){
				check(f, splits, i+1, file);
			}
		}
	}
	
	private void processFile(String file, String sourceName, File in){
		Reader r = null;
		OutputStream os = null;
		try{
			r = new FileReader(in);
			XFileReader reader = new XFileReader(sourceName, r);
			byte[] compiled = XInternCompiler.COMPILER.compile(compilerOptions, reader, log);
			try{
				r.close();
			}catch(IOException e){}
			r = null;
			File f = file==null?outputTo:new File(outputTo, file);
			f.mkdirs();
			int index = sourceName.lastIndexOf('.');
			if(index==-1){
				f = new File(f, sourceName+".xsc");
			}else{
				f = new File(f, sourceName.substring(index)+".xsc");
			}
			os = new FileOutputStream(f);
			os.write('X');
			os.write('S');
			os.write('C');
			os.write('M');
			os.write(compiled);
		}catch(IOException e){
			log.println("err.io", e.getMessage());
		}finally{
			if(r!=null){
				try {
					r.close();
				} catch (IOException e) {}
			}
			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {}
			}
		}
	}
	
}
