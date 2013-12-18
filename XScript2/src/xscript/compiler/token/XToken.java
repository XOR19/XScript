package xscript.compiler.token;

import java.util.List;

import xscript.compiler.XConstantValue;


public class XToken{

	public XTokenKind kind;
	public XLineDesk lineDesk;
	public XConstantValue param;
	public List<XComment> comments;
	public boolean space;
	
	public XToken(XTokenKind kind, XLineDesk lineDesk, List<XComment> comments, boolean space){
		this.kind = kind;
		this.lineDesk = lineDesk;
		this.comments = comments;
		this.space = space;
	}

	public XToken(XTokenKind kind, XLineDesk lineDesk, List<XComment> comments, XConstantValue param, boolean space) {
		this.kind = kind;
		this.lineDesk = lineDesk;
		this.comments = comments;
		this.param = param;
		this.space = space;
	}
	
	public String getDesk(){
		String name = kind.getName();
		if(name!=null)
			return "'"+name+"'";
		if(kind == XTokenKind.STRINGLITERAL){
			return "\""+param+"\"";
		}else if(kind == XTokenKind.CHARLITERAL){
			return "'"+param+"'";
		}
		return param.toString();
	}
	
	@Override
	public String toString(){
		String name = kind.getName();
		if(name!=null)
			return name;
		if(kind == XTokenKind.STRINGLITERAL){
			return "\""+param+"\"";
		}else if(kind == XTokenKind.CHARLITERAL){
			return "'"+param+"'";
		}
		return param.toString();
	}

}
