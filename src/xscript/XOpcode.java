package xscript;

public enum XOpcode {

	NOP(0, 0),

	LINE1(0, 0), LINE2(0, 0), LINE4(0, 0),

	LOADN(0, 1), LOADB(0, 1), LOADS(0, 1), LOADI(0, 1), LOADL(0, 1), LOADF(0, 1), LOADD(0, 1), LOADT(0, 1),

	LOAD_TRUE(0, 1), LOAD_FALSE(0, 1),

	LOADI_M1(0, 1), LOADI_0(0, 1), LOADI_1(0, 1), LOADI_2(0, 1),

	LOADD_M1(0, 1), LOADD_0(0, 1), LOADD_1(0, 1), LOADD_2(0, 1),

	LOADT_E(0, 1),

	SWAP(2, 2), DUP(1, 2),

	GETTOP1(0, 1), SETTOP1(1, 0), GETTOP2(0, 1), SETTOP2(1, 0),

	GETBOTTOM1(0, 1), SETBOTTOM1(1, 0), GETBOTTOM2(0, 1), SETBOTTOM2(1, 0),

	POP(0, 0) {
		@Override
		public int getStackRemove(int i) {
			return i;
		}
	},
	POP_1(1, 0),

	RET(0, 0), RETN(0, 1),

	JUMP(0, 0), JUMP_IF_ZERO(1, 0), JUMP_IF_NON_ZERO(1, 0),

	GET_GLOBAL(0, 1), SET_GLOBAL(1, 0), DEL_GLOBAL(0, 0),

	GET_CLOSURE(0, 1), SET_CLOSURE(1, 0),

	GET_ATTR(1, 1), SET_ATTR(2, 1), DEL_ATTR(1, 0),

	GET_INDEX(2, 1), SET_INDEX(3, 1), DEL_INDEX(2, 0),

	ADD(2, 1), // [..., a, b]=>[..., a+b]
	LADD(2, 1), // [..., a, b]=>[..., a+=b]

	SUB(2, 1), LSUB(2, 1),

	MUL(2, 1), LMUL(2, 1),

	DIV(2, 1), LDIV(2, 1),

	IDIV(2, 1), LIDIV(2, 1),

	MOD(2, 1), LMOD(2, 1),

	POW(2, 1), LPOW(2, 1),

	OR(2, 1), LOR(2, 1),

	AND(2, 1), LAND(2, 1),

	XOR(2, 1), LXOR(2, 1),

	SHL(2, 1), LSHL(2, 1),

	SHR(2, 1), LSHR(2, 1),

	ISHR(2, 1), LISHR(2, 1),

	POS(1, 1), // [..., a]=>[..., +a]
	NEG(1, 1), // [..., a]=>[..., -a]
	NOT(1, 1), // [..., a]=>[..., !a]
	INVERT(1, 1), // [..., a]=>[..., ~a]

	EQUAL(2, 1), // [..., a, b]=>[..., a===b]
	NOT_EQUAL(2, 1), // [..., a, b]=>[..., a!==b]
	SAME(2, 1), // [..., a, b]=>[..., a==b]
	NOT_SAME(2, 1), // [..., a, b]=>[..., a!=b]
	SMALLER(2, 1), // [..., a, b]=>[..., a==b]
	GREATER(2, 1), // [..., a, b]=>[..., a==b]
	SMALLER_EQUAL(2, 1), // [..., a, b]=>[..., a==b]
	GREATER_EQUAL(2, 1), // [..., a, b]=>[..., a==b]

	COMPARE(2, 1), // [..., a, b]=>[..., a<=>b]

	COPY(1, 1), // [..., a]=>[..., <:a]
	INC(1, 1), // [..., a]=>[..., ++a]
	SINC(1, 1), // [..., a]=>[..., a++]
	DEC(1, 1), // [..., a]=>[..., --a]
	SDEC(1, 1), // [..., a]=>[..., a--]

	CALL(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_LIST(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_MAP(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_LIST_MAP(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_KW(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_LIST_KW(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_MAP_KW(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},
	CALL_LIST_MAP_KW(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i + 2;
		}

	},

	MAKE_LIST(0, 1) {

		@Override
		public int getStackRemove(int i) {
			return i;
		}

	},
	MAKE_TUPLE(0, 1) {
		@Override
		public int getStackRemove(int i) {
			return i;
		}
	},
	MAKE_MAP(0, 1) {
		@Override
		public int getStackChange(int i) {
			return i * 2;
		}
	},
	MAKE_CLASS(0, 0), MAKE_FUNC(0, 0), MAKE_METH(1, 0),

	TYPEOF(1, 1), INSTANCEOF(2, 1), ISDERIVEDOF(2, 1),

	YIELD(1, 1), THROW(1, 0),

	START_TRY(0, 0), END_TRY(0, 0),

	IMPORT(0, 1), IMPORT_SAVE(2, 0),

	SWITCH(1, 0),

	END_FINALLY(2, 0),

	SUPER(0, 1);

	private int stackRemove;

	private int stackAdd;

	XOpcode(int stackRemove, int stackAdd) {
		this.stackRemove = stackRemove;
		this.stackAdd = stackAdd;
	}

	public int getStackChange(int i) {
		return getStackAdd(i) - getStackRemove(i);
	}

	public int getStackAdd(int i) {
		return stackAdd;
	}

	public int getStackRemove(int i) {
		return stackRemove;
	}

}
