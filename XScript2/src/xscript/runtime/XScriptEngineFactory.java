package xscript.runtime;

import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class XScriptEngineFactory implements ScriptEngineFactory {

	private static final HashMap<String, Object> parameters = new HashMap<String, Object>();

	static {
		parameters.put(ScriptEngine.ENGINE, XScriptLang.ENGINE_NAME);
		parameters.put(ScriptEngine.ENGINE_VERSION, XScriptLang.ENGINE_VERSION);
		parameters.put(ScriptEngine.LANGUAGE, XScriptLang.LANG_NAME);
		parameters.put(ScriptEngine.LANGUAGE_VERSION, XScriptLang.LANG_VERSION);
		parameters.put(ScriptEngine.NAME, XScriptLang.NAME);
	}

	/**
	 * Should only be invoked by <code>sun.misc.Service</code>
	 */
	public XScriptEngineFactory() {

	}

	/**
	 * The name of the engine
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.ENGINE_NAME
	 */
	@Override
	public String getEngineName() {
		return XScriptLang.ENGINE_NAME;
	}

	/**
	 * The engine version
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.ENGINE_VERSION
	 */
	@Override
	public String getEngineVersion() {
		return XScriptLang.ENGINE_VERSION;
	}

	/**
	 * The File extension for this language
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.EXTENSIONS
	 */
	@Override
	public List<String> getExtensions() {
		return XScriptLang.EXTENSIONS;
	}

	/**
	 * The language version
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.LANG_NAME
	 */
	@Override
	public String getLanguageName() {
		return XScriptLang.LANG_NAME;
	}

	/**
	 * The mime types for this language
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.LANG_VERSION
	 */
	@Override
	public String getLanguageVersion() {
		return XScriptLang.LANG_VERSION;
	}

	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		String ret = obj;
		ret += "." + m + "(";//$NON-NLS-1$//$NON-NLS-2$
		for (int i = 0; i < args.length; i++) {
			ret += args[i];
			if (i < args.length - 1) {
				ret += ",";//$NON-NLS-1$
			}
		}
		ret += ")";//$NON-NLS-1$
		return ret;
	}

	/**
	 * The mime types for this language
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.MIME
	 */
	@Override
	public List<String> getMimeTypes() {
		return XScriptLang.MIME;
	}

	/**
	 * The short names
	 * 
	 * @see XScriptLang
	 * @return {@link XScriptLang}.NAMES
	 */
	@Override
	public List<String> getNames() {
		return XScriptLang.NAMES;
	}

	@Override
	public String getOutputStatement(String toDisplay) {
		return "VM.print(" + toDisplay + ")";
	}

	/**
	 * gets the parameter for a specific option
	 * 
	 * @param key
	 *            the key, can be one of {{@link ScriptEngine}.ENGINE,
	 *            {@link ScriptEngine}.ENGINE_VERSION, {@link ScriptEngine}
	 *            .LANGUAGE, {@link ScriptEngine}.LANGUAGE_VERSION,
	 *            {@link ScriptEngine}.NAME}
	 * @return the specific parameter
	 * @see ScriptEngine
	 */
	@Override
	public Object getParameter(String key) {
		return parameters.get(key);
	}

	@Override
	public String getProgram(String... statements) {
		String ret = "class Main{\n";//$NON-NLS-1$
		for (String s : statements) {
			ret += "\t" + s + "\n";//$NON-NLS-1$
		}
		return ret+"}\n";//$NON-NLS-1$
	}

	/**
	 * creates a new scriptengine
	 * 
	 * @return a new scriptengine
	 */
	@Override
	public ScriptEngine getScriptEngine() {
		return new XScriptEngine(this);
	}

}
