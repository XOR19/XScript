package xscript.compiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import xscript.runtime.XRuntimeException;

public class XFileSourceProviderToZip extends XFileSourceProvider {
	
	private ZipOutputStream outputStream;
	
	public XFileSourceProviderToZip(File file, File file2Save, String ending, String compiledEnding, String compiler) {
		super(file, file2Save, ending, compiledEnding, compiler);
	}

	@Override
	public void startSave() {
		if(!file2Save.getParentFile().exists())
			file2Save.getParentFile().mkdirs();
		try {
			outputStream = new ZipOutputStream(new FileOutputStream(file2Save));
		} catch (FileNotFoundException e) {
			throw new XRuntimeException(e, "error on class saving");
		}
	}
	
	@Override
	public void saveClass(String name, byte[] save) {
		try{
			name = name.replace('.', '/')+"."+compiledEnding;
			outputStream.putNextEntry(new ZipEntry(name));
			outputStream.write(save);
			outputStream.closeEntry();
		} catch (IOException e) {
			throw new XRuntimeException(e, "error on class saving");
		}
	}

	@Override
	public void endSave() {
		try{
			outputStream.close();
		} catch (IOException e) {}
		outputStream = null;
	}

}
