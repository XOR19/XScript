package xscript.compiler.main;

import java.io.File;


interface OptionHelper {

	public Log getLog();

	public String getOwnName();

	public boolean addSourceDir(File file);

	public boolean setOutputDir(File file);
	
	public void error(String key, Object...args);
	
}
