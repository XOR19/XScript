package xscript.compiler.tree;

import java.util.List;

import xscript.compiler.XCodeGen;
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
		FOREACH, LABLE, SWITCH, CASE, THIS, SUPER, INSTANCEOF, ASSERT, COMPILED;
	}
	
	public static class XTreeError extends XTree{

		public XTreeError(XLineDesk line) {
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
	
	public static class XTreeClassFile extends XTree{

		public List<XTreeAnnotation> packAnnotations;
		
		public XTreeIdent packID;
		
		public List<XTree> defs;
		
		public XTreeClassFile(XLineDesk line, List<XTreeAnnotation> packAnnotations, XTreeIdent packID, List<XTree> defs) {
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
	
	public static class XTreeImport extends XTree{
		
		public String iimport;

		public boolean indirect;
		
		public boolean staticImport;
		
		public XTreeImport(XLineDesk lineDesk, String iimport, boolean indirect, boolean staticImport) {
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
	
	public static class XTreeClassDecl extends XTreeStatement{

		public XTreeModifier modifier;
		
		public String name;
		
		public List<XTreeTypeParam> typeParam;
		
		public List<XTreeType> superClasses;
		
		public List<XTree> defs;
		
		public XTreeClassDecl(XLineDesk line, XTreeModifier modifier, String name, List<XTreeTypeParam> typeParam, List<XTreeType> superClasses, List<XTree> defs) {
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
	
	public static class XTreeModifier extends XTree{
		
		public int modifier;
		
		public List<XTreeAnnotation> annotations;
		
		public XTreeModifier(XLineDesk line, int modifier){
			super(line);
			this.modifier = modifier;
		}
		
		public XTreeModifier(XLineDesk line, int modifier, List<XTreeAnnotation> annotations){
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
	
	public static class XTreeAnnotation extends XTree{

		public XTreeAnnotation(XLineDesk line) {
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
	
	public static abstract class XTreeExpression extends XTree{

		public XTreeExpression(XLineDesk line) {
			super(line);
		}
		
	}
	
	public static class XTreeIdent extends XTreeStatement{

		public String name;
		
		public XTreeIdent(XLineDesk line, String name) {
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
	
	public static class XTreeThis extends XTreeStatement{
		
		public XTreeThis(XLineDesk line) {
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
	
	public static class XTreeSuper extends XTreeStatement{
		
		public XTreeSuper(XLineDesk line) {
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
	
	public static abstract class XTreeStatement extends XTree{

		public XTreeStatement(XLineDesk line) {
			super(line);
		}
		
	}
	
	public static class XTreeType extends XTreeStatement{

		public XTreeIdent name;
		
		public List<XTreeType> typeParam;
		
		public int array;
		
		public XTreeType(XLineDesk line, XTreeIdent name, List<XTreeType> typeParam, int array) {
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
	
	public static class XTreeTypeParam extends XTree{
		
		public String name;
		
		public List<XTreeType> extend;
		
		public boolean isSuper;
		
		public XTreeTypeParam(XLineDesk line, String name, List<XTreeType> extend, boolean isSuper) {
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
	
	public static class XTreeVarDecl extends XTree{

		public XTreeModifier modifier;
		
		public String name;
		
		public XTreeType type;

		public XTreeStatement init;
		
		public XTreeVarDecl(XLineDesk line, XTreeModifier modifier, String name, XTreeType type, XTreeStatement init) {
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
	
	public static class XTreeVarDecls extends XTreeStatement{
		
		public List<XTreeVarDecl> varDecls;
		
		public XTreeVarDecls(XLineDesk line, List<XTreeVarDecl> varDecls) {
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
	
	public static class XTreeMethodDecl extends XTree{

		public XTreeModifier modifier;
		
		public String name;
		
		public List<XTreeTypeParam> typeParam;
		
		public XTreeType returnType;
		
		public List<XTreeVarDecl> paramTypes;
		
		public List<XTreeType> throwList;

		public XTreeBlock block;
		
		public List<XTreeStatement> superConstructors;
		
		public boolean varargs;
		
		public XTreeMethodDecl(XLineDesk line, XTreeModifier modifier, String name, List<XTreeTypeParam> typeParam, XTreeType returnType, List<XTreeVarDecl> paramTypes, List<XTreeType> throwList, XTreeBlock block, List<XTreeStatement> superConstructors, boolean varargs) {
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
	
	public static class XTreeBlock extends XTreeStatement{

		public List<XTreeStatement> statements;
		
		public XTreeBlock(XLineDesk line, List<XTreeStatement> statements) {
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
	
	public static class XTreeGroup extends XTreeStatement{

		public XTreeStatement statement;
		
		public XTreeGroup(XLineDesk line, XTreeStatement statement) {
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
	
	public static class XTreeBreak extends XTreeStatement{

		public String lable;
		
		public XTreeBreak(XLineDesk line, String lable) {
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
	
	public static class XTreeContinue extends XTreeStatement{

		public String lable;
		
		public XTreeContinue(XLineDesk line, String lable) {
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
	
	public static class XTreeDo extends XTreeStatement{

		public XTreeStatement block;
		
		public XTreeStatement doWhile;
		
		public XTreeDo(XLineDesk line, XTreeStatement block, XTreeStatement doWhile) {
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
	
	public static class XTreeFor extends XTreeStatement{

		public XTreeStatement init;
		
		public XTreeStatement doWhile;
		
		public XTreeStatement inc;
		
		public XTreeStatement block;
		
		public XTreeFor(XLineDesk line, XTreeStatement init, XTreeStatement doWhile, XTreeStatement inc, XTreeStatement block) {
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
	
	public static class XTreeForeach extends XTreeStatement{

		public XTreeStatement var;
		
		public XTreeStatement in;
		
		public XTreeStatement block;
		
		public XTreeForeach(XLineDesk line, XTreeStatement var, XTreeStatement in, XTreeStatement block) {
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
	
	public static class XTreeWhile extends XTreeStatement{

		public XTreeStatement block;
		
		public XTreeStatement doWhile;
		
		public XTreeWhile(XLineDesk line, XTreeStatement block, XTreeStatement doWhile) {
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
	
	public static class XTreeIf extends XTreeStatement{

		public XTreeStatement iif;
		
		public XTreeStatement block;
		
		public XTreeStatement block2;
		
		public XTreeIf(XLineDesk line, XTreeStatement iif, XTreeStatement block, XTreeStatement block2) {
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
	
	public static class XTreeReturn extends XTreeStatement{

		public XTreeStatement statement;
		
		public XTreeReturn(XLineDesk line, XTreeStatement statement) {
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
	
	public static class XTreeThrow extends XTreeStatement{

		public XTreeStatement statement;
		
		public XTreeThrow(XLineDesk line, XTreeStatement statement) {
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
	
	public static class XTreeSynchronized extends XTreeStatement{

		public XTreeStatement ident;
		
		public XTreeStatement block;
		
		public XTreeSynchronized(XLineDesk line, XTreeStatement ident, XTreeStatement block) {
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
	
	public static class XTreeConstant extends XTreeStatement{

		public XConstantValue value;
		
		public XTreeConstant(XLineDesk line, XConstantValue value) {
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
	
	public static class XTreeMethodCall extends XTreeStatement{
		
		public XTreeStatement method;
		
		public List<XTreeStatement> params;
		
		public List<XTreeType> typeParam;
		
		public XTreeMethodCall(XLineDesk line, XTreeStatement method, List<XTreeStatement> params, List<XTreeType> typeParam) {
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
	
	public static class XTreeIndex extends XTreeStatement{
		
		public XTreeStatement array;
		
		public XTreeStatement index;
		
		public XTreeIndex(XLineDesk line, XTreeStatement array, XTreeStatement index) {
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
	
	public static class XTreeNew extends XTreeStatement{
		
		public XTreeType type;
		
		public List<XTreeStatement> params;
		
		public XTreeClassDecl classDecl;
		
		public XTreeNew(XLineDesk line, XTreeType type, List<XTreeStatement> params, XTreeClassDecl classDecl) {
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
	
	public static class XTreeNewArray extends XTreeStatement{
		
		public XTreeType type;
		
		public List<XTreeStatement> arraySizes;
		
		public XTreeStatement arrayInitialize;
		
		public XTreeNewArray(XLineDesk line, XTreeType type, List<XTreeStatement> arraySizes, XTreeStatement arrayInitialize) {
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
	
	public static class XTreeOperatorStatement extends XTreeStatement{

		public XTreeStatement left;
		
		public XTreeStatement right;
		
		public XOperator operator;
		
		public XTreeOperatorStatement(XLineDesk line, XTreeStatement left, XOperator operator, XTreeStatement right) {
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
	
	public static class XTreeInstanceof extends XTreeStatement{

		public XTreeStatement statement;
		
		public XTreeType type;
		
		public XTreeInstanceof(XLineDesk line, XTreeStatement statement, XTreeType type) {
			super(line);
			this.statement = statement;
			this.type = type;
		}

		@Override
		public XTag getTag() {
			return XTag.INSTANCEOF;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitInstanceof(this);
		}
		
	}
	
	public static class XTreeOperatorPrefixSuffix extends XTreeStatement{

		public List<XOperator> prefix;
		
		public XTreeStatement statement;
		
		public List<XOperator> suffix;
		
		public XTreeOperatorPrefixSuffix(XLineDesk line, List<XOperator> prefix, XTreeStatement statement, List<XOperator> suffix) {
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
	
	public static class XTreeIfOperator extends XTreeOperatorStatement{
		
		public XTreeStatement statement;
		
		public XTreeIfOperator(XLineDesk line, XTreeStatement left, XTreeStatement statement, XTreeStatement right) {
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
	
	public static class XTreeCast extends XTreeStatement{
		
		public XTreeType type;
		
		public XTreeStatement statement;
		
		public XTreeCast(XLineDesk line, XTreeType type, XTreeStatement statement) {
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
	
	public static class XTreeLambda extends XTreeStatement{

		public List<XTreeVarDecl> params;
		
		public XTreeStatement statement;
		
		public XTreeLambda(XLineDesk line, List<XTreeVarDecl> params, XTreeStatement statement) {
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
	
	public static class XTreeTry extends XTreeStatement{
		
		public List<XTreeVarDecls> resource;
		
		public XTreeStatement block;
		
		public List<XTreeCatch> catchs;
		
		public XTreeStatement finallyBlock;
		
		public XTreeTry(XLineDesk line, List<XTreeVarDecls> varDecls, XTreeStatement block, List<XTreeCatch> catchs, XTreeStatement finallyBlock) {
			super(line);
			this.resource = varDecls;
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
	
	public static class XTreeCatch extends XTree{

		public XTreeModifier modifier;
		
		public List<XTreeType> types;
		
		public String varName;
		
		public XTreeStatement block;
		
		public XTreeCatch(XLineDesk line, XTreeModifier modifier, List<XTreeType> types, String varName, XTreeStatement block) {
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
	
	public static class XTreeArrayInitialize extends XTreeStatement{

		public List<XTreeStatement> statements;
		
		public XTreeArrayInitialize(XLineDesk line, List<XTreeStatement> statements) {
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
	
	public static class XTreeLable extends XTreeStatement{

		public String name;
		
		public XTreeStatement statement;
		
		public XTreeLable(XLineDesk line, String name) {
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
	
	public static class XTreeSwitch extends XTreeStatement{

		public XTreeStatement statement;
		
		public List<XTreeCase> cases;
		
		public XTreeSwitch(XLineDesk line, XTreeStatement statement, List<XTreeCase> cases) {
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
	
	public static class XTreeCase extends XTree{

		public XTreeStatement key;
		
		public List<XTreeStatement> block;
		
		public XTreeCase(XLineDesk line, XTreeStatement key, List<XTreeStatement> block) {
			super(line);
			this.key = key;
			this.block = block;
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
	
	public static class XTreeAssert extends XTreeStatement{

		public XTreeStatement statement;
		
		public XTreeAssert(XLineDesk line, XTreeStatement statement) {
			super(line);
			this.statement = statement;
		}

		@Override
		public XTag getTag() {
			return XTag.ASSERT;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitAssert(this);
		}
		
	}
	
	public static class XTreeCompiledPart extends XTreeStatement{
		
		public XCodeGen codeGen;
		
		public XTreeCompiledPart(XLineDesk line, XCodeGen codeGen) {
			super(line);
			this.codeGen = codeGen;
		}

		@Override
		public XTag getTag() {
			return XTag.COMPILED;
		}

		@Override
		public void accept(XVisitor v) {
			v.visitCompiled(this);
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
