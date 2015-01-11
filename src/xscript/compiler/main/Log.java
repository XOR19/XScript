package xscript.compiler.main;

import java.io.PrintWriter;
import java.util.IllegalFormatException;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

import xscript.compiler.XDiagnostic;

class Log implements DiagnosticListener<String>{

	public enum Kind{
		NOTICE,
		WARNING,
		ERROR
	}
	
	private PrintWriter errWriter;

	private PrintWriter warnWriter;

	private PrintWriter noticeWriter;
	
	private Kind last;
	
	public Log(PrintWriter errWriter, PrintWriter warnWriter, PrintWriter noticeWriter){
		this.errWriter = errWriter;
		this.warnWriter = warnWriter;
		this.noticeWriter = noticeWriter;
	}
	
	public Log(){
		this(new PrintWriter(System.err), new PrintWriter(System.err), new PrintWriter(System.out));
	}
	
	public void println(){
		checkLast(Kind.NOTICE);
		noticeWriter.println();
	}
	
	public void println(String key, Object...args) {
		rawprintln(localize(key, args));
	}

	public void rawprintln(String message, Object...args) {
		rawprintln(format(message, args));
	}
	
	public void rawprintln(String message) {
		checkLast(Kind.NOTICE);
		rawprintln(noticeWriter, message);
	}

	public String localize(String key){
		return key;
	}
	
	public String localize(String key, Object...args){
		return format(localize(key), args);
	}
	
	public PrintWriter getPrintWriter(Kind kind){
		switch(kind){
		case ERROR:
			return errWriter;
		case WARNING:
			return warnWriter;
		case NOTICE:
		default:
			return noticeWriter;
		}
	}
	
	public void rawprintln(Kind kind, String message) {
		checkLast(kind);
		rawprintln(getPrintWriter(kind), message);
	}
	
	private void checkLast(Kind kind){
		if(last!=kind){
			if(last!=null){
				getPrintWriter(last).flush();
			}
			last = kind;
		}
	}
	
	public static void rawprintln(PrintWriter writer, String msg) {
		int nl;
        while ((nl = msg.indexOf('\n')) != -1) {
            writer.println(msg.substring(0, nl));
            msg = msg.substring(nl+1);
        }
        if (msg.length() != 0) writer.println(msg);
	}
	
	@Override
	public void report(Diagnostic<? extends String> diagnostic) {
		String message;
		Kind kind;
		switch(diagnostic.getKind()){
		case ERROR:
			kind = Kind.ERROR;
			break;
		case MANDATORY_WARNING:
		case WARNING:
			kind = Kind.WARNING;
			break;
		case NOTE:
		case OTHER:
		default:
			kind = Kind.NOTICE;
			break;
		}
		if(diagnostic instanceof XDiagnostic){
			message = localize(diagnostic.getCode(), ((XDiagnostic)diagnostic).getArgs());
		}else{
			message = diagnostic.getMessage(Locale.US);
		}
		rawprintln(kind, message);
	}

    public static String format(String message, Object...args){
    	try{
    		return String.format(message, args);
    	}catch(IllegalFormatException e){
    		return message;
    	}
    }

	public void flush() {
		checkLast(null);
	}
	
}
