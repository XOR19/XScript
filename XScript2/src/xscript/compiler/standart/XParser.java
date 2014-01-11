package xscript.compiler.standart;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.XConstantValue;
import xscript.compiler.XOperator;
import xscript.compiler.XOperator.Type;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.token.XToken;
import xscript.compiler.token.XTokenKind;
import xscript.compiler.tree.XTree;
import xscript.compiler.tree.XTree.XAnnotation;
import xscript.compiler.tree.XTree.XArrayInitialize;
import xscript.compiler.tree.XTree.XBlock;
import xscript.compiler.tree.XTree.XBreak;
import xscript.compiler.tree.XTree.XCase;
import xscript.compiler.tree.XTree.XCast;
import xscript.compiler.tree.XTree.XCatch;
import xscript.compiler.tree.XTree.XClassDecl;
import xscript.compiler.tree.XTree.XClassFile;
import xscript.compiler.tree.XTree.XConstant;
import xscript.compiler.tree.XTree.XContinue;
import xscript.compiler.tree.XTree.XDo;
import xscript.compiler.tree.XTree.XFor;
import xscript.compiler.tree.XTree.XForeach;
import xscript.compiler.tree.XTree.XGroup;
import xscript.compiler.tree.XTree.XIdent;
import xscript.compiler.tree.XTree.XIf;
import xscript.compiler.tree.XTree.XIfOperator;
import xscript.compiler.tree.XTree.XImport;
import xscript.compiler.tree.XTree.XIndex;
import xscript.compiler.tree.XTree.XInstanceof;
import xscript.compiler.tree.XTree.XLable;
import xscript.compiler.tree.XTree.XLambda;
import xscript.compiler.tree.XTree.XMethodCall;
import xscript.compiler.tree.XTree.XMethodDecl;
import xscript.compiler.tree.XTree.XModifier;
import xscript.compiler.tree.XTree.XNew;
import xscript.compiler.tree.XTree.XNewArray;
import xscript.compiler.tree.XTree.XOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XOperatorStatement;
import xscript.compiler.tree.XTree.XReturn;
import xscript.compiler.tree.XTree.XStatement;
import xscript.compiler.tree.XTree.XSuper;
import xscript.compiler.tree.XTree.XSwitch;
import xscript.compiler.tree.XTree.XSynchronized;
import xscript.compiler.tree.XTree.XThis;
import xscript.compiler.tree.XTree.XThrow;
import xscript.compiler.tree.XTree.XTry;
import xscript.compiler.tree.XTree.XType;
import xscript.compiler.tree.XTree.XTypeParam;
import xscript.compiler.tree.XTree.XVarDecl;
import xscript.compiler.tree.XTree.XVarDecls;
import xscript.compiler.tree.XTree.XWhile;

public class XParser {

	private XLexer lexer;
	private XToken token;
	private XMessageList messages;
	private List<XLineDesk> lines = new ArrayList<XLineDesk>();
	private boolean unhandledUnexpected;
	private List<MessageBuffer> messageBuffer;
	
	public XParser(XLexer lexer, XMessageList messages){
		this.lexer = lexer;
		this.messages = messages;
		nextToken();
	}
	
	public void skip(boolean stopAtModifier, boolean stopAtType, boolean stopAtStatement, boolean stopAtClassDecl, boolean stopAtImport, boolean stopAtIdent, boolean stopAtGroup){
		unhandledUnexpected = false;
		while(token.kind!=XTokenKind.EOF){
			switch (token.kind) {
			case SEMICOLON:
				nextToken();
				return;
			case SYNCHRONIZED:
				if(stopAtModifier || stopAtStatement)
					return;
				break;
			case ABSTRACT:
			case AT:
			case FINAL:
			case NATIVE:
			case PRIVATE:
			case PROTECTED:
			case PUBLIC:
			case STATIC:
				if(stopAtModifier)
					return;
				break;
			case BOOL:
			case BYTE:
			case CHAR:
			case DOUBLE:
			case FLOAT:
			case INT:
			case LONG:
			case SHORT:
			case VOID:
				if(stopAtType)
					return;
				break;
			case BREAK:
			case CASE:
			case CONTINUE:
			case DEFAULT:
			case DO:
			case ELSE:
			case FOR:
			case IF:
			case RETURN:
			case SWITCH:
			case WHILE:
			case THROW:
			case TRY:
			case CATCH:
			case FINALLY:
			case NEW:
				if(stopAtStatement)
					return;
				break;
			case CLASS:
			case ENUM:
			case INTERFACE:
			case ANNOTATION:
				if(stopAtClassDecl)
					return;
				break;
			case RBRAKET:
				if(stopAtGroup){
					return;
				}else if(stopAtStatement){
					nextToken();
					return;
				}
				break;
			case IMPORT:
				if(stopAtImport)
					return;
				break;
			case IDENT:
				if(stopAtIdent)
					return;
				break;
			case LBRAKET:
				if(stopAtStatement || stopAtGroup)
					return;
				break;
			default:
				break;
			}
			nextToken();
		}
	}
	
	public void syntaxError(String key, Object... args){
		parserMessage(XMessageLevel.ERROR, key, args);
	}
	
	public boolean expected(XTokenKind kind){
		if(token.kind==kind){
			nextToken();
			return true;
		}else{
			syntaxError("expected", kind, token.kind, token.param);
			unhandledUnexpected = true;
			return false;
		}
	}
	
	public void startLineBlock(){
		lines.add(new XLineDesk(token.lineDesk.startLine, token.lineDesk.startLinePos, -1, -1));
	}
	
	public XLineDesk endLineBlock(){
		XLineDesk line = lines.remove(lines.size()-1);
		if(line.endLine==-1 && line.endLinePos==-1)
			return null;
		return line;
	}
	
	public int getModifier(){
		if(token.kind==XTokenKind.PUBLIC){
			return xscript.runtime.XModifier.PUBLIC;
		}else if(token.kind==XTokenKind.PRIVATE){
			return xscript.runtime.XModifier.PRIVATE;
		}else if(token.kind==XTokenKind.PROTECTED){
			return xscript.runtime.XModifier.PROTECTED;
		}else if(token.kind==XTokenKind.FINAL){
			return xscript.runtime.XModifier.FINAL;
		}else if(token.kind==XTokenKind.ABSTRACT){
			return xscript.runtime.XModifier.ABSTRACT;
		}else if(token.kind==XTokenKind.NATIVE){
			return xscript.runtime.XModifier.NATIVE;
		}else if(token.kind==XTokenKind.STATIC){
			return xscript.runtime.XModifier.STATIC;
		}else if(token.kind==XTokenKind.SYNCHRONIZED){
			return xscript.runtime.XModifier.SYNCHRONIZED;
		}
		return 0;
	}
	
	public XAnnotation makeAnnotation(){
		return null;
	}
	
	public XModifier makeModifier(){
		List<XAnnotation> annotations = null;
		int modifier = 0;
		int m;
		startLineBlock();
		while((m=getModifier())!=0 || token.kind==XTokenKind.AT){
			if(token.kind==XTokenKind.AT){
				if(annotations==null)
					annotations = new ArrayList<XTree.XAnnotation>();
				annotations.add(makeAnnotation());
			}else{
				if((m & modifier)==0){
					modifier |= m;
				}else{
					parserMessage(XMessageLevel.ERROR, "duplicated.modifier", token.kind.name);
				}
			}
			nextToken();
		}
		return new XModifier(endLineBlock(), modifier, annotations);
	}
	
	public String readOperatorOperator(){
		if(!isOperator(token.kind)){
			if(token.kind==XTokenKind.LGROUP){
				nextToken();
				expected(XTokenKind.LGROUP);
				return "()";
			}else if(token.kind==XTokenKind.LINDEX){
				nextToken();
				expected(XTokenKind.RINDEX);
				return "[]";
			}
		}
		XOperator o = readOperator(null);
		if(o==XOperator.NONE)
			throw new AssertionError();
		if(!o.canBeOverwritten)
			parserMessage(XMessageLevel.ERROR, "cant.override.operator", o.op);
		return o.op;
	}
	
	public String ident(){
		String name;
		if(token.kind==XTokenKind.IDENT){
			name = token.param.getString();
			nextToken();
			if(name.equals("operator")){
				name += readOperatorOperator();
			}
		}else{
			expected(XTokenKind.IDENT);
			name = "!error!";
		}
		return name;
	}
	
	public XIdent makeIdent(){
		return new XIdent(token.lineDesk, ident());
	}
	
	public XIdent qualident(){
		startLineBlock();
		String name = ident();
		XToken oldToken = token;
		lexer.notSure();
		while(token.kind==XTokenKind.ELEMENT){
			nextToken();
			if(token.kind==XTokenKind.IDENT){
				lexer.sure();
				name += "."+ident();
				lexer.notSure();
				oldToken = token;
			}else{
				break;
			}
		}
		lexer.reset();
		token = oldToken;
		return new XIdent(endLineBlock(), name);
	}
	
	public XImport makeImport(){
		startLineBlock();
		if(expected(XTokenKind.IMPORT)){
			boolean staticImport = false;
			if(token.kind==XTokenKind.STATIC){
				staticImport = true;
				nextToken();
			}
			String name = ident();
			boolean indirect = false;
			while(token.kind==XTokenKind.ELEMENT){
				nextToken();
				if(token.kind==XTokenKind.MUL){
					indirect = true;
					nextToken();
					break;
				}else{
					name += "."+ident();
				}
			}
			return new XImport(endLineBlock(), name, indirect, staticImport);
		}
		endLineBlock();
		return new XImport(token.lineDesk, "!error!", false, false);
	}
	
	public XType makeType(){
		return makeType(true);
	}
	
	public XType makeType(boolean beArray){
		startLineBlock();
		XIdent name;
		List<XType> typeParam = null;
		if(token.kind==XTokenKind.BOOL || token.kind==XTokenKind.BYTE || token.kind==XTokenKind.SHORT ||
				token.kind==XTokenKind.CHAR || token.kind==XTokenKind.INT || token.kind==XTokenKind.LONG 
				|| token.kind==XTokenKind.FLOAT || token.kind==XTokenKind.DOUBLE || token.kind==XTokenKind.VOID){
			name = new XIdent(new XLineDesk(token.lineDesk), token.kind.name);
			nextToken();
		}else{
			name = qualident();
			if(token.kind==XTokenKind.SMALLER){
				nextToken();
				typeParam = new ArrayList<XTree.XType>();
				typeParam.add(makeType());
				while(token.kind==XTokenKind.COMMA){
					nextToken();
					typeParam.add(makeType());
				}
				expected(XTokenKind.GREATER);
			}
		}
		int array = 0;
		if(beArray){
			while(token.kind==XTokenKind.LINDEX){
				nextToken();
				expected(XTokenKind.RINDEX);
				array++;
			}
		}
		return new XType(endLineBlock(), name, typeParam, array);
	}
	
	public List<XType> makeTypeList(XTokenKind split){
		List<XType> list = new ArrayList<XTree.XType>();
		list.add(makeType());
		while(token.kind==split){
			nextToken();
			list.add(makeType());
		}
		return list;
	}
	
	public XTypeParam makeTypeParam(){
		startLineBlock();
		String name = ident();
		boolean isSuper = false;
		List<XType> extend = null;
		if(token.kind==XTokenKind.EXTENDS || token.kind==XTokenKind.COLON){
			nextToken();
			extend = makeTypeList(XTokenKind.AND);
		}else if(token.kind==XTokenKind.SUPER){
			isSuper = true;
			nextToken();
			extend = makeTypeList(XTokenKind.AND);
		}
		return new XTypeParam(endLineBlock(), name, extend, isSuper);
	}
	
	public List<XTypeParam> makeTypeParamList(){
		List<XTypeParam> list = null;
		if(token.kind == XTokenKind.SMALLER){
			nextToken();
			list = new ArrayList<XTypeParam>();
			list.add(makeTypeParam());
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				list.add(makeTypeParam());
			}
			expected(XTokenKind.GREATER);
		}
		return list;
	}
	
	public Object[] makeParamDecl(boolean needName){
		startLineBlock();
		XModifier modifier = makeModifier();
		XType type = makeType();
		boolean varArgs = false;
		if(token.kind==XTokenKind.ELEMENT){
			varArgs = true;
			startLineBlock();
			nextToken();
			if(token.kind==XTokenKind.ELEMENT && !token.space){
				nextToken();
				if(token.kind==XTokenKind.ELEMENT && !token.space){
					nextToken();
					endLineBlock();
				}else{
					parserMessage(XMessageLevel.ERROR, "expect.elipsis", endLineBlock());
					unhandledUnexpected = true;
				}
			}else{
				parserMessage(XMessageLevel.ERROR, "expect.elipsis", endLineBlock());
				unhandledUnexpected = true;
			}
		}
		if(varArgs){
			type.array++;
		}
		String name;
		if(needName || token.kind==XTokenKind.IDENT){
			name = ident();
		}else{
			name = null;
		}
		return new Object[]{makeVarDecl(endLineBlock(), modifier, type, name, 0), varArgs};
	}
	
	public Object[] makeParamList(boolean needName){
		expected(XTokenKind.LGROUP);
		List<XVarDecl> list = new ArrayList<XTree.XVarDecl>();
		boolean varargs = false;
		if(token.kind!=XTokenKind.RGROUP){
			Object[] args = makeParamDecl(needName);
			list.add((XVarDecl)args[0]);
			varargs = (Boolean) args[1];
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				if(varargs)
					parserMessage(XMessageLevel.ERROR, "noparam.after.varargs");
				args = makeParamDecl(needName);
				list.add((XVarDecl)args[0]);
				varargs = (Boolean) args[1];
			}
		}
		expected(XTokenKind.RGROUP);
		return new Object[]{list, varargs};
	}
	
	public XVarDecls makeVarDeclStatement(XModifier modifier, boolean needEnding){
		XType type = makeType();
		XLineDesk line = new XLineDesk(token.lineDesk);
		String name = ident();
		XVarDecls varDecl = makeVarDecls(line, modifier, type, name);
		if(needEnding)
			expected(XTokenKind.SEMICOLON);
		return varDecl;
	}
	
	public XStatement makeDeclStatement(boolean needEnding){
		XModifier modifier = makeModifier();
		if(token.kind==XTokenKind.CLASS || token.kind==XTokenKind.INTERFACE || token.kind==XTokenKind.ENUM || token.kind==XTokenKind.ANNOTATION){
			return makeClassDecl(modifier);
		}else{
			return makeVarDeclStatement(modifier, needEnding);
		}
	}
	
	public boolean isOperator(XTokenKind kind){
		return kind==XTokenKind.ADD || kind==XTokenKind.SUB || kind==XTokenKind.MUL
				|| kind==XTokenKind.DIV || kind==XTokenKind.MOD || kind==XTokenKind.NOT
				|| kind==XTokenKind.BNOT || kind==XTokenKind.AND || kind==XTokenKind.OR
				|| kind==XTokenKind.XOR || kind==XTokenKind.EQUAL || kind==XTokenKind.GREATER
				|| kind==XTokenKind.SMALLER
				|| kind==XTokenKind.ELEMENT || kind==XTokenKind.COLON || kind==XTokenKind.QUESTIONMARK
				|| kind==XTokenKind.OAND || kind==XTokenKind.OOR || kind==XTokenKind.OXOR
				|| kind==XTokenKind.OBAND || kind==XTokenKind.OBOR
				|| kind==XTokenKind.OMOD || kind==XTokenKind.ONOT || kind==XTokenKind.OBNOT || kind==XTokenKind.OPOW;
	}
	
	public List<XToken> readOperators(){
		List<XToken> list = new ArrayList<XToken>();
		while(isOperator(token.kind)){
			list.add(token);
			nextToken();
		}
		return list;
	}
	
	public List<XStatement> makeMethodCallParamList() {
		expected(XTokenKind.LGROUP);
		List<XStatement> list = null;
		if(token.kind!=XTokenKind.RGROUP){
			list = new ArrayList<XTree.XStatement>();
			list.add(makeInnerStatement());
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				list.add(makeInnerStatement());
			}
		}
		expected(XTokenKind.RGROUP);
		return list;
	}
	
	public XVarDecl makeLambdaParamDecl(){
		startLineBlock();
		XType type = makeType();
		XVarDecl varDecl;
		if(token.kind==XTokenKind.IDENT || type.array!=0 || type.typeParam!=null || type.name.name.indexOf('.')!=-1){
			endLineBlock();
			varDecl = new XVarDecl(endLineBlock(), null, ident(), type, null);
		}else{
			endLineBlock();
			varDecl = new XVarDecl(type.line, null, type.name.name, null, null);
		}
		return varDecl;
	}
	
	public Object[] makeCastTypeOrLambdaParams(){
		startLineBlock();
		XToken oldToken = token;
		lexer.notSure();
		boolean error = unhandledUnexpected;
		startMessageBuffer();
		boolean knowRealy = false;
		XIdent name = null;
		List<XType> typeParam = null;
		int array = 0;
		List<XVarDecl> varDecls = null;
		if(expected(XTokenKind.LGROUP)){
			switch(token.kind){
			case IDENT:
				startLineBlock();
				name = qualident();
				if(token.kind==XTokenKind.SMALLER){
					nextToken();
					typeParam = new ArrayList<XType>();
					typeParam.add(makeType());
					while(token.kind==XTokenKind.COMMA){
						nextToken();
						typeParam.add(makeType());
					}
					if(expected(XTokenKind.GREATER))
						knowRealy = true;
				}
				while(token.kind==XTokenKind.LINDEX){
					nextToken();
					array++;
					if(expected(XTokenKind.RINDEX)){
						knowRealy = true;
					}
				}
				if(name.name.indexOf('.') == -1 && (token.kind==XTokenKind.IDENT || (token.kind==XTokenKind.COMMA && !knowRealy))){
					knowRealy = true;
					varDecls = new ArrayList<XVarDecl>();
					if(token.kind==XTokenKind.IDENT){
						varDecls.add(new XVarDecl(endLineBlock(), null, ident(), new XType(name.line, name, typeParam, array), null));
					}else{
						endLineBlock();
						varDecls.add(new XVarDecl(name.line, null, name.name, null, null));
					}
					while(token.kind==XTokenKind.COMMA){
						nextToken();
						varDecls.add(makeLambdaParamDecl());
					}
					name = null;
				}else{
					endLineBlock();
				}
				break;
			case BOOL:
			case BYTE:
			case SHORT:
			case CHAR:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case VOID:
				knowRealy = true;
				name = new XIdent(new XLineDesk(token.lineDesk), token.kind.name);
				nextToken();
				break;
			case RGROUP:
				varDecls = new ArrayList<XVarDecl>();
				knowRealy = true;
			default:
				break;
			}
			if(name!=null){
				while(token.kind==XTokenKind.LINDEX){
					nextToken();
					array++;
					if(expected(XTokenKind.RINDEX)){
						knowRealy = true;
					}
				}
			}
			if((knowRealy || (token.kind==XTokenKind.RGROUP && !unhandledUnexpected)) && name != null){
				endMessageBuffer(true);
				unhandledUnexpected |= error;
				lexer.sure();
				expected(XTokenKind.RGROUP);
				return new Object[]{knowRealy, new XType(endLineBlock(), name, typeParam, array)};
			}else if(varDecls!=null){
				endMessageBuffer(true);
				unhandledUnexpected |= error;
				lexer.sure();
				expected(XTokenKind.RGROUP);
				return new Object[]{knowRealy, varDecls};
			}
		}
		endLineBlock();
		endMessageBuffer(false);
		lexer.reset();
		unhandledUnexpected = error;
		token = oldToken;
		return null;
	}
	
	public XStatement makeArrayInitialize(){
		startLineBlock();
		expected(XTokenKind.LBRAKET);
		List<XStatement> statements = null;
		if(token.kind!=XTokenKind.RBRAKET){
			statements = new ArrayList<XStatement>();
			statements.add(makeInnerStatement());
			while(token.kind==XTokenKind.COMMA){
				statements.add(makeInnerStatement());
			}
		}
		expected(XTokenKind.RBRAKET);
		return new XArrayInitialize(endLineBlock(), statements);
	}
	
	@SuppressWarnings("unchecked")
	public XStatement makeNumRead(boolean b){
		XToken oldToken = token;
		switch(token.kind){
		case LGROUP:{
			startLineBlock();
			Object[] ret = makeCastTypeOrLambdaParams();
			if(ret==null){
				endLineBlock();
				return makeGroup();
			}else{
				boolean knowRealy = (Boolean) ret[0];
				if(knowRealy){
					if(ret[1] instanceof XType){
						XStatement s = makeStatementWithSuffixAndPrefix();
						return new XCast(endLineBlock(), (XType) ret[1], s);
					}else{
						startLineBlock();
						boolean ok=token.kind==XTokenKind.SUB;
						if(ok)
							nextToken();
						if(token.kind==XTokenKind.GREATER){
							nextToken();
							ok &= !token.space;
						}else{
							ok = false;
						}
						if(ok){
							endLineBlock();
						}else{
							syntaxError("expected", "->");
						}
						XStatement s = makeSecoundStatement(false);
						return new XLambda(endLineBlock(), (List<XVarDecl>)ret[1], s);
					}
				}else{
					XType type = (XType)ret[1];
					if(token.kind==XTokenKind.SUB){
						lexer.notSure();
						XToken oldT = token;
						nextToken();
						if(token.kind==XTokenKind.GREATER && !token.space){
							lexer.sure();
							nextToken();
							XStatement s = makeSecoundStatement(false);
							List<XVarDecl> l = new ArrayList<XVarDecl>();
							l.add(new XVarDecl(type.line, null, type.name.name, null, null));
							return new XLambda(endLineBlock(), l, s);
						}
						lexer.reset();
						token = oldT;
					}
					boolean error = unhandledUnexpected;
					unhandledUnexpected = false;
					startMessageBuffer();
					XToken oldT = token;
					lexer.notSure();
					XStatement s = makeStatementWithSuffixAndPrefix();
					if(unhandledUnexpected){
						endMessageBuffer(false);
						lexer.reset();
						token = oldT;
						endLineBlock();
						unhandledUnexpected = error;
						return new XGroup(type.line, type.name);
					}else{
						endMessageBuffer(true);
						lexer.sure();
						unhandledUnexpected |= error;
						return new XCast(endLineBlock(), type, s);
					}
				}
			}
		}case IDENT:{
			startLineBlock();
			lexer.notSure();
			startMessageBuffer();
			boolean error = unhandledUnexpected;
			unhandledUnexpected = false;
			XType type = makeType();
			if(unhandledUnexpected){
				token = oldToken;
				endMessageBuffer(false);
				unhandledUnexpected = error;
				lexer.reset();
				XIdent ident = makeIdent();
				if(token.kind==XTokenKind.SUB){
					XToken oldT = token;
					lexer.notSure();
					nextToken();
					if(token.kind==XTokenKind.GREATER && !token.space){
						lexer.sure();
						nextToken();
						XStatement s = makeSecoundStatement(false);
						List<XVarDecl> l = new ArrayList<XVarDecl>();
						l.add(new XVarDecl(ident.line, null, ident.name, null, null));
						return new XLambda(endLineBlock(), l, s);
					}else{
						lexer.reset();
						token = oldT;
					}
				}
				endLineBlock();
				return ident;
			}
			boolean rtype = type.typeParam!=null || type.array>0;
			if(!rtype && token.kind==XTokenKind.SUB && type.name.name.indexOf('.')==-1){
				XToken oldT = token;
				lexer.notSure();
				nextToken();
				if(token.kind==XTokenKind.GREATER && !token.space){
					lexer.sure();
					nextToken();
					XStatement s = makeSecoundStatement(false);
					List<XVarDecl> l = new ArrayList<XVarDecl>();
					l.add(new XVarDecl(type.name.line, null, type.name.name, null, null));
					return new XLambda(endLineBlock(), l, s);
				}else{
					lexer.reset();
					token = oldT;
				}
			}
			endLineBlock();
			return type;
		}case FLOATLITERAL:
		case DOUBLELITERAL:
		case LONGLITERAL:
		case INTLITERAL:
		case CHARLITERAL:
		case STRINGLITERAL: 
			nextToken();
			return new XConstant(oldToken.lineDesk, oldToken.param);
		case TRUE:
			nextToken();
			return new XConstant(oldToken.lineDesk, new XConstantValue(true));
		case FALSE:
			nextToken();
			return new XConstant(oldToken.lineDesk, new XConstantValue(false));
		case NULL:
			nextToken();
			return new XConstant(oldToken.lineDesk, new XConstantValue(null));
		case LBRAKET:
			return makeArrayInitialize();
		case NEW:
			startLineBlock();
			nextToken();
			XType type = makeType(false);
			if(token.kind==XTokenKind.LINDEX){
				List<XStatement> l = new ArrayList<XStatement>();
				while(token.kind==XTokenKind.LINDEX){
					nextToken();
					if(token.kind!=XTokenKind.RINDEX){
						l.add(makeInnerStatement());
					}else{
						l.add(null);
					}
					expected(XTokenKind.RINDEX);
				}
				type.array = l.size();
				XStatement arrayInitialize = null;
				if(token.kind==XTokenKind.LBRAKET){
					arrayInitialize = makeArrayInitialize();
				}
				return new XNewArray(endLineBlock(), type, l, arrayInitialize);
			}else{
				List<XStatement> params = makeMethodCallParamList();
				XClassDecl classDecl = null;
				if(token.kind==XTokenKind.LBRAKET){
					startLineBlock();
					List<XTree> body = classAndInterfaceBody(false, null);
					List<XType> superClasses = new ArrayList<XTree.XType>();
					superClasses.add(type);
					classDecl = new XClassDecl(endLineBlock(), null, null, null, superClasses, body);
				}
				return new XNew(endLineBlock(), type, params, classDecl);
			}
		case THIS:
			nextToken();
			return new XThis(oldToken.lineDesk);
		case SUPER:
			nextToken();
			return new XSuper(oldToken.lineDesk);
		default:
			if(b)
				parserMessage(XMessageLevel.ERROR, "unexpected", token.kind.name);
			return null;
		}
	}
	
	public XStatement makeStatementWithSuffixAndPrefix(){
		startLineBlock();
		startLineBlock();
		XOperator operator;
		List<XOperator> prefix = new ArrayList<XOperator>();
		while(isOperator(token.kind)){
			operator = readOperator(Type.PREFIX);
			if(operator==XOperator.NONE)
				break;
			prefix.add(0, operator);
		}
		if(prefix.isEmpty())
			prefix = null;
		List<XType> typeParam = null;
		if(token.kind==XTokenKind.SMALLER){
			nextToken();
			typeParam = new ArrayList<XTree.XType>();
			typeParam.add(makeType());
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				typeParam.add(makeType());
			}
			expected(XTokenKind.GREATER);
		}
		XStatement statement = makeNumRead(true);
		List<XOperator> suffix = new ArrayList<XOperator>();
		while(isOperator(token.kind)){
			operator = readOperator(Type.SUFFIX);
			if(operator==XOperator.NONE)
				break;
			suffix.add(operator);
		}
		if(suffix.isEmpty())
			suffix = null;
		if(suffix == null){
			endLineBlock();
		}else{
			statement = new XOperatorPrefixSuffix(endLineBlock(), null, statement, suffix);
		}
		while(token.kind==XTokenKind.LGROUP || token.kind==XTokenKind.LINDEX || token.kind==XTokenKind.ELEMENT || token.kind==XTokenKind.INSTANCEOF || typeParam!=null){
			startLineBlock();
			startLineBlock();
			if(token.kind==XTokenKind.LGROUP || typeParam!=null){
				List<XStatement> list = makeMethodCallParamList();
				statement = new XMethodCall(endLineBlock(), statement, list, typeParam);
			}else if(token.kind==XTokenKind.LINDEX){
				nextToken();
				XStatement index = makeInnerStatement();
				expected(XTokenKind.RINDEX);
				statement = new XIndex(endLineBlock(), statement, index);
			}else if(token.kind==XTokenKind.ELEMENT){
				XToken t = token;
				nextToken();
				XIdent ident;
				if(token.kind==XTokenKind.CLASS){
					ident = new XIdent(token.lineDesk, "class");
					nextToken();
				}else if(token.kind==XTokenKind.THIS){
					ident = new XIdent(token.lineDesk, "this");
					nextToken();
				}else{
					ident = makeIdent();
				}
				statement = new XOperatorStatement(t.lineDesk, statement, XOperator.ELEMENT, ident);
			}else if(token.kind==XTokenKind.INSTANCEOF){
				XToken t = token;
				nextToken();
				XType type = makeType();
				statement = new XInstanceof(t.lineDesk, statement, type);
			}
			suffix = new ArrayList<XOperator>();
			while(isOperator(token.kind)){
				operator = readOperator(Type.SUFFIX);
				if(operator==XOperator.NONE)
					break;
				suffix.add(operator);
			}
			if(suffix.isEmpty())
				suffix = null;
			if(suffix == null){
				endLineBlock();
			}else{
				statement = new XOperatorPrefixSuffix(endLineBlock(), null, statement, suffix);
			}
			typeParam = null;
			if(token.kind==XTokenKind.SMALLER){
				nextToken();
				typeParam = new ArrayList<XTree.XType>();
				typeParam.add(makeType());
				while(token.kind==XTokenKind.COMMA){
					nextToken();
					typeParam.add(makeType());
				}
				expected(XTokenKind.GREATER);
			}
		}
		if(prefix == null){
			endLineBlock();
		}else if(statement instanceof XOperatorPrefixSuffix){
			endLineBlock();
			((XOperatorPrefixSuffix) statement).prefix = prefix;
		}else{
			statement = new XOperatorPrefixSuffix(endLineBlock(), prefix, statement, null);
		}
		return statement;
	}
	
	public XOperator readOperator(Type type){
		if(!isOperator(token.kind)){
			return XOperator.NONE;
		}
		XOperator best = XOperator.NONE;
		switch(token.kind){
		case OAND:
			best = XOperator.AND;
			break;
		case OOR:
			best = XOperator.OR;
			break;
		case OXOR:
			best = XOperator.XOR;
			break;
		case OBAND:
			best = XOperator.BAND;
			break;
		case OBOR:
			best = XOperator.BOR;
			break;
		case OMOD:
			best = XOperator.MOD;
			break;
		case ONOT:
			best = XOperator.NOT;
			break;
		case OBNOT:
			best = XOperator.BNOT;
			break;
		case OPOW:
			best = XOperator.POW;
			break;
		default:
			best = XOperator.NONE;
			break;
		}
		if(best!=XOperator.NONE){
			if(type!=null && best.type!=type){
				return XOperator.NONE;
			}
			nextToken();
			return best;
		}
		XOperator[] all = XOperator.values();
		String s = token.kind.name;
		XToken oldToken = token;
		best = XOperator.NONE;
		boolean hasNext;
		lexer.notSure();
		do{
			nextToken();
			hasNext = false;
			for(XOperator o:all){
				if(o.type!=null && (type == null || o.type==type)){
					if(o.op.equals(s)){
						best=o;
					}else if(o.op.startsWith(s)){
						hasNext = true;
					}
				}
			}
			if(hasNext && isOperator(token.kind) && !token.space){
				s += token.kind.name;
			}else{
				hasNext = false;
			}
		}while(hasNext);
		lexer.reset();
		token = oldToken;
		if(best!=XOperator.NONE){
			for(int i=0; i<best.op.length(); i++){
				nextToken();
			}
		}
		return best;
	}
	
	public XStatement mergeStatements(XLineDesk line, XStatement left, XOperator o, XStatement right, XStatement between){
		if(left instanceof XOperatorStatement){
			XOperatorStatement oLeft = (XOperatorStatement) left;
			if(oLeft.operator.priority<o.priority || (oLeft.operator.priority==o.priority && !XOperator.L2R[o.priority])){
				oLeft.right = mergeStatements(line, oLeft.right, o, right, between);
				return oLeft;
			}else{
				if(o==XOperator.IF)
					return new XIfOperator(line, left, between, right);
				return new XOperatorStatement(line, left, o, right);
			}
		}else{
			if(o==XOperator.IF)
				return new XIfOperator(line, left, between, right);
			return new XOperatorStatement(line, left, o, right);
		}
	}
	
	public XStatement makeInnerStatement(){
		XStatement statement = makeStatementWithSuffixAndPrefix();
		XLable lable = null;
		if(token.kind==XTokenKind.COLON && statement instanceof XIdent && ((XIdent)statement).name.indexOf('.')==-1){
			lable = new XLable(statement.line, ((XIdent)statement).name);
			nextToken();
			statement = makeStatementWithSuffixAndPrefix();
		}
		XStatement between = null;
		while(isOperator(token.kind)){
			startLineBlock();
			between = null;
			XOperator o = readOperator(Type.INFIX);
			if(o==XOperator.IF){
				between = makeStatementWithSuffixAndPrefix();
				expected(XTokenKind.COLON);
			}
			if(o==XOperator.NONE){
				break;
			}
			statement = mergeStatements(endLineBlock(), statement, o, makeStatementWithSuffixAndPrefix(), between);
		}
		if(lable==null)
			return statement;
		lable.statement = statement;
		return lable;
	}
	
	public XStatement makeSecoundStatement(boolean needEnding){
		XStatement statement = null;
		XStatement statement2 = null;
		XStatement statement3 = null;
		String lable = null;
		XStatement block = null;
		XStatement block2 = null;
		startLineBlock();
		switch(token.kind){
		case SYNCHRONIZED:
			lexer.notSure();
			XToken oldtoken = token;
			nextToken();
			if(token.kind==XTokenKind.LGROUP){
				lexer.sure();
				statement = makeInnerStatement();
				block = makeBlock();
				return new XSynchronized(endLineBlock(), statement, block);
			}else{
				lexer.reset();
				token = oldtoken;
			}
		case ABSTRACT:
		case AT:
		case FINAL:
		case NATIVE:
		case PRIVATE:
		case PROTECTED:
		case PUBLIC:
		case STATIC:
		case BOOL:
		case BYTE:
		case CHAR:
		case DOUBLE:
		case FLOAT:
		case INT:
		case LONG:
		case SHORT:
		case VOID:
		case CLASS:
		case ENUM:
		case INTERFACE:
		case ANNOTATION:
			endLineBlock();
			return makeDeclStatement(needEnding);
		case BREAK:
			nextToken();
			if(token.kind==XTokenKind.IDENT){
				lable = ident();
			}
			expected(XTokenKind.SEMICOLON);
			return new XBreak(endLineBlock(), lable);
		case CONTINUE:
			nextToken();
			if(token.kind==XTokenKind.IDENT){
				lable = ident();
			}
			expected(XTokenKind.SEMICOLON);
			return new XContinue(endLineBlock(), lable);
		case RETURN:
			nextToken();
			if(token.kind==XTokenKind.SEMICOLON){
				return new XReturn(endLineBlock(), null);
			}
			statement = makeInnerStatement();
			return new XReturn(endLineBlock(), statement);
		case IF:
			nextToken();
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			block = makeSecoundStatement(true);
			if(token.kind==XTokenKind.ELSE){
				nextToken();
				block2 = makeSecoundStatement(true);
			}
			return new XIf(endLineBlock(), statement, block, block2);
		case SWITCH:
			nextToken();
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			List<XCase> cases = makeSwitchBlock();
			return new XSwitch(endLineBlock(), statement, cases);
		case DO:
			nextToken();
			block = makeSecoundStatement(true);
			expected(XTokenKind.WHILE);
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			if(needEnding)
				expected(XTokenKind.SEMICOLON);
			return new XDo(endLineBlock(), block, statement);
		case FOR:
			nextToken();
			expected(XTokenKind.LGROUP);
			boolean foreach=false;
			if(token.kind==XTokenKind.SEMICOLON){
				nextToken();
			}else{
				statement = makeStatement(false);
				if(token.kind==XTokenKind.COLON){
					nextToken();
					foreach = true;
				}else{
					expected(XTokenKind.SEMICOLON);
				}
			}
			if(foreach){
				statement2 = makeInnerStatement();
				expected(XTokenKind.RGROUP);
				block = makeStatement(needEnding);
				return new XForeach(endLineBlock(), statement, statement2, block);
			}else{
				if(token.kind!=XTokenKind.SEMICOLON){
					statement2 = makeInnerStatement();
				}
				expected(XTokenKind.SEMICOLON);
				if(token.kind!=XTokenKind.RGROUP){
					statement3 = makeInnerStatement();
				}
				expected(XTokenKind.RGROUP);
				block = makeSecoundStatement(needEnding);
				return new XFor(endLineBlock(), statement, statement2, statement3, block);
			}
		case WHILE:
			nextToken();
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			block = makeSecoundStatement(needEnding);
			return new XWhile(endLineBlock(), block, statement);
		case THROW:
			nextToken();
			statement = makeInnerStatement();
			return new XThrow(endLineBlock(), statement);
		case TRY:
			nextToken();
			List<XVarDecls> varDecls = null;
			if(token.kind==XTokenKind.LGROUP){
				nextToken();
				varDecls = new ArrayList<XTree.XVarDecls>();
				varDecls.add(makeVarDeclStatement(makeModifier(), false));
				while(token.kind==XTokenKind.SEMICOLON){
					nextToken();
					varDecls.add(makeVarDeclStatement(makeModifier(), false));
				}
				expected(XTokenKind.RGROUP);
			}
			block = makeBlock();
			List<XCatch> catchs = new ArrayList<XCatch>();
			while(token.kind==XTokenKind.CATCH){
				startLineBlock();
				nextToken();
				expected(XTokenKind.LGROUP);
				XModifier modifier = makeModifier();
				List<XType> types = new ArrayList<XType>();
				types.add(makeType());
				while(token.kind==XTokenKind.OR){
					nextToken();
					types.add(makeType());
				}
				String name = ident();
				expected(XTokenKind.RGROUP);
				block2 = makeBlock();
				catchs.add(new XCatch(endLineBlock(), modifier, types, name, block2));
			}
			if(token.kind==XTokenKind.FINALLY){
				nextToken();
				statement2 = makeBlock();
			}else if(catchs.isEmpty()){
				expected(XTokenKind.FINALLY);
			}
			if(catchs.isEmpty()){
				catchs = null;
			}
			return new XTry(endLineBlock(), varDecls, block, catchs, statement2);
		case LBRAKET:
			endLineBlock();
			return makeBlock();
		case CASE:
		case DEFAULT:
			endLineBlock();
			nextToken();
			parserMessage(XMessageLevel.ERROR, "unexpected.keyword", token.kind.name);
			return null;
		case ELSE:
		case CATCH:
		case FINALLY:
			parserMessage(XMessageLevel.ERROR, "unexpected.keyword", token.kind.name);
			nextToken();
			makeBlock();
			endLineBlock();
			return null;
		case IDENT:
		case ADD:
		case BNOT:
		case CHARLITERAL:
		case DOUBLELITERAL:
		case FALSE:
		case FLOATLITERAL:
		case INTLITERAL:
		case LGROUP:
		case LONGLITERAL:
		case NOT:
		case NULL:
		case STRINGLITERAL:
		case SUB:
		case SUPER:
		case THIS:
		case TRUE:
			endLineBlock();
			statement = makeInnerStatement();
			if(needEnding)
				expected(XTokenKind.SEMICOLON);
			return statement;
		case SEMICOLON:
			endLineBlock();
			nextToken();
			return null;
		default:
			endLineBlock();
			parserMessage(XMessageLevel.ERROR, "unexpected", token.kind.name);
			return null;
		}
	}
	
	public List<XCase> makeSwitchBlock() {
		if(expected(XTokenKind.LBRAKET)){
			List<XCase> cases = new ArrayList<XCase>();
			List<XStatement> statements = null;
			while(token.kind!=XTokenKind.RBRAKET && token.kind!=XTokenKind.EOF){
				if(token.kind==XTokenKind.CASE){
					nextToken();
					XStatement key = makeNumRead(true);
					expected(XTokenKind.COLON);
					statements = new ArrayList<XTree.XStatement>();
					cases.add(new XCase(token.lineDesk, key, statements));
				}else if(token.kind==XTokenKind.DEFAULT){
					nextToken();
					expected(XTokenKind.COLON);
					statements = new ArrayList<XTree.XStatement>();
					cases.add(new XCase(token.lineDesk, null, statements));
				}else{
					statements.add(makeStatement(true));
				}
				if(unhandledUnexpected){
					skip(false, true, true, true, false, true, true);
				}
			}
			expected(XTokenKind.RBRAKET);
			return cases;
		}
		return null;
	}

	public XStatement makeStatement(boolean needEnding){
		XToken oldtoken;
		switch(token.kind){
		case IDENT:
			lexer.notSure();
			oldtoken = token;
			startMessageBuffer();
			boolean bv = unhandledUnexpected;
			unhandledUnexpected = false;
			XType type = makeType();
			boolean knowRealy = false;
			XVarDecls decl = null;
			if(!unhandledUnexpected){
				knowRealy = type.array!=0;
				if(token.kind==XTokenKind.IDENT || knowRealy){
					knowRealy |= type.typeParam==null || type.typeParam.size()!=1;
					XLineDesk line = new XLineDesk(token.lineDesk);
					String name = ident();
					if(token.kind==XTokenKind.LINDEX){
						nextToken();
						if(token.kind==XTokenKind.RINDEX){
							nextToken();
							decl = makeVarDecls(line, new XModifier(line, 0), type, name, 1);
						}
					}else if(token.kind==XTokenKind.EQUAL || token.kind==XTokenKind.SEMICOLON){
						decl = makeVarDecls(line, new XModifier(line, 0), type, name);
					}
				}
			}
			if(knowRealy || decl!=null){
				endMessageBuffer(true);
				lexer.sure();
				unhandledUnexpected|=bv;
				if(needEnding)
					expected(XTokenKind.SEMICOLON);
				return decl;
			}else{
				endMessageBuffer(false);
				lexer.reset();
				unhandledUnexpected=bv;
				token = oldtoken;
			}
		default:
			return makeSecoundStatement(needEnding);
		}
	}

	private XStatement makeGroup() {
		startLineBlock();
		expected(XTokenKind.LGROUP);
		XStatement statement = makeInnerStatement();
		expected(XTokenKind.RGROUP);
		return new XGroup(endLineBlock(), statement);
	}

	public XBlock makeBlock(){
		startLineBlock();
		if(expected(XTokenKind.LBRAKET)){
			List<XStatement> statements = new ArrayList<XTree.XStatement>();
			while(token.kind!=XTokenKind.RBRAKET && token.kind!=XTokenKind.EOF){
				statements.add(makeStatement(true));
				if(unhandledUnexpected){
					skip(false, true, true, true, false, true, true);
				}
			}
			expected(XTokenKind.RBRAKET);
			return new XBlock(endLineBlock(), statements);
		}
		endLineBlock();
		return null;
	}
	
	public XMethodDecl makeMethodDecl(XLineDesk line, XModifier modifier, List<XTypeParam> typeParam, XType returnType, String name, boolean isInterface, boolean isConstructor){
		int m = modifier==null?0:modifier.modifier;
		Object[] p = makeParamList(!(xscript.runtime.XModifier.isAbstract(m) || xscript.runtime.XModifier.isNative(m)));
		@SuppressWarnings("unchecked")
		List<XVarDecl> paramTypes = (List<XVarDecl>) p[0];
		boolean varargs = (Boolean) p[1];
		List<XType> throwList = null;
		if(token.kind==XTokenKind.THROWS){
			nextToken();
			throwList = makeTypeList(XTokenKind.COMMA);
		}
		List<XStatement> superConstructors = null;
		if(isConstructor){
			if(token.kind==XTokenKind.COLON){
				nextToken();
				superConstructors = new ArrayList<XTree.XStatement>();
				superConstructors.add(makeStatementWithSuffixAndPrefix());
				while(token.kind==XTokenKind.COMMA){
					nextToken();
					superConstructors.add(makeStatementWithSuffixAndPrefix());
				}
			}
		}
		XBlock block = null;
		if(((isInterface && token.kind!=XTokenKind.DEFAULT) || xscript.runtime.XModifier.isAbstract(modifier.modifier) || xscript.runtime.XModifier.isNative(modifier.modifier)) && !isConstructor){
			if(!expected(XTokenKind.SEMICOLON) && token.kind==XTokenKind.LBRAKET){
				block = makeBlock();
			}
		}else{
			if(isInterface){
				nextToken();
			}
			block = makeBlock();
		}
		return new XMethodDecl(line, modifier, name, typeParam, returnType, paramTypes, throwList, block, superConstructors, varargs);
	}
	
	public XVarDecl makeVarDecl(XLineDesk line, XModifier modifier, XType type, String name, int arrayAdd){
		while(token.kind==XTokenKind.LINDEX){
			nextToken();
			expected(XTokenKind.RINDEX);
			arrayAdd++;
		}
		if(arrayAdd!=0){
			type = new XType(type.line, type.name, type.typeParam, type.array+arrayAdd);
		}
		XStatement init = null;
		if(token.kind==XTokenKind.EQUAL){
			nextToken();
			init = makeInnerStatement();
		}
		return new XVarDecl(line, modifier, name, type, init);
	}
	
	public XVarDecls makeVarDecls(XLineDesk line, XModifier modifier, XType type, String name, int arrayAdd){
		List<XVarDecl> list = new ArrayList<XVarDecl>();
		list.add(makeVarDecl(line, modifier, type, name, arrayAdd));
		while(token.kind==XTokenKind.COMMA){
			nextToken();
			XLineDesk lline = new XLineDesk(token.lineDesk);
			name = ident();
			list.add(makeVarDecl(lline, modifier, type, name, 0));
		}
		line.endLine = token.lineDesk.endLine;
		line.endLinePos = token.lineDesk.endLinePos;
		return new XVarDecls(line, list);
	}
	
	public XVarDecls makeVarDecls(XLineDesk line, XModifier modifier, XType type, String name){
		return makeVarDecls(line, modifier, type, name, 0);
	}
	
	public XTree classAndInterfaceBodyDecl(boolean isInterface, String className){
		XModifier modifier = makeModifier();
		if(token.kind==XTokenKind.CLASS || token.kind==XTokenKind.INTERFACE || token.kind==XTokenKind.ENUM || token.kind==XTokenKind.ANNOTATION){
			return classDecl(modifier);
		}
		if(token.kind==XTokenKind.LBRAKET && xscript.runtime.XModifier.isStatic(modifier.modifier)){
			if(modifier.annotations!=null){
				parserMessage(XMessageLevel.ERROR, "staticblock.noannotations", modifier.line);
			}
			if(modifier.modifier!=xscript.runtime.XModifier.STATIC){
				parserMessage(XMessageLevel.ERROR, "staticblock.wrongmodifier", modifier.line, xscript.runtime.XModifier.toString(modifier.modifier & ~xscript.runtime.XModifier.STATIC));
			}
			return makeBlock();
		}
		List<XTypeParam> typeParam = makeTypeParamList();
		XType type = makeType();
		boolean isConstructor = token.kind==XTokenKind.LGROUP && className!=null && type.name.name.equals(className);
		if(isConstructor){
			type = new XType(type.line, new XIdent(type.line, "void"), null, 0);
		}
		XLineDesk line = new XLineDesk(token.lineDesk);
		String name = isConstructor?"<init>":ident();
		if(isConstructor || token.kind==XTokenKind.LGROUP){
			return makeMethodDecl(line, modifier, typeParam, type, name, isInterface, isConstructor);
		}else{
			XTree tree = makeVarDecls(line, modifier, type, name);
			expected(XTokenKind.SEMICOLON);
			return tree;
		}
	}
	
	public List<XTree> classAndInterfaceBody(boolean isInterface, String className){
		expected(XTokenKind.LBRAKET);
		if(unhandledUnexpected){
			skip(false, false, false, false, false, false, true);
			if(token.kind==XTokenKind.LBRAKET)
				nextToken();
		}
		List<XTree> list = new ArrayList<XTree>();
		while(token.kind!=XTokenKind.EOF && token.kind!=XTokenKind.RBRAKET){
			list.add(classAndInterfaceBodyDecl(isInterface, className));
			if(unhandledUnexpected){
				skip(true, true, false, true, false, true, true);
			}
		}
		expected(XTokenKind.RBRAKET);
		return list;
	}
	
	public XClassDecl classDecl(XModifier modifier){
		startLineBlock();
		expected(XTokenKind.CLASS);
		String name = ident();
		List<XTypeParam> typeParam = makeTypeParamList();
		List<XType> superClasses = null;
		if(token.kind==XTokenKind.COLON){
			nextToken();
			superClasses = makeTypeList(XTokenKind.COMMA);
		}else if(token.kind==XTokenKind.EXTENDS || token.kind==XTokenKind.IMPLEMENTS){
			superClasses = new ArrayList<XType>();
			parserMessage(XMessageLevel.INFO, "newextends");
			if(token.kind==XTokenKind.EXTENDS){
				nextToken();
				superClasses.add(makeType());
			}
			if(token.kind==XTokenKind.IMPLEMENTS){
				nextToken();
				superClasses.addAll(makeTypeList(XTokenKind.COMMA));
			}
		}
		XLineDesk line = endLineBlock();
		List<XTree> body = classAndInterfaceBody(false, name);
		return new XClassDecl(line, modifier, name, typeParam, superClasses, body);
	}
	
	public XClassDecl interfaceDecl(XModifier modifier){
		startLineBlock();
		expected(XTokenKind.INTERFACE);
		modifier.modifier |= xscript.runtime.XModifier.ABSTRACT;
		String name = ident();
		List<XTypeParam> typeParam = makeTypeParamList();
		List<XType> superClasses = null;
		if(token.kind==XTokenKind.COLON){
			nextToken();
			superClasses = makeTypeList(XTokenKind.COMMA);
		}else if(token.kind==XTokenKind.EXTENDS){
			nextToken();
			parserMessage(XMessageLevel.INFO, "newextends");
			superClasses = makeTypeList(XTokenKind.COMMA);
		}
		XLineDesk line = endLineBlock();
		List<XTree> body = classAndInterfaceBody(true, name);
		return new XClassDecl(line, modifier, name, typeParam, superClasses, body);
	}
	
	public XNew enumConstInit(){
		startLineBlock();
		startLineBlock();
		String n = ident();
		XIdent name = new XIdent(endLineBlock(), n);
		List<XStatement> l = new ArrayList<XStatement>();
		if(token.kind==XTokenKind.LGROUP){
			l = makeMethodCallParamList();
		}
		XClassDecl cdecl = null;
		if(token.kind==XTokenKind.LBRAKET){
			startLineBlock();
			List<XTree> body = classAndInterfaceBody(false, null);
			cdecl = new XClassDecl(endLineBlock(), null, null, null, null, body);
		}
		return new XNew(endLineBlock(), new XType(name.line, name, null, 0), l, cdecl);
	}
	
	public List<XTree> enumBody(String name){
		expected(XTokenKind.LBRAKET);
		if(unhandledUnexpected){
			skip(true, true, false, true, false, true, true);
			if(token.kind==XTokenKind.LBRAKET)
				nextToken();
		}
		List<XTree> list = new ArrayList<XTree>();
		while(token.kind!=XTokenKind.SEMICOLON){
			list.add(enumConstInit());
			if(token.kind!=XTokenKind.COMMA)
				break;
			nextToken();
		}
		if(token.kind==XTokenKind.SEMICOLON){
			nextToken();
			while(token.kind!=XTokenKind.EOF && token.kind!=XTokenKind.RBRAKET){
				list.add(classAndInterfaceBodyDecl(false, name));
				if(unhandledUnexpected){
					skip(true, true, false, true, false, true, true);
				}
			}
		}
		expected(XTokenKind.RBRAKET);
		return list;
	}
	
	public XClassDecl enumDecl(XModifier modifier){
		startLineBlock();
		expected(XTokenKind.ENUM);
		modifier.modifier |= xscript.runtime.XModifier.FINAL;
		startLineBlock();
		String name = ident();
		List<XType> superClasses = new ArrayList<XTree.XType>();
		XLineDesk line = endLineBlock();
		List<XType> list = new ArrayList<XTree.XType>();
		list.add(new XType(line, new XIdent(line, name), null, 0));
		superClasses.add(new XType(line, new XIdent(line, "xscript.lang.Enum"), list, 0));
		if(token.kind==XTokenKind.COLON){
			nextToken();
			superClasses.addAll(makeTypeList(XTokenKind.COMMA));
		}else if(token.kind==XTokenKind.IMPLEMENTS){
			nextToken();
			parserMessage(XMessageLevel.INFO, "newextends");
			superClasses.addAll(makeTypeList(XTokenKind.COMMA));
		}
		line = endLineBlock();
		List<XTree> body = enumBody(name);
		return new XClassDecl(line, modifier, name, null, superClasses, body);
	}
	
	public List<XTree> annotationBody(String name){
		return null;
	}
	
	public XClassDecl annotationDecl(XModifier modifier){
		startLineBlock();
		expected(XTokenKind.ANNOTATION);
		startLineBlock();
		String name = ident();
		XLineDesk line = endLineBlock();
		List<XType> superClasses = new ArrayList<XTree.XType>();
		superClasses.add(new XType(line, new XIdent(line, "xscript.lang.Annotation"), null, 0));
		List<XTree> body = annotationBody(name);
		return new XClassDecl(line, modifier, name, null, superClasses, body);
	}
	
	public XClassDecl makeClassDecl(XModifier modifier){
		if(token.kind==XTokenKind.CLASS){
			return classDecl(modifier);
		}else if(token.kind==XTokenKind.INTERFACE){
			return interfaceDecl(modifier);
		}else if(token.kind==XTokenKind.ENUM){
			return enumDecl(modifier);
		}else if(token.kind==XTokenKind.ANNOTATION){
			return annotationDecl(modifier);
		}else{
			parserMessage(XMessageLevel.ERROR, "expected", "class, interface & enum");
			unhandledUnexpected = true;
			return null;
		}
	}
	
	public XTree makeTree(){
		startLineBlock();
		List<XTree> defs = new ArrayList<XTree>();
		List<XAnnotation> annotations = null;
		XIdent packageName = null;
		XModifier modifier = makeModifier();
		if(token.kind==XTokenKind.PACKAGE){
			nextToken();
			annotations = modifier.annotations;
			if(modifier.modifier!=0){
				parserMessage(XMessageLevel.ERROR, "modifier.not_expected", xscript.runtime.XModifier.toString(modifier.modifier));
			}
			packageName = qualident();
			expected(XTokenKind.SEMICOLON);
			modifier = null;
		}
		while(token.kind!=XTokenKind.EOF){
			if(unhandledUnexpected){
				skip(true, false, false, true, true, false, false);
			}
			if(modifier==null)
				modifier = makeModifier();
			if(unhandledUnexpected){
				modifier=null;
				continue;
			}
			if(token.kind==XTokenKind.IMPORT){
				defs.add(makeImport());
			}else{
				defs.add(makeClassDecl(modifier));
			}
			modifier = null;
		}
		return new XClassFile(endLineBlock(), annotations, packageName, defs);
	}
	
	public void nextToken(){
		for(XLineDesk line:lines){
			line.endLine = token.lineDesk.endLine;
			line.endLinePos = token.lineDesk.endLinePos;
		}
		token = lexer.getNextToken();
	}
	
	private void parserMessage(XMessageLevel level, String key, Object...args){
		parserMessage(level, key, token.lineDesk, args);
	}
	
	private void parserMessage(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		if(messageBuffer!=null){
			MessageBuffer buffer = new MessageBuffer();
			buffer.level = level;
			buffer.key = key;
			buffer.lineDesk = lineDesk;
			buffer.args = args;
			messageBuffer.add(buffer);
		}else{
			messages.postMessage(level, "parser."+key, lineDesk, args);
		}
	}
	
	private void startMessageBuffer(){
		messageBuffer = new ArrayList<XParser.MessageBuffer>();
	}
	
	private void endMessageBuffer(boolean post){
		if(post){
			for(MessageBuffer message:messageBuffer){
				messages.postMessage(message.level, "parser."+message.key, message.lineDesk, message.args);
			}
		}
		messageBuffer = null;
	}
	
	private static class MessageBuffer{
		XMessageLevel level;
		String key;
		XLineDesk lineDesk;
		Object[]args;
	}
	
}
