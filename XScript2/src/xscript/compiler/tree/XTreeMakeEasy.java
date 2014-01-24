package xscript.compiler.tree;

import java.util.ArrayList;
import java.util.List;

import xscript.compiler.XConstantValue;
import xscript.compiler.XOperator;
import xscript.compiler.message.XMessageLevel;
import xscript.compiler.message.XMessageList;
import xscript.compiler.token.XLineDesk;
import xscript.compiler.tree.XTree.XTreeCast;
import xscript.compiler.tree.XTree.XTreeConstant;
import xscript.compiler.tree.XTree.XTreeFor;
import xscript.compiler.tree.XTree.XTreeGroup;
import xscript.compiler.tree.XTree.XTreeIf;
import xscript.compiler.tree.XTree.XTreeIfOperator;
import xscript.compiler.tree.XTree.XTreeOperatorPrefixSuffix;
import xscript.compiler.tree.XTree.XTreeOperatorStatement;
import xscript.compiler.tree.XTree.XTreeWhile;

public class XTreeMakeEasy extends XTreeChanger implements XMessageListSetter {

	private boolean doreplace;
	
	private XTree replace;
	
	private XMessageList messages;
	
	public XTreeMakeEasy(){}
	
	public XTreeMakeEasy(XMessageList messages){
		this.messages = messages;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected <T extends XTree> T visitTree(T tree){
		if(tree!=null){
			try{
				tree.accept(this);
			}catch(Exception e){
				message(XMessageLevel.ERROR, "operator", tree.line, e.getMessage());
			}
		}
		if(doreplace){
			tree = (T) replace;
			doreplace = false;
		}
		return tree;
	}
	
	@Override
	public void visitOperator(XTreeOperatorStatement xOperatorStatement) {
		super.visitOperator(xOperatorStatement);
		if(xOperatorStatement.left instanceof XTreeConstant && xOperatorStatement.right instanceof XTreeConstant){
			XConstantValue left = ((XTreeConstant)xOperatorStatement.left).value;
			XConstantValue right = ((XTreeConstant)xOperatorStatement.right).value;
			try{
				XConstantValue value = xOperatorStatement.operator.calc(left, right);
				if((xOperatorStatement.operator==XOperator.OR && left.getBool())||(xOperatorStatement.operator==XOperator.AND && !left.getBool())){
					message(XMessageLevel.WARNING, "deadcode", xOperatorStatement.right.line);
				}
				replaceWith(xOperatorStatement, value);
			}catch(UnsupportedOperationException e){
				
			}
		}
		if(xOperatorStatement.operator==XOperator.COPY){
			List<XOperator> op = new ArrayList<XOperator>();
			op.add(XOperator.COPYS);
			if(!(xOperatorStatement.right instanceof XTreeConstant)){
				xOperatorStatement.right = new XTreeOperatorPrefixSuffix(xOperatorStatement.line, op, xOperatorStatement.right, null);
			}
			xOperatorStatement.operator=XOperator.LET;
		}
	}

	@Override
	public void visitOperatorPrefixSuffix(XTreeOperatorPrefixSuffix xOperatorPrefixSuffix) {
		super.visitOperatorPrefixSuffix(xOperatorPrefixSuffix);
		if(xOperatorPrefixSuffix.statement instanceof XTreeConstant){
			XConstantValue value = ((XTreeConstant)xOperatorPrefixSuffix.statement).value;
			if(xOperatorPrefixSuffix.prefix!=null){
				for(XOperator op: xOperatorPrefixSuffix.prefix){
					value = op.calc(value, null);
				}
			}
			if(xOperatorPrefixSuffix.suffix!=null){
				for(XOperator op: xOperatorPrefixSuffix.suffix){
					value = op.calc(value, null);
				}
			}
			replaceWith(xOperatorPrefixSuffix, value);
		}
	}

	@Override
	public void visitWhile(XTreeWhile xWhile) {
		super.visitWhile(xWhile);
		if(xWhile.doWhile instanceof XTreeConstant){
			XConstantValue value = ((XTreeConstant)xWhile.doWhile).value;
			if(!value.getBool()){
				message(XMessageLevel.WARNING, "deadcode", xWhile.block.line);
				setReplace(null);
			}
		}
	}

	@Override
	public void visitFor(XTreeFor xFor) {
		super.visitFor(xFor);
		if(xFor.doWhile instanceof XTreeConstant){
			XConstantValue value = ((XTreeConstant)xFor.doWhile).value;
			if(!value.getBool()){
				message(XMessageLevel.WARNING, "deadcode", xFor.block.line);
				setReplace(null);
			}
		}
	}

	@Override
	public void visitIf(XTreeIf xIf) {
		super.visitIf(xIf);
		if(xIf.iif instanceof XTreeConstant){
			XConstantValue value = ((XTreeConstant)xIf.iif).value;
			if(value.getBool()){
				if(xIf.block2!=null)
					message(XMessageLevel.WARNING, "deadcode", xIf.block2.line);
				setReplace(xIf.block);
			}else{
				message(XMessageLevel.WARNING, "deadcode", xIf.block.line);
				setReplace(xIf.block2);
			}
		}
	}

	@Override
	public void visitGroup(XTreeGroup xGroup) {
		super.visitGroup(xGroup);
		if(xGroup.statement instanceof XTreeConstant){
			replaceWith(xGroup, ((XTreeConstant)xGroup.statement).value);
		}
	}

	@Override
	public void visitIfOperator(XTreeIfOperator xIfOperator) {
		super.visitIfOperator(xIfOperator);
		if(xIfOperator.left instanceof XTreeConstant){
			XConstantValue value = ((XTreeConstant)xIfOperator.left).value;
			if(value.getBool()){
				message(XMessageLevel.WARNING, "deadcode", xIfOperator.right.line);
				setReplace(xIfOperator.statement);
			}else{
				message(XMessageLevel.WARNING, "deadcode", xIfOperator.statement.line);
				setReplace(xIfOperator.right);
			}
		}
	}

	@Override
	public void visitCast(XTreeCast xCast) {
		super.visitCast(xCast);
		if(xCast.statement instanceof XTreeConstant){
			if(xCast.type.typeParam==null && xCast.type.array==0){
				XConstantValue value = ((XTreeConstant)xCast.statement).value;
				Class<?> c = null;
				if(xCast.type.name.name.equals("bool")){
					c = Boolean.class;
				}else if(xCast.type.name.name.equals("char")){
					c = Character.class;
				}else if(xCast.type.name.name.equals("byte")){
					c = Byte.class;
				}else if(xCast.type.name.name.equals("short")){
					c = Short.class;
				}else if(xCast.type.name.name.equals("int")){
					c = Integer.class;
				}else if(xCast.type.name.name.equals("long")){
					c = Long.class;
				}else if(xCast.type.name.name.equals("float")){
					c = Float.class;
				}else if(xCast.type.name.name.equals("double")){
					c = Double.class;
				}
				value = value.castTo(c);
				replaceWith(xCast, value);
			}
		}
	}

	private void replaceWith(XTree tree, XConstantValue value){
		setReplace(new XTreeConstant(tree.line, value));
	}
	
	private void setReplace(XTree replace){
		doreplace = true;
		this.replace = replace;
	}
	
	private void message(XMessageLevel level, String key, XLineDesk lineDesk, Object...args){
		messages.postMessage(level, "makeeasy."+key, lineDesk, args);
	}

	@Override
	public void setMessageList(XMessageList messageList) {
		messages = messageList;
	}
	
}
