package com.taobao.mrsstd.hiveudf.calculate;

import com.taobao.mrsstd.hiveudf.util.Constants;

public class Operator {

	/**
	 * 判断一个字符或字符数组是否是运算符
	 * 
	 * @param object
	 * @return
	 */
	public static boolean isOperator(Object object, int mode) {
		if (object instanceof String) {
			object = ((String) object).toCharArray();
			if (((char[]) object).length == 1) {
				object = ((char[]) object)[0];
			}
		}

		if (object instanceof char[]) {
			char[] operator = (char[]) object;
			if (operator.length == 2) {
				if ((operator[0] == Constants.LOGIC_GREATEQUAL[0] && operator[1] == Constants.LOGIC_GREATEQUAL[1])
						|| (operator[0] == Constants.LOGIC_LESSEQUAL[0] && operator[1] == Constants.LOGIC_LESSEQUAL[1])
						|| (operator[0] == Constants.LOGIC_NOTEQUAL[0] && operator[1] == Constants.LOGIC_NOTEQUAL[1])
						|| (operator[0] == Constants.LOGIC_OR[0] && operator[1] == Constants.LOGIC_OR[1])) {
					return true;
				}
			} else if (operator.length == 3) {
				if ((operator[0] == Constants.LOGIC_AND[0]
						&& operator[1] == Constants.LOGIC_AND[1] && operator[2] == Constants.LOGIC_AND[2])
						|| (operator[0] == Constants.LOGIC_NOT[0]
								&& operator[1] == Constants.LOGIC_NOT[1] && operator[2] == Constants.LOGIC_NOT[2])) {
					return true;
				}
			}
		} else if (object instanceof Character) {
			Character operator = (Character) object;
			if (operator == Constants.SIGN_ADD
					|| operator == Constants.LOGIC_EQUAL
					|| operator == Constants.LOGIC_GREAT
					|| operator == Constants.LOGIC_LESS) {
				return true;
			}

			if ((mode == Constants.MODE_NUMBER)
					&& (operator == Constants.SIGN_SUBTRACT
							|| operator == Constants.SIGN_MULTIPLICATION || operator == Constants.SIGN_DIVIDE)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 取得运算符的优先级
	 * 
	 * @param object
	 * @return
	 */
	public static int getPriority(Object object) {
		if (object instanceof String) {
			object = ((String) object).toCharArray();
			if (((char[]) object).length == 1) {
				object = ((char[]) object)[0];
			}
		}

		if (object instanceof char[]) {
			char[] operator = (char[]) object;
			if (operator.length == 2) {
				if ((operator[0] == Constants.LOGIC_GREATEQUAL[0] && operator[1] == Constants.LOGIC_GREATEQUAL[1])
						|| (operator[0] == Constants.LOGIC_LESSEQUAL[0] && operator[1] == Constants.LOGIC_LESSEQUAL[1])
						|| (operator[0] == Constants.LOGIC_NOTEQUAL[0] && operator[1] == Constants.LOGIC_NOTEQUAL[1])) {
					return 6;
				}
				if (operator[0] == Constants.LOGIC_OR[0]
						&& operator[1] == Constants.LOGIC_OR[1]) {
					return 4;
				}
			} else if (operator.length == 3) {
				if (operator[0] == Constants.LOGIC_AND[0]
						&& operator[1] == Constants.LOGIC_AND[1]
						&& operator[2] == Constants.LOGIC_AND[2]) {
					return 5;
				}
				if (operator[0] == Constants.LOGIC_NOT[0]
						&& operator[1] == Constants.LOGIC_NOT[1]
						&& operator[2] == Constants.LOGIC_NOT[2]) {
					return 3;
				}
			}
		} else if (object instanceof Character) {
			Character operator = (Character) object;
			if (operator == Constants.SIGN_ADD
					|| operator == Constants.SIGN_SUBTRACT) {
				return 7;
			}

			if (operator == Constants.SIGN_MULTIPLICATION
					|| operator == Constants.SIGN_DIVIDE) {
				return 8;
			}

			if (operator == Constants.LOGIC_EQUAL
					|| operator == Constants.LOGIC_GREAT
					|| operator == Constants.LOGIC_LESS) {
				return 6;
			}
		}

		return -1;
	}

	/**
	 * 比较两个运算符
	 * 
	 * @param op1
	 * @param op2
	 * @return
	 */
	public static int compare(Object op1, Object op2) {
		int iop1 = getPriority(op1);
		int iop2 = getPriority(op2);
		return iop1 - iop2;
	}

	/**
	 * 获得操作符运算数个数，即运算符是几元运算符 目前除了not是一元运算符外，其它均为二元运算符
	 * 
	 * @param ops
	 * @return
	 */
	public static int getOperatorNum(String ops) {
		if (ops.equals(String.valueOf(Constants.LOGIC_NOT))) {
			return 1;
		}

		return 2;
	}
}
