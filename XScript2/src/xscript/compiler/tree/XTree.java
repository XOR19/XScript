package xscript.compiler.tree;

import java.util.List;

import xscript.compiler.XConstantValue;
import xscript.compiler.XOperator;
import xscript.compiler.token.XLineDesk;

public abstract class XTree{

	public static enum XTag{
		ERROR, TOPLEVEL, IMPORT, CLASSDEF, ANNOTATION, MODIFIER, IDENT, TYPE, 
		TYPEPARAM, VARDECL, METHODDECL, BLOCK, BREAK, CONTINUE, DO, WHILE, 
		FOR, IF, RETURN, THROW, VARDECLS, GROUP, SYNCHRONIZED, CONST, METHODCALL, 
		NEW, OPERATOR, OPERATORSUFFIXPREFIX, 
		INDEX, IFOPERATOR, CAST, LAMBDA, TRY, CATCH, NEWARRAY, ARRAYINITIALIZE, 
		FOREACH, LABLE, SWITCH, CASE, THIS, SUPER;
	}
	
	public static class XError extends XTree{

		public XError(XLineDesk line) {
			super(line);
		}

		@Override
		public XTag getTag() {
			return XTag.ERROR;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitError(this);
		}
		
	}
	
	public static class XClassFile extends XTree{

		public List<XAnnotation> packAnnotations;
		
		public XIdent packID;
		
		public List<XTree> defs;
		
		public XClassFile(XLineDesk line, List<XAnnotation> packAnnotations, XIdent packID, List<XTree> defs) {
			super(line);
			this.packAnnotations = packAnnotations;
			this.packID = packID;
			this.defs = defs;
		}
		
		@Override
		public XTag getTag() {
			return XTag.TOPLEVEL;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitTopLevel(this);
		}
		
	}
	
	public static class XImport extends XTree{
		
		public String iimport;

		public boolean indirect;
		
		public boolean staticImport;
		
		public XImport(XLineDesk lineDesk, String iimport, boolean indirect, boolean staticImport) {
			super(lineDesk);
			this.iimport = iimport;
			this.indirect = indirect;
			this.staticImport = staticImport;
		}

		@Override
		public XTag getTag() {
			return XTag.IMPORT;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitImport(this);
		}
		
	}
	
	public static class XClassDecl extends XStatement{

		public XModifier modifier;
		
		public String name;
		
		public List<XTypeParam> typeParam;
		
		public List<XType> superClasses;
		
		public List<XTree> defs;
		
		public XClassDecl(XLineDesk line, XModifier modifier, String name, List<XTypeParam> typeParam, List<XType> superClasses, List<XTree> defs) {
			super(line);
			this.modifier = modifier;
			this.name = name;
			this.typeParam = typeParam;
			this.superClasses = superClasses;
			this.defs = defs;
		}
		
		@Override
		public XTag getTag() {
			return XTag.CLASSDEF;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitClassDecl(this);
		}
		
	}
	
	public static class XModifier extends XTree{
		
		public int modifier;
		
		public List<XAnnotation> annotations;
		
		public XModifier(XLineDesk line, int modifier){
			super(line);
			this.modifier = modifier;
		}
		
		public XModifier(XLineDesk line, int modifier, List<XAnnotation> annotations){
			super(line);
			this.modifier = modifier;
			this.annotations = annotations;
		}
		
		@Override
		public XTag getTag() {
			return XTag.MODIFIER;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitModifier(this);
		}
		
	}
	
	public static class XAnnotation extends XTree{

		public XAnnotation(XLineDesk line) {
			super(line);
		}

		@Override
		public XTag getTag() {
			return XTag.ANNOTATION;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitAnnotation(this);
		}
		
	}
	
	public static abstract class XExpression extends XTree{

		public XExpression(XLineDesk line) {
			super(line);
		}
		
	}
	
	public static class XIdent extends XStatement{

		public String name;
		
		public XIdent(XLineDesk line, String name) {
			super(line);
			this.name = name;
		}

		@Override
		public XTag getTag() {
			return XTag.IDENT;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitIdent(this);
		}
		
	}
	
	public static class XThis extends XStatement{
		
		public XThis(XLineDesk line) {
			super(line);
		}

		@Override
		public XTag getTag() {
			return XTag.THIS;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitThis(this);
		}
		
	}
	
	public static class XSuper extends XStatement{
		
		public XSuper(XLineDesk line) {
			super(line);
		}

		@Override
		public XTag getTag() {
			return XTag.SUPER;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitSuper(this);
		}
		
	}
	
	public static abstract class XStatement extends XTree{

		public XStatement(XLineDesk line) {
			super(line);
		}
		
	}
	
	public static class XType extends XTree{

		public XIdent name;
		
		public List<XType> typeParam;
		
		public int array;
		
		public XType(XLineDesk line, XIdent name, List<XType> typeParam, int array) {
			super(line);
			this.name = name;
			this.typeParam = typeParam;
			this.array = array;
		}

		@Override
		public XTag getTag() {
			return XTag.TYPE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitType(this);
		}
		
	}
	
	public static class XTypeParam extends XTree{
		
		public String name;
		
		public List<XType> extend;
		
		public boolean isSuper;
		
		public XTypeParam(XLineDesk line, String name, List<XType> extend, boolean isSuper) {
			super(line);
			this.name = name;
			this.extend = extend;
			this.isSuper = isSuper;
		}

		@Override
		public XTag getTag() {
			return XTag.TYPEPARAM;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitTypeParam(this);
		}
		
	}
	
	public static class XVarDecl extends XTree{

		public XModifier modifier;
		
		public String name;
		
		public XType type;

		public XStatement init;
		
		public XVarDecl(XLineDesk line, XModifier modifier, String name, XType type, XStatement init) {
			super(line);
			this.modifier = modifier;
			this.name = name;
			this.type = type;
			this.init = init;
		}
		
		@Override
		public XTag getTag() {
			return XTag.VARDECL;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitVarDecl(this);
		}
		
	}
	
	public static class XVarDecls extends XStatement{
		
		public List<XVarDecl> varDecls;
		
		public XVarDecls(XLineDesk line, List<XVarDecl> varDecls) {
			super(line);
			this.varDecls = varDecls;
		}
		
		@Override
		public XTag getTag() {
			return XTag.VARDECLS;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitVarDecls(this);
		}
		
	}
	
	public static class XMethodDecl extends XTree{

		public XModifier modifier;
		
		public String name;
		
		public List<XTypeParam> typeParam;
		
		public XType returnType;
		
		public List<XVarDecl> paramTypes;
		
		public List<XType> throwList;

		public XBlock block;
		
		public List<XStatement> superConstructors;
		
		public boolean varargs;
		
		public XMethodDecl(XLineDesk line, XModifier modifier, String name, List<XTypeParam> typeParam, XType returnType, List<XVarDecl> paramTypes, List<XType> throwList, XBlock block, List<XStatement> superConstructors, boolean varargs) {
			super(line);
			this.modifier = modifier;
			this.name = name;
			this.typeParam = typeParam;
			this.returnType = returnType;
			this.paramTypes = paramTypes;
			this.throwList = throwList;
			this.block = block;
			this.superConstructors = superConstructors;
			this.varargs = varargs;
		}
		
		@Override
		public XTag getTag() {
			return XTag.METHODDECL;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitMethodDecl(this);
		}
		
	}
	
	public static class XBlock extends XStatement{

		public List<XStatement> statements;
		
		public XBlock(XLineDesk line, List<XStatement> statements) {
			super(line);
			this.statements = statements;
		}

		@Override
		public XTag getTag() {
			return XTag.BLOCK;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitBlock(this);
		}
		
	}
	
	public static class XGroup extends XStatement{

		public XStatement statement;
		
		public XGroup(XLineDesk line, XStatement statement) {
			super(line);
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.GROUP;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitGroup(this);
		}
		
	}
	
	public static class XBreak extends XStatement{

		public String lable;
		
		public XBreak(XLineDesk line, String lable) {
			super(line);
			this.lable = lable;
		}

		@Override
		public XTag getTag() {
			return XTag.BREAK;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitBreak(this);
		}
		
	}
	
	public static class XContinue extends XStatement{

		public String lable;
		
		public XContinue(XLineDesk line, String lable) {
			super(line);
			this.lable = lable;
		}

		@Override
		public XTag getTag() {
			return XTag.CONTINUE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitContinue(this);
		}
		
	}
	
	public static class XDo extends XStatement{

		public XStatement block;
		
		public XStatement doWhile;
		
		public XDo(XLineDesk line, XStatement block, XStatement doWhile) {
			super(line);
			this.block = block;
			this.doWhile = doWhile;
		}

		@Override
		public XTag getTag() {
			return XTag.DO;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitDo(this);
		}
		
	}
	
	public static class XFor extends XStatement{

		public XStatement init;
		
		public XStatement doWhile;
		
		public XStatement inc;
		
		public XStatement block;
		
		public XFor(XLineDesk line, XStatement init, XStatement doWhile, XStatement inc, XStatement block) {
			super(line);
			this.init = init;
			this.doWhile = doWhile;
			this.inc = inc;
			this.block = block;
		}

		@Override
		public XTag getTag() {
			return XTag.FOR;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitFor(this);
		}
		
	}
	
	public static class XForeach extends XStatement{

		public XStatement var;
		
		public XStatement in;
		
		public XStatement block;
		
		public XForeach(XLineDesk line, XStatement var, XStatement in, XStatement block) {
			super(line);
			this.var = var;
			this.in = in;
			this.block = block;
		}

		@Override
		public XTag getTag() {
			return XTag.FOREACH;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitForeach(this);
		}
		
	}
	
	public static class XWhile extends XStatement{

		public XStatement block;
		
		public XStatement doWhile;
		
		public XWhile(XLineDesk line, XStatement block, XStatement doWhile) {
			super(line);
			this.block = block;
			this.doWhile = doWhile;
		}

		@Override
		public XTag getTag() {
			return XTag.WHILE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitWhile(this);
		}
		
	}
	
	public static class XIf extends XStatement{

		public XStatement iif;
		
		public XStatement block;
		
		public XStatement block2;
		
		public XIf(XLineDesk line, XStatement iif, XStatement block, XStatement block2) {
			super(line);
			this.iif = iif;
			this.block = block;
			this.block2 = block2;
		}

		@Override
		public XTag getTag() {
			return XTag.IF;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitIf(this);
		}
		
	}
	
	public static class XReturn extends XStatement{

		public XStatement statement;
		
		public XReturn(XLineDesk line, XStatement statement) {
			super(line);
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.RETURN;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitReturn(this);
		}
		
	}
	
	public static class XThrow extends XStatement{

		public XStatement statement;
		
		public XThrow(XLineDesk line, XStatement statement) {
			super(line);
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.THROW;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitThrow(this);
		}
		
	}
	
	public static class XSynchronized extends XStatement{

		public XStatement ident;
		
		public XStatement block;
		
		public XSynchronized(XLineDesk line, XStatement ident, XStatement block) {
			super(line);
			this.ident = ident;
			this.block = block;
		}

		@Override
		public XTag getTag() {
			return XTag.SYNCHRONIZED;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitSynchronized(this);
		}
		
	}
	
	public static class XConstant extends XStatement{

		public XConstantValue value;
		
		public XConstant(XLineDesk line, XConstantValue value) {
			super(line);
			this.value = value;
		}
		
		@Override
		public XTag getTag() {
			return XTag.CONST;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitConstant(this);
		}
		
	}
	
	public static class XMethodCall extends XStatement{
		
		public XStatement method;
		
		public List<XStatement> params;
		
		public List<XType> typeParam;
		
		public XMethodCall(XLineDesk line, XStatement method, List<XStatement> params, List<XType> typeParam) {
			super(line);
			this.method = method;
			this.params = params;
			this.typeParam = typeParam;
		}

		@Override
		public XTag getTag() {
			return XTag.METHODCALL;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitMethodCall(this);
		}
		
	}
	
	public static class XIndex extends XStatement{
		
		public XStatement array;
		
		public XStatement index;
		
		public XIndex(XLineDesk line, XStatement array, XStatement index) {
			super(line);
			this.array = array;
			this.index = index;
		}

		@Override
		public XTag getTag() {
			return XTag.INDEX;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitIndex(this);
		}
		
	}
	
	public static class XNew extends XStatement{
		
		public XType type;
		
		public List<XStatement> params;
		
		public XClassDecl classDecl;
		
		public XNew(XLineDesk line, XType type, List<XStatement> params, XClassDecl classDecl) {
			super(line);
			this.type = type;
			this.params = params;
			this.classDecl = classDecl;
		}

		@Override
		public XTag getTag() {
			return XTag.NEW;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitNew(this);
		}
		
	}
	
	public static class XNewArray extends XStatement{
		
		public XType type;
		
		public List<XStatement> arraySizes;
		
		public XStatement arrayInitialize;
		
		public XNewArray(XLineDesk line, XType type, List<XStatement> arraySizes, XStatement arrayInitialize) {
			super(line);
			this.type = type;
			this.arraySizes = arraySizes;
			this.arrayInitialize = arrayInitialize;
		}

		@Override
		public XTag getTag() {
			return XTag.NEWARRAY;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitNewArray(this);
		}
		
	}
	
	public static class XOperatorStatement extends XStatement{

		public XStatement left;
		
		public XStatement right;
		
		public XOperator operator;
		
		public XOperatorStatement(XLineDesk line, XStatement left, XOperator operator, XStatement right) {
			super(line);
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public XTag getTag() {
			return XTag.OPERATOR;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitOperator(this);
		}
		
	}
	
	public static class XOperatorPrefixSuffix extends XStatement{

		public List<XOperator> prefix;
		
		public XStatement statement;
		
		public List<XOperator> suffix;
		
		public XOperatorPrefixSuffix(XLineDesk line, List<XOperator> prefix, XStatement statement, List<XOperator> suffix) {
			super(line);
			this.prefix = prefix;
			this.statement = statement;
			this.suffix = suffix;
		}

		@Override
		public XTag getTag() {
			return XTag.OPERATORSUFFIXPREFIX;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitOperatorPrefixSuffix(this);
		}
		
	}
	
	public static class XIfOperator extends XOperatorStatement{
		
		public XStatement statement;
		
		public XIfOperator(XLineDesk line, XStatement left, XStatement statement, XStatement right) {
			super(line, left, XOperator.IF, right);
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.IFOPERATOR;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitIfOperator(this);
		}
		
	}
	
	public static class XCast extends XStatement{
		
		public XType type;
		
		public XStatement statement;
		
		public XCast(XLineDesk line, XType type, XStatement statement) {
			super(line);
			this.type = type;
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.CAST;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitCast(this);
		}
		
	}
	
	public static class XLambda extends XStatement{

		public List<XVarDecl> params;
		
		public XStatement statement;
		
		public XLambda(XLineDesk line, List<XVarDecl> params, XStatement statement) {
			super(line);
			this.params = params;
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.LAMBDA;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitLambda(this);
		}
		
	}
	
	public static class XTry extends XStatement{
		
		public XStatement resource;
		
		public XStatement block;
		
		public List<XCatch> catchs;
		
		public XStatement finallyBlock;
		
		public XTry(XLineDesk line, XStatement resource, XStatement block, List<XCatch> catchs, XStatement finallyBlock) {
			super(line);
			this.resource = resource;
			this.block = block;
			this.catchs = catchs;
			this.finallyBlock = finallyBlock;
		}

		@Override
		public XTag getTag() {
			return XTag.TRY;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitTry(this);
		}
		
	}
	
	public static class XCatch extends XTree{

		public XModifier modifier;
		
		public List<XType> types;
		
		public String varName;
		
		public XStatement block;
		
		public XCatch(XLineDesk line, XModifier modifier, List<XType> types, String varName, XStatement block) {
			super(line);
			this.modifier = modifier;
			this.types = types;
			this.varName = varName;
			this.block = block;
		}

		@Override
		public XTag getTag() {
			return XTag.CATCH;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitCatch(this);
		}
		
	}
	
	public static class XArrayInitialize extends XStatement{

		public List<XStatement> statements;
		
		public XArrayInitialize(XLineDesk line, List<XStatement> statements) {
			super(line);
			this.statements = statements;
		}

		@Override
		public XTag getTag() {
			return XTag.ARRAYINITIALIZE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitArrayInitialize(this);
		}
		
	}
	
	public static class XLable extends XStatement{

		public String name;
		
		public XStatement statement;
		
		public XLable(XLineDesk line, String name) {
			super(line);
			this.name = name;
		}

		@Override
		public XTag getTag() {
			return XTag.LABLE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitLable(this);
		}
		
	}
	
	public static class XSwitch extends XStatement{

		public XStatement statement;
		
		public List<XCase> cases;
		
		public XSwitch(XLineDesk line, XStatement statement, List<XCase> cases) {
			super(line);
			this.statement = statement;
			this.cases = cases;
		}

		@Override
		public XTag getTag() {
			return XTag.SWITCH;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitSwitch(this);
		}
		
	}
	
	public static class XCase extends XTree{

		public XStatement key;
		
		public List<XStatement> block;
		
		public XCase(XLineDesk line, XStatement key, List<XStatement> block) {
			super(line);
		}
		
		@Override
		public XTag getTag() {
			return XTag.CASE;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitCase(this);
		}
		
	}
	
	public XLineDesk line;
	
	public XTree(XLineDesk line){
		this.line = line;
	}
	
	public abstract XTag getTag();
	
	public boolean hasTag(XTag tag){
		return tag==getTag();
	}
	
	public abstract void accept(XVisitor v);
	
}
