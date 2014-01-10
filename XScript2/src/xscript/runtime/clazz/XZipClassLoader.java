package xscript.runtime.clazz;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import xscript.runtime.XRuntimeException;

public class XZipClassLoader extends XClassLoader {

	public XZipClassLoader(File rootFile) throws IOException {
		super(rootFile);
	}

	@Override
	public XInputStream getInputStream(String name) {
		try{
			ZipFile file = new ZipFile(rootFile);
			Enumeration<? extends ZipEntry> e = file.entries();
			while(e.hasMoreElements()){
				ZipEntry entry = e.nextElement();
				if(!entry.isDirectory()){
					String className = entry.getName().substring(0, entry.getName().length()-5);
					className = className.replace('\\', '.').replace('/', '.');
					if(name.startsWith(className) && (name.length() == className.length() || name.charAt(className.length())=='.')){
						return new XInputStream(new XZipInputStream(file.getInputStream(entry), file), className);
					}
				}
			}
			file.close();
			return null;
		}catch(IOException e){
			throw new XRuntimeException(e, "error on class loading");
		}
	}
	
	private static class XZipInputStream extends InputStream{
		
		private final InputStream inputStream;
		private final ZipFile file;
		
		public XZipInputStream(InputStream inputStream, ZipFile file){
			this.inputStream = inputStream;
			this.file = file;
		}

		@Override
		public int available() throws IOException {
			return inputStream.available();
		}

		@Override
		public void close() throws IOException {
			inputStream.close();
			file.close();
		}

		@Override
		public synchronized void mark(int readlimit) {
			inputStream.mark(readlimit);
		}

		@Override
		public boolean markSupported() {
			return inputStream.markSupported();
		}

		@Override
		public int read() throws IOException {
			return inputStream.read();
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return inputStream.read(b, off, len);
		}

		@Override
		public int read(byte[] b) throws IOException {
			return inputStream.read(b);
		}

		@Override
		public synchronized void reset() throws IOException {
			inputStream.reset();
		}

		@Override
		public long skip(long n) throws IOException {
			return inputStream.skip(n);
		}
		
	}
	
}
