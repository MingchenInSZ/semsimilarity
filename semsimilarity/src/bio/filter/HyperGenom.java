package bio.filter;

import java.math.BigInteger;

public class HyperGenom {

	/**
	 * 
	 * @param n
	 * @param m
	 * @return int
	 */
	public static BigInteger choice(int n, int m) {
		return permutation(n).divide(permutation(n-m).multiply(permutation(m)));
	}

	/**
	 * 
	 * @param n
	 * @return int
	 */
	public static BigInteger permutation(int n) {
		BigInteger bi = new BigInteger("1");
		for (int i = 2; i <= n; i++) {
			bi = bi.multiply(new BigInteger(String.valueOf(i)));
		}
		return bi;
	}

	/**
	 * 
	 * @param N
	 * @param M
	 * @param n
	 * @param k
	 * @return double
	 */
	public static double hyperGenom(int N, int M, int n, int k) {
		return choice(M, k).multiply(choice(N - M, n - k)).divide(choice(N, n))
				.doubleValue();
	}

	/**
	 * 
	 * @param N
	 * @param M
	 * @param n
	 * @param k
	 * @return double
	 */
	public static double hyperGenomLessK(int N, int M, int n, int k) {
		double d = 0.0;
		for (int i = 0; i < k; i++) {
			d += hyperGenom(N, M, n, i);
		}
		return d;
	}

	/**
	 * Main function
	 */
	public static void main(String[] args) {
		System.out.println(HyperGenom.hyperGenom(10000, 3750, 1000, 20));
	}
}
