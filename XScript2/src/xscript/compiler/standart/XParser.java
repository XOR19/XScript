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
import xscript.compiler.tree.XTree.XTreeAnnotation;
import xscript.compiler.tree.XTree.XTreeArrayInitialize;
import xscript.compiler.tree.XTree.XTreeAssert;
import xscript.compiler.tree.XTree.XTreeBlock;
import xscript.compiler.tree.XTree.XTreeBreak;
import xscript.compiler.tree.XTree.XTreeCase;
import xscript.compiler.tree.XTree.XTreeCast;
import xscript.compiler.tree.XTree.XTreeCatch;
import xscript.compiler.tree.XTree.XTreeClassDecl;
import xscript.compiler.tree.XTree.XTreeClassFile;
import xscript.compiler.tree.XTree.XTreeConstant;
import xscript.compiler.tree.XTree.XTreeContinue;
import xscript.compiler.tree.XTree.XTreeDo;
import xscript.compiler.tree.XTree.XTreeFor;
import xscript.compiler.tree.XTree.XTreeForeach;
import xscript.compiler.tree.XTree.XTreeGroup;
import xscript.compiler.tree.XTree.XTreeIdent;
import xscript.compiler.tree.XTree.XTreeIf;
import xscript.compiler.tree.XTree.XTreeIfOperator;
import xscript.compiler.tree.XTree.XTreeImport;
import xscript.compiler.tree.XTree.XTreeIndex;
import xscript.compiler.tree.XTree.XTreeInstanceof;
import xscript.compiler.tree.XTree.XTreeLable;
import xscript.compiler.tree.XTree.XTreeLambda;
import xscript.compiler.tree.XTree.XTreeMethodCall;
import xscript.compiler.tree.XTree.XTreeMethodDecl;
import xscript.compiler.tree.XTree.XTreeModifier;
import xscript.compiler.tree.XTree.XTreeNew;
import xscript.compiler.tree.XTree.XTreeNewArray;
import xscript.compiler.tree.XTree.XTreeOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XTreeOperatorStatement;
import xscript.compiler.tree.XTree.XTreeReturn;
import xscript.compiler.tree.XTree.XTreeStatement;
import xscript.compiler.tree.XTree.XTreeSuper;
import xscript.compiler.tree.XTree.XTreeSwitch;
import xscript.compiler.tree.XTree.XTreeSynchronized;
import xscript.compiler.tree.XTree.XTreeThis;
import xscript.compiler.tree.XTree.XTreeThrow;
import xscript.compiler.tree.XTree.XTreeTry;
import xscript.compiler.tree.XTree.XTreeType;
import xscript.compiler.tree.XTree.XTreeTypeParam;
import xscript.compiler.tree.XTree.XTreeVarDecl;
import xscript.compiler.tree.XTree.XTreeVarDecls;
import xscript.compiler.tree.XTree.XTreeWhile;

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
	
	public XTreeAnnotation makeAnnotation(){
		return null;
	}
	
	public XTreeModifier makeModifier(){
		List<XTreeAnnotation> annotations = null;
		int modifier = 0;
		int m;
		startLineBlock();
		while((m=getModifier())!=0 || token.kind==XTokenKind.AT){
			if(token.kind==XTokenKind.AT){
				if(annotations==null)
					annotations = new ArrayList<XTree.XTreeAnnotation>();
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
		return new XTreeModifier(endLineBlock(), modifier, annotations);
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
	
	public XTreeIdent makeIdent(){
		return new XTreeIdent(token.lineDesk, ident());
	}
	
	public XTreeIdent qualident(){
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
		return new XTreeIdent(endLineBlock(), name);
	}
	
	public XTreeImport makeImport(){
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
			expected(XTokenKind.SEMICOLON);
			return new XTreeImport(endLineBlock(), name, indirect, staticImport);
		}
		endLineBlock();
		return new XTreeImport(token.lineDesk, "!error!", false, false);
	}
	
	public XTreeType makeType(){
		return makeType(true);
	}
	
	public XTreeType makeType(boolean beArray){
		startLineBlock();
		XTreeIdent name;
		List<XTreeType> typeParam = null;
		if(token.kind==XTokenKind.BOOL || token.kind==XTokenKind.BYTE || token.kind==XTokenKind.SHORT ||
				token.kind==XTokenKind.CHAR || token.kind==XTokenKind.INT || token.kind==XTokenKind.LONG 
				|| token.kind==XTokenKind.FLOAT || token.kind==XTokenKind.DOUBLE || token.kind==XTokenKind.VOID){
			name = new XTreeIdent(new XLineDesk(token.lineDesk), token.kind.name);
			nextToken();
		}else{
			name = qualident();
			if(token.kind==XTokenKind.SMALLER){
				nextToken();
				typeParam = new ArrayList<XTree.XTreeType>();
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
		return new XTreeType(endLineBlock(), name, typeParam, array);
	}
	
	public List<XTreeType> makeTypeList(XTokenKind split){
		List<XTreeType> list = new ArrayList<XTree.XTreeType>();
		list.add(makeType());
		while(token.kind==split){
			nextToken();
			list.add(makeType());
		}
		return list;
	}
	
	public XTreeTypeParam makeTypeParam(){
		startLineBlock();
		String name = ident();
		boolean isSuper = false;
		List<XTreeType> extend = null;
		if(token.kind==XTokenKind.EXTENDS || token.kind==XTokenKind.COLON){
			nextToken();
			extend = makeTypeList(XTokenKind.AND);
		}else if(token.kind==XTokenKind.SUPER){
			isSuper = true;
			nextToken();
			extend = makeTypeList(XTokenKind.AND);
		}
		return new XTreeTypeParam(endLineBlock(), name, extend, isSuper);
	}
	
	public List<XTreeTypeParam> makeTypeParamList(){
		List<XTreeTypeParam> list = null;
		if(token.kind == XTokenKind.SMALLER){
			nextToken();
			list = new ArrayList<XTreeTypeParam>();
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
		XTreeModifier modifier = makeModifier();
		XTreeType type = makeType();
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
		List<XTreeVarDecl> list = new ArrayList<XTree.XTreeVarDecl>();
		boolean varargs = false;
		if(token.kind!=XTokenKind.RGROUP){
			Object[] args = makeParamDecl(needName);
			list.add((XTreeVarDecl)args[0]);
			varargs = (Boolean) args[1];
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				if(varargs)
					parserMessage(XMessageLevel.ERROR, "noparam.after.varargs");
				args = makeParamDecl(needName);
				list.add((XTreeVarDecl)args[0]);
				varargs = (Boolean) args[1];
			}
		}
		expected(XTokenKind.RGROUP);
		return new Object[]{list, varargs};
	}
	
	public XTreeVarDecls makeVarDeclStatement(XTreeModifier modifier, boolean needEnding){
		XTreeType type = makeType();
		XLineDesk line = new XLineDesk(token.lineDesk);
		String name = ident();
		XTreeVarDecls varDecl = makeVarDecls(line, modifier, type, name);
		if(needEnding)
			expected(XTokenKind.SEMICOLON);
		return varDecl;
	}
	
	public XTreeStatement makeDeclStatement(boolean needEnding){
		XTreeModifier modifier = makeModifier();
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
	
	public List<XTreeStatement> makeMethodCallParamList() {
		expected(XTokenKind.LGROUP);
		List<XTreeStatement> list = null;
		if(token.kind!=XTokenKind.RGROUP){
			list = new ArrayList<XTree.XTreeStatement>();
			list.add(makeInnerStatement());
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				list.add(makeInnerStatement());
			}
		}
		expected(XTokenKind.RGROUP);
		return list;
	}
	
	public XTreeVarDecl makeLambdaParamDecl(){
		startLineBlock();
		XTreeType type = makeType();
		XTreeVarDecl varDecl;
		if(token.kind==XTokenKind.IDENT || type.array!=0 || type.typeParam!=null || type.name.name.indexOf('.')!=-1){
			endLineBlock();
			varDecl = new XTreeVarDecl(endLineBlock(), null, ident(), type, null);
		}else{
			endLineBlock();
			varDecl = new XTreeVarDecl(type.line, null, type.name.name, null, null);
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
		XTreeIdent name = null;
		List<XTreeType> typeParam = null;
		int array = 0;
		List<XTreeVarDecl> varDecls = null;
		if(expected(XTokenKind.LGROUP)){
			switch(token.kind){
			case IDENT:
				startLineBlock();
				name = qualident();
				if(token.kind==XTokenKind.SMALLER){
					nextToken();
					typeParam = new ArrayList<XTreeType>();
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
					varDecls = new ArrayList<XTreeVarDecl>();
					if(token.kind==XTokenKind.IDENT){
						varDecls.add(new XTreeVarDecl(endLineBlock(), null, ident(), new XTreeType(name.line, name, typeParam, array), null));
					}else{
						endLineBlock();
						varDecls.add(new XTreeVarDecl(name.line, null, name.name, null, null));
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
				name = new XTreeIdent(new XLineDesk(token.lineDesk), token.kind.name);
				nextToken();
				break;
			case RGROUP:
				varDecls = new ArrayList<XTreeVarDecl>();
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
				return new Object[]{knowRealy, new XTreeType(endLineBlock(), name, typeParam, array)};
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
	
	public XTreeStatement makeArrayInitialize(){
		startLineBlock();
		expected(XTokenKind.LBRAKET);
		List<XTreeStatement> statements = null;
		if(token.kind!=XTokenKind.RBRAKET){
			statements = new ArrayList<XTreeStatement>();
			statements.add(makeInnerStatement());
			while(token.kind==XTokenKind.COMMA){
				statements.add(makeInnerStatement());
			}
		}
		expected(XTokenKind.RBRAKET);
		return new XTreeArrayInitialize(endLineBlock(), statements);
	}
	
	@SuppressWarnings("unchecked")
	public XTreeStatement makeNumRead(boolean b){
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
					if(ret[1] instanceof XTreeType){
						XTreeStatement s = makeStatementWithSuffixAndPrefix();
						return new XTreeCast(endLineBlock(), (XTreeType) ret[1], s);
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
						XTreeStatement s = makeSecoundStatement(false);
						return new XTreeLambda(endLineBlock(), (List<XTreeVarDecl>)ret[1], s);
					}
				}else{
					XTreeType type = (XTreeType)ret[1];
					if(token.kind==XTokenKind.SUB){
						lexer.notSure();
						XToken oldT = token;
						nextToken();
						if(token.kind==XTokenKind.GREATER && !token.space){
							lexer.sure();
							nextToken();
							XTreeStatement s = makeSecoundStatement(false);
							List<XTreeVarDecl> l = new ArrayList<XTreeVarDecl>();
							l.add(new XTreeVarDecl(type.line, null, type.name.name, null, null));
							return new XTreeLambda(endLineBlock(), l, s);
						}
						lexer.reset();
						token = oldT;
					}
					boolean error = unhandledUnexpected;
					unhandledUnexpected = false;
					startMessageBuffer();
					XToken oldT = token;
					lexer.notSure();
					XTreeStatement s = makeStatementWithSuffixAndPrefix();
					if(unhandledUnexpected){
						endMessageBuffer(false);
						lexer.reset();
						token = oldT;
						endLineBlock();
						unhandledUnexpected = error;
						return new XTreeGroup(type.line, type.name);
					}else{
						endMessageBuffer(true);
						lexer.sure();
						unhandledUnexpected |= error;
						return new XTreeCast(endLineBlock(), type, s);
					}
				}
			}
		}case IDENT:{
			startLineBlock();
			lexer.notSure();
			startMessageBuffer();
			boolean error = unhandledUnexpected;
			unhandledUnexpected = false;
			XTreeType type = makeType();
			if(unhandledUnexpected){
				token = oldToken;
				endMessageBuffer(false);
				unhandledUnexpected = error;
				lexer.reset();
				XTreeIdent ident = makeIdent();
				if(token.kind==XTokenKind.SUB){
					XToken oldT = token;
					lexer.notSure();
					nextToken();
					if(token.kind==XTokenKind.GREATER && !token.space){
						lexer.sure();
						nextToken();
						XTreeStatement s = makeSecoundStatement(false);
						List<XTreeVarDecl> l = new ArrayList<XTreeVarDecl>();
						l.add(new XTreeVarDecl(ident.line, null, ident.name, null, null));
						return new XTreeLambda(endLineBlock(), l, s);
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
					XTreeStatement s = makeSecoundStatement(false);
					List<XTreeVarDecl> l = new ArrayList<XTreeVarDecl>();
					l.add(new XTreeVarDecl(type.name.line, null, type.name.name, null, null));
					return new XTreeLambda(endLineBlock(), l, s);
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
			return new XTreeConstant(oldToken.lineDesk, oldToken.param);
		case TRUE:
			nextToken();
			return new XTreeConstant(oldToken.lineDesk, new XConstantValue(true));
		case FALSE:
			nextToken();
			return new XTreeConstant(oldToken.lineDesk, new XConstantValue(false));
		case NULL:
			nextToken();
			return new XTreeConstant(oldToken.lineDesk, new XConstantValue(null));
		case LBRAKET:
			return makeArrayInitialize();
		case NEW:
			startLineBlock();
			nextToken();
			XTreeType type = makeType(false);
			if(token.kind==XTokenKind.LINDEX){
				List<XTreeStatement> l = new ArrayList<XTreeStatement>();
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
				XTreeStatement arrayInitialize = null;
				if(token.kind==XTokenKind.LBRAKET){
					arrayInitialize = makeArrayInitialize();
				}
				return new XTreeNewArray(endLineBlock(), type, l, arrayInitialize);
			}else{
				List<XTreeStatement> params = makeMethodCallParamList();
				XTreeClassDecl classDecl = null;
				if(token.kind==XTokenKind.LBRAKET){
					startLineBlock();
					List<XTree> body = classAndInterfaceBody(false, null);
					List<XTreeType> superClasses = new ArrayList<XTree.XTreeType>();
					superClasses.add(type);
					classDecl = new XTreeClassDecl(endLineBlock(), null, null, null, superClasses, body);
				}
				return new XTreeNew(endLineBlock(), type, params, classDecl);
			}
		case THIS:
			nextToken();
			return new XTreeThis(oldToken.lineDesk);
		case SUPER:
			nextToken();
			return new XTreeSuper(oldToken.lineDesk);
		default:
			if(b)
				parserMessage(XMessageLevel.ERROR, "unexpected", token.kind.name);
			return null;
		}
	}
	
	public XTreeStatement makeStatementWithSuffixAndPrefix(){
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
		List<XTreeType> typeParam = null;
		if(token.kind==XTokenKind.SMALLER){
			nextToken();
			typeParam = new ArrayList<XTree.XTreeType>();
			typeParam.add(makeType());
			while(token.kind==XTokenKind.COMMA){
				nextToken();
				typeParam.add(makeType());
			}
			expected(XTokenKind.GREATER);
		}
		XTreeStatement statement = makeNumRead(true);
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
			statement = new XTreeOperatorPrefixSuffix(endLineBlock(), null, statement, suffix);
		}
		while(token.kind==XTokenKind.LGROUP || token.kind==XTokenKind.LINDEX || token.kind==XTokenKind.ELEMENT || token.kind==XTokenKind.INSTANCEOF || typeParam!=null){
			startLineBlock();
			startLineBlock();
			if(token.kind==XTokenKind.LGROUP || typeParam!=null){
				List<XTreeStatement> list = makeMethodCallParamList();
				statement = new XTreeMethodCall(endLineBlock(), statement, list, typeParam);
			}else if(token.kind==XTokenKind.LINDEX){
				nextToken();
				XTreeStatement index = makeInnerStatement();
				expected(XTokenKind.RINDEX);
				statement = new XTreeIndex(endLineBlock(), statement, index);
			}else if(token.kind==XTokenKind.ELEMENT){
				XToken t = token;
				nextToken();
				XTreeIdent ident = null;
				if(token.kind==XTokenKind.CLASS){
					ident = new XTreeIdent(token.lineDesk, "class");
					nextToken();
				}else if(token.kind==XTokenKind.THIS){
					ident = new XTreeIdent(token.lineDesk, "this");
					nextToken();
				}else if(token.kind==XTokenKind.NEW){
					XTreeNew n = (XTreeNew) makeNumRead(true);
					n.element = statement;
					statement = n;
				}else{
					ident = makeIdent();
				}
				if(ident!=null){
					statement = new XTreeOperatorStatement(t.lineDesk, statement, XOperator.ELEMENT, ident);
				}
			}else if(token.kind==XTokenKind.INSTANCEOF){
				XToken t = token;
				nextToken();
				XTreeType type = makeType();
				statement = new XTreeInstanceof(t.lineDesk, statement, type);
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
				statement = new XTreeOperatorPrefixSuffix(endLineBlock(), null, statement, suffix);
			}
			typeParam = null;
			if(token.kind==XTokenKind.SMALLER){
				nextToken();
				typeParam = new ArrayList<XTree.XTreeType>();
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
		}else if(statement instanceof XTreeOperatorPrefixSuffix){
			endLineBlock();
			((XTreeOperatorPrefixSuffix) statement).prefix = prefix;
		}else{
			statement = new XTreeOperatorPrefixSuffix(endLineBlock(), prefix, statement, null);
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
	
	public XTreeStatement mergeStatements(XLineDesk line, XTreeStatement left, XOperator o, XTreeStatement right, XTreeStatement between){
		if(left instanceof XTreeOperatorStatement){
			XTreeOperatorStatement oLeft = (XTreeOperatorStatement) left;
			if(oLeft.operator.priority<o.priority || (oLeft.operator.priority==o.priority && !XOperator.L2R[o.priority])){
				oLeft.right = mergeStatements(line, oLeft.right, o, right, between);
				return oLeft;
			}else{
				if(o==XOperator.IF)
					return new XTreeIfOperator(line, left, between, right);
				return new XTreeOperatorStatement(line, left, o, right);
			}
		}else{
			if(o==XOperator.IF)
				return new XTreeIfOperator(line, left, between, right);
			return new XTreeOperatorStatement(line, left, o, right);
		}
	}
	
	public XTreeStatement makeInnerStatement(){
		XTreeStatement statement = makeStatementWithSuffixAndPrefix();
		XTreeLable lable = null;
		if(token.kind==XTokenKind.COLON && statement instanceof XTreeIdent && ((XTreeIdent)statement).name.indexOf('.')==-1){
			lable = new XTreeLable(statement.line, ((XTreeIdent)statement).name);
			nextToken();
			statement = makeStatementWithSuffixAndPrefix();
		}
		XTreeStatement between = null;
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
	
	public XTreeStatement makeSecoundStatement(boolean needEnding){
		XTreeStatement statement = null;
		XTreeStatement statement2 = null;
		XTreeStatement statement3 = null;
		String lable = null;
		XTreeStatement block = null;
		XTreeStatement block2 = null;
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
				return new XTreeSynchronized(endLineBlock(), statement, block);
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
			return new XTreeBreak(endLineBlock(), lable);
		case CONTINUE:
			nextToken();
			if(token.kind==XTokenKind.IDENT){
				lable = ident();
			}
			expected(XTokenKind.SEMICOLON);
			return new XTreeContinue(endLineBlock(), lable);
		case RETURN:
			nextToken();
			if(token.kind==XTokenKind.SEMICOLON){
				return new XTreeReturn(endLineBlock(), null);
			}
			statement = makeInnerStatement();
			return new XTreeReturn(endLineBlock(), statement);
		case ASSERT:
			nextToken();
			statement = makeInnerStatement();
			return new XTreeAssert(endLineBlock(), statement);
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
			return new XTreeIf(endLineBlock(), statement, block, block2);
		case SWITCH:
			nextToken();
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			List<XTreeCase> cases = makeSwitchBlock();
			return new XTreeSwitch(endLineBlock(), statement, cases);
		case DO:
			nextToken();
			block = makeSecoundStatement(true);
			expected(XTokenKind.WHILE);
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			if(needEnding)
				expected(XTokenKind.SEMICOLON);
			return new XTreeDo(endLineBlock(), block, statement);
		case FOR:
			nextToken();
			expected(XTokenKind.LGROUP);
			boolean foreach=false;
			if(token.kind==XTokenKind.SEMICOLON){
				nextToken();
			}else{
				statement = makeStatement(false, XTokenKind.COLON);
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
				block = makeStatement(needEnding, XTokenKind.SEMICOLON);
				return new XTreeForeach(endLineBlock(), statement, statement2, block);
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
				return new XTreeFor(endLineBlock(), statement, statement2, statement3, block);
			}
		case WHILE:
			nextToken();
			expected(XTokenKind.LGROUP);
			statement = makeInnerStatement();
			expected(XTokenKind.RGROUP);
			block = makeSecoundStatement(needEnding);
			return new XTreeWhile(endLineBlock(), block, statement);
		case THROW:
			nextToken();
			statement = makeInnerStatement();
			return new XTreeThrow(endLineBlock(), statement);
		case TRY:
			nextToken();
			List<XTreeVarDecls> varDecls = null;
			if(token.kind==XTokenKind.LGROUP){
				nextToken();
				varDecls = new ArrayList<XTree.XTreeVarDecls>();
				varDecls.add(makeVarDeclStatement(makeModifier(), false));
				while(token.kind==XTokenKind.SEMICOLON){
					nextToken();
					varDecls.add(makeVarDeclStatement(makeModifier(), false));
				}
				expected(XTokenKind.RGROUP);
			}
			block = makeBlock();
			List<XTreeCatch> catchs = new ArrayList<XTreeCatch>();
			while(token.kind==XTokenKind.CATCH){
				startLineBlock();
				nextToken();
				expected(XTokenKind.LGROUP);
				XTreeModifier modifier = makeModifier();
				List<XTreeType> types = new ArrayList<XTreeType>();
				types.add(makeType());
				while(token.kind==XTokenKind.OR){
					nextToken();
					types.add(makeType());
				}
				String name = ident();
				expected(XTokenKind.RGROUP);
				block2 = makeBlock();
				catchs.add(new XTreeCatch(endLineBlock(), modifier, types, name, block2));
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
			return new XTreeTry(endLineBlock(), varDecls, block, catchs, statement2);
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
		case NEW:
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
			nextToken();
			unhandledUnexpected = true;
			return null;
		}
	}
	
	public List<XTreeCase> makeSwitchBlock() {
		if(expected(XTokenKind.LBRAKET)){
			List<XTreeCase> cases = new ArrayList<XTreeCase>();
			List<XTreeStatement> statements = null;
			while(token.kind!=XTokenKind.RBRAKET && token.kind!=XTokenKind.EOF){
				if(token.kind==XTokenKind.CASE){
					nextToken();
					XTreeStatement key = makeNumRead(true);
					expected(XTokenKind.COLON);
					statements = new ArrayList<XTree.XTreeStatement>();
					cases.add(new XTreeCase(token.lineDesk, key, statements));
				}else if(token.kind==XTokenKind.DEFAULT){
					nextToken();
					expected(XTokenKind.COLON);
					statements = new ArrayList<XTree.XTreeStatement>();
					cases.add(new XTreeCase(token.lineDesk, null, statements));
				}else{
					statements.add(makeStatement(true, XTokenKind.SEMICOLON));
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

	public XTreeStatement makeStatement(boolean needEnding, XTokenKind ending){
		XToken oldtoken;
		switch(token.kind){
		case IDENT:
			lexer.notSure();
			oldtoken = token;
			startMessageBuffer();
			boolean bv = unhandledUnexpected;
			unhandledUnexpected = false;
			XTreeType type = makeType();
			boolean knowRealy = false;
			XTreeVarDecls decl = null;
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
							decl = makeVarDecls(line, new XTreeModifier(line, 0), type, name, 1);
						}
					}else if(token.kind==XTokenKind.EQUAL || token.kind==ending){
						decl = makeVarDecls(line, new XTreeModifier(line, 0), type, name);
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

	private XTreeStatement makeGroup() {
		startLineBlock();
		expected(XTokenKind.LGROUP);
		XTreeStatement statement = makeInnerStatement();
		expected(XTokenKind.RGROUP);
		return new XTreeGroup(endLineBlock(), statement);
	}

	public XTreeBlock makeBlock(){
		startLineBlock();
		if(expected(XTokenKind.LBRAKET)){
			List<XTreeStatement> statements = new ArrayList<XTree.XTreeStatement>();
			while(token.kind!=XTokenKind.RBRAKET && token.kind!=XTokenKind.EOF){
				statements.add(makeStatement(true, XTokenKind.SEMICOLON));
				if(unhandledUnexpected){
					skip(false, true, true, true, false, true, true);
				}
			}
			expected(XTokenKind.RBRAKET);
			return new XTreeBlock(endLineBlock(), statements);
		}
		endLineBlock();
		return null;
	}
	
	public XTreeMethodDecl makeMethodDecl(XLineDesk line, XTreeModifier modifier, List<XTreeTypeParam> typeParam, XTreeType returnType, String name, boolean isInterface, boolean isConstructor){
		int m = modifier==null?0:modifier.modifier;
		Object[] p = makeParamList(!(xscript.runtime.XModifier.isAbstract(m) || xscript.runtime.XModifier.isNative(m)));
		@SuppressWarnings("unchecked")
		List<XTreeVarDecl> paramTypes = (List<XTreeVarDecl>) p[0];
		boolean varargs = (Boolean) p[1];
		List<XTreeType> throwList = null;
		if(token.kind==XTokenKind.THROWS){
			nextToken();
			throwList = makeTypeList(XTokenKind.COMMA);
		}
		List<XTreeStatement> superConstructors = null;
		if(isConstructor){
			if(token.kind==XTokenKind.COLON){
				nextToken();
				superConstructors = new ArrayList<XTree.XTreeStatement>();
				superConstructors.add(makeStatementWithSuffixAndPrefix());
				while(token.kind==XTokenKind.COMMA){
					nextToken();
					superConstructors.add(makeStatementWithSuffixAndPrefix());
				}
			}
		}
		XTreeBlock block = null;
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
		return new XTreeMethodDecl(line, modifier, name, typeParam, returnType, paramTypes, throwList, block, superConstructors, varargs);
	}
	
	public XTreeVarDecl makeVarDecl(XLineDesk line, XTreeModifier modifier, XTreeType type, String name, int arrayAdd){
		while(token.kind==XTokenKind.LINDEX){
			nextToken();
			expected(XTokenKind.RINDEX);
			arrayAdd++;
		}
		if(arrayAdd!=0){
			type = new XTreeType(type.line, type.name, type.typeParam, type.array+arrayAdd);
		}
		XTreeStatement init = null;
		if(token.kind==XTokenKind.EQUAL){
			nextToken();
			init = makeInnerStatement();
		}
		return new XTreeVarDecl(line, modifier, name, type, init);
	}
	
	public XTreeVarDecls makeVarDecls(XLineDesk line, XTreeModifier modifier, XTreeType type, String name, int arrayAdd){
		List<XTreeVarDecl> list = new ArrayList<XTreeVarDecl>();
		list.add(makeVarDecl(line, modifier, type, name, arrayAdd));
		while(token.kind==XTokenKind.COMMA){
			nextToken();
			XLineDesk lline = new XLineDesk(token.lineDesk);
			name = ident();
			list.add(makeVarDecl(lline, modifier, type, name, 0));
		}
		line.endLine = token.lineDesk.endLine;
		line.endLinePos = token.lineDesk.endLinePos;
		return new XTreeVarDecls(line, list);
	}
	
	public XTreeVarDecls makeVarDecls(XLineDesk line, XTreeModifier modifier, XTreeType type, String name){
		return makeVarDecls(line, modifier, type, name, 0);
	}
	
	public XTree classAndInterfaceBodyDecl(boolean isInterface, String className){
		XTreeModifier modifier = makeModifier();
		if(token.kind==XTokenKind.CLASS || token.kind==XTokenKind.INTERFACE || token.kind==XTokenKind.ENUM || token.kind==XTokenKind.ANNOTATION){
			return makeClassDecl(modifier);
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
		List<XTreeTypeParam> typeParam = makeTypeParamList();
		XTreeType type = makeType();
		boolean isConstructor = token.kind==XTokenKind.LGROUP && className!=null && type.name.name.equals(className);
		if(isConstructor){
			type = new XTreeType(type.line, new XTreeIdent(type.line, "void"), null, 0);
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
	
	public XTreeClassDecl classDecl(XTreeModifier modifier){
		startLineBlock();
		expected(XTokenKind.CLASS);
		String name = ident();
		List<XTreeTypeParam> typeParam = makeTypeParamList();
		List<XTreeType> superClasses = null;
		if(token.kind==XTokenKind.COLON){
			nextToken();
			superClasses = makeTypeList(XTokenKind.COMMA);
		}else if(token.kind==XTokenKind.EXTENDS || token.kind==XTokenKind.IMPLEMENTS){
			superClasses = new ArrayList<XTreeType>();
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
		return new XTreeClassDecl(line, modifier, name, typeParam, superClasses, body);
	}
	
	public XTreeClassDecl interfaceDecl(XTreeModifier modifier){
		startLineBlock();
		expected(XTokenKind.INTERFACE);
		modifier.modifier |= xscript.runtime.XModifier.ABSTRACT;
		String name = ident();
		List<XTreeTypeParam> typeParam = makeTypeParamList();
		List<XTreeType> superClasses = null;
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
		return new XTreeClassDecl(line, modifier, name, typeParam, superClasses, body);
	}
	
	public XTreeNew enumConstInit(){
		startLineBlock();
		startLineBlock();
		String n = ident();
		XTreeIdent name = new XTreeIdent(endLineBlock(), n);
		List<XTreeStatement> l = new ArrayList<XTreeStatement>();
		if(token.kind==XTokenKind.LGROUP){
			l = makeMethodCallParamList();
		}
		XTreeClassDecl cdecl = null;
		if(token.kind==XTokenKind.LBRAKET){
			startLineBlock();
			List<XTree> body = classAndInterfaceBody(false, null);
			cdecl = new XTreeClassDecl(endLineBlock(), null, null, null, null, body);
		}
		return new XTreeNew(endLineBlock(), new XTreeType(name.line, name, null, 0), l, cdecl);
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
	
	public XTreeClassDecl enumDecl(XTreeModifier modifier){
		startLineBlock();
		expected(XTokenKind.ENUM);
		modifier.modifier |= xscript.runtime.XModifier.FINAL;
		startLineBlock();
		String name = ident();
		List<XTreeType> superClasses = new ArrayList<XTree.XTreeType>();
		XLineDesk line = endLineBlock();
		List<XTreeType> list = new ArrayList<XTree.XTreeType>();
		list.add(new XTreeType(line, new XTreeIdent(line, name), null, 0));
		superClasses.add(new XTreeType(line, new XTreeIdent(line, "xscript.lang.Enum"), list, 0));
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
		return new XTreeClassDecl(line, modifier, name, null, superClasses, body);
	}
	
	public List<XTree> annotationBody(String name){
		return null;
	}
	
	public XTreeClassDecl annotationDecl(XTreeModifier modifier){
		startLineBlock();
		expected(XTokenKind.ANNOTATION);
		startLineBlock();
		String name = ident();
		XLineDesk line = endLineBlock();
		List<XTreeType> superClasses = new ArrayList<XTree.XTreeType>();
		superClasses.add(new XTreeType(line, new XTreeIdent(line, "xscript.lang.Annotation"), null, 0));
		List<XTree> body = annotationBody(name);
		return new XTreeClassDecl(line, modifier, name, null, superClasses, body);
	}
	
	public XTreeClassDecl makeClassDecl(XTreeModifier modifier){
		if(token.kind==XTokenKind.CLASS){
			return classDecl(modifier);
		}else if(token.kind==XTokenKind.INTERFACE){
			return interfaceDecl(modifier);
		}else if(token.kind==XTokenKind.ENUM){
			return enumDecl(modifier);
		}else if(token.kind==XTokenKind.ANNOTATION){
			return annotationDecl(modifier);
		}else{
			parserMessage(XMessageLevel.ERROR, "expected", "class, interface & enum", token.kind);
			unhandledUnexpected = true;
			return null;
		}
	}
	
	public XTree makeTree(){
		startLineBlock();
		List<XTree> defs = new ArrayList<XTree>();
		List<XTreeAnnotation> annotations = null;
		XTreeIdent packageName = null;
		XTreeModifier modifier = makeModifier();
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
		return new XTreeClassFile(endLineBlock(), annotations, packageName, defs);
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
