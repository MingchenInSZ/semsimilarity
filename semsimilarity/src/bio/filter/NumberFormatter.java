package bio.filter;

import java.text.DecimalFormat;

public class NumberFormatter {
	/**
	 * 
	 * @param format
	 * @param f
	 * @return
	 */
	public static String decimalFormat(String format, double f) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(f);
	}

	/**
	 * 
	 * @param format
	 * @param f
	 * @return
	 */
	public static String decimalFormat(String format, float f) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(f);
	}

	/**
	 * 
	 * @param format
	 * @param long l
	 * @return String
	 */
	public static String decimalFormat(String format, long l) {
		DecimalFormat df = new DecimalFormat(format);
		return df.format(l);
	}

	public static void main(String[] args) {
		System.out.println(NumberFormatter.decimalFormat("#0.###E00", 123456789l));
		System.out.println(NumberFormatter.decimalFormat("#.00%", 0.5687));
	}
}
