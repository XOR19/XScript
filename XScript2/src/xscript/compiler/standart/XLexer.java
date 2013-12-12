package xscript.compiler.standart;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XToken;
import xscript.compiler.token.XTokenParser;

public class XLexer {

	private XTokenParser parser;
	private List<XToken> tokens = new ArrayList<XToken>();
	private List<Integer> pos = new ArrayList<Integer>();
	private int acPos;
	
	public XLexer(String source, XMessageList messages){
		parser = new XTokenParser(source, messages);
	}
	
	public XToken getNextToken(){
		if(!pos.isEmpty() && tokens.size()>acPos){
			return tokens.get(acPos++);
		}else if(pos.isEmpty() && !tokens.isEmpty()){
			return tokens.remove(0);
		}
		XToken token = parser.readNextToken();
		if(!pos.isEmpty()){
			tokens.add(token);
			acPos++;
		}
		return token;
	}
	
	public void notSure(){
		pos.add(0, acPos);
	}
	
	public void sure(){
		pos.remove(0);
		if(pos.isEmpty()){
			tokens.clear();
			acPos=0;
		}
	}
	
	public void reset(){
		acPos = pos.remove(0);
	}
	
}
