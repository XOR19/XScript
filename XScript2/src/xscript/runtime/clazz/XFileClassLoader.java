package xscript.runtime.clazz;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XFileClassLoader implements XClassLoader {

	protected final File rootFile;
	
	public XFileClassLoader(File rootFile) throws IOException{
		this.rootFile = rootFile;
		if(!rootFile.exists())
			throw new IOException("class root "+rootFile+" not found");
	}
	
	private XInputStream loadClassBytes(File file, String name){
		try{
			InputStream inputStream = new FileInputStream(file);
			XInputStream classInput = new XInputStream(inputStream, name);
			return classInput;
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public XInputStream getInputStream(String name) {
		String[] s = name.split("\\.");
		File file = rootFile;
		String n = "";
		for(int i=0; i<s.length; i++){
			File[] files = file.listFiles();
			file = null;
			for(File f:files){
				if(f.getName().equals(s[i]) || f.getName().equals(s[i]+".xcbc")){
					file = f;
					break;
				}
			}
			if(file==null)
				return null;
			n += s[i];
			if(file.isFile()){
				return loadClassBytes(file, n);
			}
			n += ".";
		}
		return null;
	}
	
}
