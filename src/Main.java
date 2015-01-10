import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import xscript.XCompilerMain;
import xscript.XScriptEngineFactory;
import xscript.XScriptLang;



public class Main {

	public static void main(String[] args) throws IOException{
		ScriptEngineManager manager = new ScriptEngineManager();
		manager.registerEngineName(XScriptLang.ENGINE_NAME, new XScriptEngineFactory());
		ScriptEngine engine = manager.getEngineByName(XScriptLang.ENGINE_NAME);
		engine.put(XScriptLang.ENGINE_ATTR_SOURCE_FILE, "Test.xsm");
		try{
			System.out.println(engine.eval(new FileReader("Test.xsm")));
		}catch(ScriptException e){
			e.printStackTrace();
		}
		//XCompilerMain.main(new String[]{"__builtin__", "sys"});
	}
	
}
