package xscript.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.tools.Diagnostic;

public class XDiagnostic implements Diagnostic<String> {
	
	private static final Map<String, String> lang = new HashMap<String, String>();
	
	private static final Map<String, Kind> kinds = new HashMap<String, Kind>();
	
	static{
		//XTokenizer errors
		lang.put("char.unknown", "Unknown char '%s'");
		lang.put("char.invalid.escape", "Invalid char escape '%s'");
		lang.put("string.invalid.escape", "Invalid string escape '%s'");
		lang.put("char.unexpected.eof", "Expect \"'\" before end of file");
		lang.put("string.unexpected.eof", "Expect '\"' before end of file");
		lang.put("char.invalid.empty", "Char is empty");
		lang.put("char.invalid.length", "Char to long");
		lang.put("literal.single_dot", "Allreay digit");
		lang.put("literal.empty_exponent", "Empty exponent");
		lang.put("number.format", "Number format error %s");
		lang.put("comment.unexpected.eof", "No end of comment");

		//XTreeMakerImpl errors

		lang.put("value.unexpected.keyword", "Expected value but got keyword '%s'");
		lang.put("value.unexpected.kind", "Expected value but got '%s'");
		lang.put("func.only.one.kw", "Func can only have only one keyword, '**param', parameter");
		lang.put("func.list.after.kw", "List parameter, '*param', has to be bevore keyword, '**param', parameter");
		lang.put("func.only.one.list", "Func can only have only one list, '*param', parameter");
		lang.put("expect.keyword", "Expect keyword '%s' but got '%s'");
		lang.put("expect.ident", "Expect ident but got '%s'");

		//XTreeMakeEasy errors

		lang.put("makeeasy.tree.failure", "Failure in makeeasy %s");
		lang.put("invalid.data.types", "%s");

		//XTreeCompiler errors

		lang.put("need.default.after.default", "After default params there have to be default params");
		lang.put("break.no.target", "No target found for break");
		lang.put("break.no.lable", "Target label not found for break");
		lang.put("continue.no.target", "No target found for continue");
		lang.put("continue.no.lable", "Target label not found for continue");
		lang.put("label.duplicated", "Duplicated label");
		lang.put("label.unused", "Unused label");
		kinds.put("label.unused", Kind.WARNING);
		lang.put("element.expected.ident", "Expect ident after '.'");
		lang.put("code.dead", "Dead Code");
		kinds.put("code.dead", Kind.WARNING);
		lang.put("case.duplicated.default", "Duplicated default in switch");
		lang.put("case.duplicated.key", "Duplicated key '%s' in switch");
		lang.put("case.key.no.constant", "Key not constant in switch");
		lang.put("call.unpack.list.after.map", "List unpacking only allowed bevore map unpacking");
		lang.put("call.unpack.list.after.list", "Only one list unpacking allowed");
		lang.put("call.unpack.map.after.map", "Only one map unpacking allowed");
		lang.put("unpack.list.with.keyword", "No keyword expected for list unpacking");
		lang.put("unpack.map.with.keyword", "No keyword expected for map unpacking");
		lang.put("keyword.needed", "Need keyword after list unpacking, map unpacking and other keywords");
		lang.put("code.empty", "Empty block");
		kinds.put("code.empty", Kind.WARNING);
		lang.put("locals.not.allowed", "Locals not allowed here");
	}
	
	private XPosition position;
	private long start;
	private long end;
	private String code;
	private Object[] args;
	
	public XDiagnostic(XPosition position, String code, Object...args) {
		this.position = position;
		this.start = position.pos;
		this.end = position.pos;
		this.code = code;
		this.args = args;
	}
	
	public XDiagnostic(XPosition position, long start, String code, Object...args) {
		this.position = position;
		this.start = start;
		this.end = position.pos;
		this.code = code;
		this.args = args;
	}
	
	public XDiagnostic(XPosition position, long start, long end, String code, Object...args) {
		this.position = position;
		this.start = start;
		this.end = end;
		this.code = code;
		this.args = args;
	}

	public void setEnd(long end){
		this.end = end;
	}
	
	@Override
	public String getCode() {
		return code;
	}

	@Override
	public long getColumnNumber() {
		return position.column;
	}

	@Override
	public long getEndPosition() {
		return end;
	}

	@Override
	public Kind getKind() {
		Kind kind = kinds.get(code);
		return kind==null?Kind.ERROR:kind;
	}

	@Override
	public long getLineNumber() {
		return position.line;
	}

	@Override
	public String getMessage(Locale loc) {
		String message = code;
		String l = lang.get(message);
		if(l==null){
			return "!"+message+"!"+Arrays.toString(args);
		}
		return String.format(l, args);
	}

	@Override
	public long getPosition() {
		return position.pos;
	}

	@Override
	public String getSource() {
		return position.source;
	}

	@Override
	public long getStartPosition() {
		return start;
	}

	@Override
	public String toString() {
		return position+":"+start+"->"+end+":"+getMessage(null);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + Arrays.hashCode(args);
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + (int) (start ^ (start >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XDiagnostic other = (XDiagnostic) obj;
		if (!Arrays.equals(args, other.args))
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (end != other.end)
			return false;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
}
