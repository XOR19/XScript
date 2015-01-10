package xscript;

import java.util.List;

public final class XScriptLang {

	public static final String ENGINE_NAME = "XScriptEngine";
	public static final String ENGINE_VERSION = "1.0.0";
	public static final int ENGINE_VERSION_INT = 0;
	public static final List<String> EXTENSIONS = new XArrayList<String>("xsc");
	public static final String LANG_NAME = "XScript";
	public static final String LANG_VERSION = "1.0.0";
	public static final List<String> MIME_TYPES = new XArrayList<String>();
	public static final List<String> NAMES = new XArrayList<String>(ENGINE_NAME, LANG_NAME);

	public static final String ENGINE_ATTR_SOURCE_FILE = "SOURCE";
	public static final String ENGINE_ATTR_FUNCTIONS_BINDING = "FUNCTIONS";
	public static final String ENGINE_ATTR_COMPILER_MAP = "COMPILERS";
	public static final String ENGINE_ATTR_FILE_SYSTEM = "FILE_SYSTEM";
	public static final String ENGINE_ATTR_FILE_SYSTEM_ROOT = "FILE_SYSTEM_ROOT";
	public static final String ENGINE_ATTR_OUT = "OUT";
	public static final String ENGINE_ATTR_IN = "IN";
	
	public static final String COMPILER_OPT_COMPILER = "COMPILER";
	
	
	private XScriptLang(){}
	
}
