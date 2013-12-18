package xscript.compiler;

import static xscript.compiler.XOperator.Type.*;

public enum XOperator {

	NONE(null, null, -1, false),
	
	ADD("+", INFIX, 10) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.add(right);
		}
	},
	SUB("-", INFIX, 10) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.sub(right);
		}
	},
	MUL("*", INFIX, 11) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.mul(right);
		}
	},
	DIV("/", INFIX, 11) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.div(right);
		}
	},
	MOD("%", INFIX, 11) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.mod(right);
		}
	},
	POW("**", INFIX, 12) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.pow(right);
		}
	},
	SHR(">>", INFIX, 9) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.shr(right);
		}
	},
	SHL("<<", INFIX, 9) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.shl(right);
		}
	},
	
	POS("+", PREFIX, -1) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left;
		}
	},
	NEG("-", PREFIX, -1) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.neg();
		}
	},
	
	BOR("|", INFIX, 4) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.bor(right);
		}
	},
	BAND("&", INFIX, 6) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.band(right);
		}
	},
	XOR("^", INFIX, 5) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.xor(right);
		}
	},
	OR("||", INFIX, 2) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.or(right);
		}
	},
	AND("&&", INFIX, 3) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.and(right);
		}
	},
	
	NOT("!", PREFIX, -1) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.not();
		}
	},
	BNOT("~", PREFIX, -1) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.bnot();
		}
	},
	
	EQ("==", INFIX, 7, false) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.eq(right);
		}
	},
	REQ("===", INFIX, 7) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.eq(right);
		}
	},
	NEQ("!=", INFIX, 7, false) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.eq(right).not();
		}
	},
	RNEQ("!==", INFIX, 7) {
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.eq(right).not();
		}
	},
	BIG(">", INFIX, 8){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.comp(right).eq(new XConstantValue(1));
		}
	},
	BEQ(">=", INFIX, 8){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.comp(right).eq(new XConstantValue(-1)).not();
		}
	},
	SMA("<", INFIX, 8){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.comp(right).eq(new XConstantValue(-1));
		}
	},
	SEQ("<=", INFIX, 8){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.comp(right).eq(new XConstantValue(1)).not();
		}
	},
	COMP("<=>", INFIX, 7){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left.comp(right);
		}
	},
	
	LET("=", INFIX, 0, false),
	LETADD("+=", INFIX, 0),
	LETSUB("-=", INFIX, 0),
	LETMUL("*=", INFIX, 0),
	LETDIV("/=", INFIX, 0),
	LETMOD("%=", INFIX, 0),
	LETOR("|=", INFIX, 0),
	LETAND("&=", INFIX, 0),
	LETXOR("^=", INFIX, 0),
	LETSHR("<<=", INFIX, 0),
	LETSHL(">>=", INFIX, 0),
	COPY("<:", INFIX, 0, false),
	
	INC("++", PREFIX, -1),
	DEC("--", PREFIX, -1),
	INCS("++", SUFFIX, -1),
	DECS("--", SUFFIX, -1),
	COPYS("<:", SUFFIX, -1){
		@Override
		public XConstantValue calc(XConstantValue left, XConstantValue right) {
			return left;
		}
	},
	
	ELEMENT(".", INFIX, 13, false),
	
	IF("?", INFIX, 1, false),
	
	;
	
	public final static boolean[] L2R = {false, false, true, true, true, true, true, true, true, true, true, true, true, true};
	
	public final String op;
	public final Type type;
	public final int priority;
	public final boolean canBeOverwritten;
	
	XOperator(String op, Type type, int priority){
		this.op = op;
		this.type = type;
		this.priority = priority;
		canBeOverwritten = true;
	}
	
	XOperator(String op, Type type, int priority, boolean canBeOverwritten){
		this.op = op;
		this.type = type;
		this.priority = priority;
		this.canBeOverwritten = canBeOverwritten;
	}
	
	public XConstantValue calc(XConstantValue left, XConstantValue right){
		throw new UnsupportedOperationException();
	}
	
	public static enum Type{
		PREFIX, INFIX, SUFFIX
	}
	
}
