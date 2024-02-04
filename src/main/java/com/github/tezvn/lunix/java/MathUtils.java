package com.github.tezvn.lunix.java;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

	/**
	 * Find percentage between two ranges
	 */
	public static BigDecimal findPercentInRange(double start, double end, double val) {
		end = end - start;
		val = val - start;
		double value = ((1-(val/end))*100)/100;
		return new BigDecimal(value);
	}

	/**
	 * Convert percentage to numeric between two ranges
	 */
	public static BigDecimal fromPercentToNumber(double start, double end, double percent) {
		return new BigDecimal(start + percent * (end -start));
	}

	public static BigDecimal getPercent(double number1, double number2) {
		return getPercent(number1, number2, RoundingMode.HALF_UP, 1);
	}

	public static BigDecimal getPercent(double number1, double number2, RoundingMode roundMode, int scale) {
		return BigDecimal.valueOf(number1 < number2 ? (number1 * 100.0f) / number2 : (number2 * 100.0f) / number1).setScale(scale, roundMode);
	}

	public static double roundDouble(double amount) {
		return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public static double round(double amount, int scale) {
		return new BigDecimal(amount).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	public static int roundUp(double amount) {
		return new BigDecimal(amount).setScale(2, RoundingMode.FLOOR).intValue();
	}
	
	public static int eval(final String str) {
		return (int) new Object() {
			int pos = -1, ch;

			void nextChar() {
				ch = (++pos < str.length()) ? str.charAt(pos) : -1;
			}

			boolean eat(int charToEat) {
				while (ch == ' ') nextChar();
				if (ch == charToEat) {
					nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				nextChar();
				double x = parseExpression();
				if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)`
			//        | number | functionName factor | factor `^` factor

			double parseExpression() {
				double x = parseTerm();
				for (;;) {
					if      (eat('+')) x += parseTerm(); // addition
					else if (eat('-')) x -= parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = parseFactor();
				for (;;) {
					if      (eat('*')) x *= parseFactor(); // multiplication
					else if (eat('/')) x /= parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (eat('+')) return parseFactor(); // unary plus
				if (eat('-')) return -parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (eat('(')) { // parentheses
					x = parseExpression();
					eat(')');
				} else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
					while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (ch >= 'a' && ch <= 'z') { // functions
					while (ch >= 'a' && ch <= 'z') nextChar();
					String func = str.substring(startPos, this.pos);
					x = parseFactor();
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char)ch);
				}

				if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
