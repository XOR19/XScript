package xscript.compiler.main;

import java.util.HashMap;
import java.util.Map;

enum Option {

	HELP("-help", null, "opt.help", "-?") {
		@Override
		boolean process(OptionHelper helper, String arg) {
			Log log = helper.getLog();
			log.println("msg.usage.header", helper.getOwnName());
			for(Option c:Option.values()){
				c.help(log);
			}
			log.println();
			return true;
		}
	};
	private final String name;
	private final String argNameKey;
	private final String descrKey;
	Option(String name, String argName, String descrKey, String...extraNames){
		this.name = name;
		this.argNameKey = argName;
		this.descrKey = descrKey;
		Options.OPTIONS.put(name, this);
		for(String name2:extraNames){
			Options.OPTIONS.put(name2, this);
		}
	}
	Option(String name, String argName, String descrKey){
		this.name = name;
		this.argNameKey = argName;
		this.descrKey = descrKey;
		Options.OPTIONS.put(name, this);
	}
	abstract boolean process(OptionHelper helper, String arg);
	void help(Log log){
		log.rawprintln("   %-26s %s", helpSynopsis(log), log.localize(descrKey));
	}
	private String helpSynopsis(Log log) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        if(argNameKey!=null){
        	sb.append(log.localize(argNameKey));
        }
        return sb.toString();
    }
	public boolean hasArg() {
		return argNameKey!=null;
	}
	public static Option getOption(String name){
		return Options.OPTIONS.get(name);
	}
	
	private static final class Options{
		static final Map<String, Option> OPTIONS = new HashMap<String, Option>();
	}
	
}
