package xscript.compiler.inst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import xscript.XOpcode;
import xscript.compiler.XDataOutput;
import xscript.compiler.XJumpTarget;

public class XInstSwitch extends XInst {

	public static final Object DEFAULT = new Object();

	private XInstRef _default;
	private HashMap<Object, XInstRef> switches = new HashMap<Object, XInstRef>();

	public XInstSwitch(int line) {
		super(line, XOpcode.SWITCH, null, new ArrayList<XInstRef>());
	}
	
	@Override
	public void toCode(XDataOutput dataOutput) {
		super.toCode(dataOutput);
		int defRes = _default.getInst().resolved;
		dataOutput.writeShort(defRes);
		XInstRef ref = switches.get(null);
		if (ref == null) {
			dataOutput.writeShort(defRes);
		} else {
			dataOutput.writeShort(ref.getInst().resolved);
		}
		ref = switches.get(true);
		if (ref == null) {
			dataOutput.writeShort(defRes);
		} else {
			dataOutput.writeShort(ref.getInst().resolved);
		}
		ref = switches.get(false);
		if (ref == null) {
			dataOutput.writeShort(defRes);
		} else {
			dataOutput.writeShort(ref.getInst().resolved);
		}
		List<Long> longSwitches = new LinkedList<Long>();
		List<Number> doubleSwitches = new LinkedList<Number>();
		List<String> stringSwitches = new LinkedList<String>();
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		for (Entry<Object, XInstRef> e : switches.entrySet()) {
			Object key = e.getKey();
			if (key instanceof Long) {
				long l = ((Long) key).longValue();
				if (l > max)
					max = l;
				if (l < min)
					min = l;
				longSwitches.add((Long) key);
			} else if (key instanceof Float || key instanceof Double) {
				doubleSwitches.add((Number) key);
			} else if (key instanceof String) {
				stringSwitches.add((String) key);
			} else {
				throw new AssertionError();
			}
		}

		Number[] doubleKeys = doubleSwitches.toArray(new Number[doubleSwitches.size()]);
		Arrays.sort(doubleKeys, DOUBLE_COMPARATOR);
		dataOutput.writeShort(doubleKeys.length);
		for (Number key : doubleKeys) {
			if (key instanceof Float) {
				dataOutput.writeByte(0);
				dataOutput.writeFloat((Float) key);
			} else if (key instanceof Double) {
				dataOutput.writeByte(1);
				dataOutput.writeDouble((Float) key);
			}
			dataOutput.writeShort(switches.get(key).getInst().resolved);
		}

		String[] stringKeys = stringSwitches.toArray(new String[stringSwitches.size()]);
		Arrays.sort(stringKeys);
		dataOutput.writeShort(stringKeys.length);
		for (String key : stringKeys) {
			dataOutput.writeUTF(key);
			dataOutput.writeShort(switches.get(key).getInst().resolved);
		}

		if (longSwitches.size() == 0) {
			dataOutput.writeByte(5);
		} else if (useTabel(min, max, longSwitches.size())) {
			if (min >= Byte.MIN_VALUE && min <= Byte.MAX_VALUE) {
				dataOutput.writeByte(0);
				dataOutput.writeByte((int) min);
			} else if (min >= Short.MIN_VALUE && min <= Short.MAX_VALUE) {
				dataOutput.writeByte(1);
				dataOutput.writeShort((int) min);
			} else if (min >= Integer.MIN_VALUE && min <= Integer.MAX_VALUE) {
				dataOutput.writeByte(2);
				dataOutput.writeInt((int) min);
			} else {
				dataOutput.writeByte(3);
				dataOutput.writeLong(min);
			}
			int size = (int) (max - min);
			dataOutput.writeShort(size);
			for (int j = 0; j <= size; j++) {
				ref = switches.get(Long.valueOf(min + j));
				if (ref == null) {
					dataOutput.writeShort(defRes);
				} else {
					dataOutput.writeShort(ref.getInst().resolved);
				}
			}
		} else {
			dataOutput.writeByte(4);
			Long[] longKeys = longSwitches.toArray(new Long[longSwitches.size()]);
			Arrays.sort(longKeys, LONG_COMPARATOR);
			dataOutput.writeShort(longKeys.length);
			for (Long key : longKeys) {
				long k = key;
				if (k >= Short.MIN_VALUE && k <= Short.MAX_VALUE) {
					dataOutput.writeByte(0);
					dataOutput.writeShort((int) k);
				} else if (k >= Integer.MIN_VALUE && k <= Integer.MAX_VALUE) {
					dataOutput.writeByte(1);
					dataOutput.writeInt((int) k);
				} else {
					dataOutput.writeByte(2);
					dataOutput.writeLong(k);
				}
				dataOutput.writeShort(switches.get(key).getInst().resolved);
			}
		}

	}

	@Override
	public int getSize() {
		int size = 14;
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		int cnt = 0;
		int nonTableSize = 0;
		for (Object key : switches.keySet()) {
			if (key instanceof Long) {
				long l = ((Long) key).longValue();
				cnt++;
				if (l > max)
					max = l;
				if (l < min)
					min = l;
				nonTableSize += 5;
			} else if (key instanceof Float || key instanceof Double) {
				size += 5;
			} else if (key instanceof String) {
				size += 4;
			}
		}
		if (cnt != 0) {
			if (useTabel(min, max, cnt)) {
				if (min >= Byte.MIN_VALUE && min <= Byte.MAX_VALUE) {
					size += 3;
				} else {
					size += 4;
				}
				size += cnt * 2;
			} else {
				size += 2 + nonTableSize;
			}
		}
		return size;
	}

	private static boolean useTabel(long min, long max, int count) {
		return false;
	}

	private static final LongComparator LONG_COMPARATOR = new LongComparator();

	private static final DoubleComparator DOUBLE_COMPARATOR = new DoubleComparator();

	private static class LongComparator implements Comparator<Number> {

		@Override
		public int compare(Number o1, Number o2) {
			return Long.compare(o1.longValue(), o2.longValue());
		}

	}

	private static class DoubleComparator implements Comparator<Number> {

		@Override
		public int compare(Number o1, Number o2) {
			return Double.compare(o1.doubleValue(), o2.doubleValue());
		}

	}

	public boolean putIfNonExist(Object _const, XJumpTarget target) {
		if (_const == DEFAULT) {
			if (_default != null)
				return false;
			jumps.add(_default = new XInstRef(target.target));
			return true;
		}
		if (_const instanceof Character) {
			_const = Long.valueOf(((Character) _const).charValue());
		} else if (_const instanceof Byte || _const instanceof Short || _const instanceof Integer) {
			_const = Long.valueOf(((Number) _const).longValue());
		}
		if (switches.containsKey(_const))
			return false;
		XInstRef ref = new XInstRef(target.target);
		switches.put(_const, ref);
		jumps.add(ref);
		return true;
	}

}
