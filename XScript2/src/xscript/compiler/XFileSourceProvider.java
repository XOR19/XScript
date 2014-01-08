package xscript.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class XFileSourceProvider implements XSourceProvider {

	private File file;
	
	private String ending;
	
	private String compiler;
	
	private String compiledEnding;
	
	private List<String> providedClasses;
	
	public XFileSourceProvider(File file, String ending, String compiledEnding, String compiler){
		this.file = file;
		this.ending = ending;
		this.compiler = compiler;
		this.compiledEnding = compiledEnding;
		providedClasses = new ArrayList<String>();
		searchDirecotry(null, file);
	}
	
	private void searchDirecotry(String app, File file){
		File[] childs = file.listFiles();
		if(app==null){
			app="";
		}else{
			app += ".";
		}
		for(File child:childs){
			if(child.isDirectory()){
				searchDirecotry(app+child.getName(), child);
			}else if(child.isFile()){
				if(child.getName().endsWith("."+ending)){
					providedClasses.add(app+child.getName().substring(0, child.getName().length()-1-ending.length()));
				}
			}
		}
	}
	
	@Override
	public List<String> getProvidedClasses() {
		return providedClasses;
	}

	@Override
	public String getClassSource(String name) {
		File file = new File(this.file, name.replace('.', '/')+"."+ending);
		try {
			String source = "";
			String line;
			BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while((line=buffer.readLine())!=null){
				source += line+"\n";
			}
			buffer.close();
			return source;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getClassCompiler(String name) {
		return compiler;
	}

	@Override
	public void saveClass(String name, byte[] save) {
		File file = new File(this.file, name.replace('.', '/')+"."+compiledEnding);
		try {
			OutputStream outputStream = new FileOutputStream(file);
			outputStream.write(save);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
