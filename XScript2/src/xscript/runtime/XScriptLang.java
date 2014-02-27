package xscript.runtime;

import java.util.List;

public class XScriptLang {

	/**
	 * The name of the script
	 */
	public static final String NAME = "XScript";//$NON-NLS-1$
	/**
	 * The name of the engine
	 */
	public static final String ENGINE_NAME = NAME;
	/**
	 * The engine version. To get allways the right, use {@link javax.script.ScriptEngineFactory}.getEngineVersion()
	 */
	public static final String ENGINE_VERSION = "1.0.0";//$NON-NLS-1$
	/**
	 * The language name, allways "MiniScript"
	 */
	public static final String LANG_NAME = NAME;
	/**
	 * The language version. To get allways the right, use {@link javax.script.ScriptEngineFactory}.getLanguageVersion()
	 */
	public static final String LANG_VERSION = "1.0.0";//$NON-NLS-1$
	/**
	 * The File extension for this language
	 */
	public static final List<String> EXTENSIONS = new XScriptImmutableList<String>();
	/**
	 * The mime types for this language
	 */
	public static final List<String> MIME = new XScriptImmutableList<String>();
	/**
	 * The short names
	 */
	public static final List<String> NAMES = new XScriptImmutableList<String>(NAME);
	
	
}
