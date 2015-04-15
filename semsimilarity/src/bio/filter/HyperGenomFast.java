package bio.filter;

/**
 * 
 * @author mingchen
 * @date 2015.4.15
 * 
 */
public class HyperGenomFast {
	/**
	 * Change the continuous multiplication into addition
	 * 
	 * @param n
	 * @param m
	 * @return double
	 */
	public static double lnchoice(int n, int m) {
		// choose to calculate the small part
		if (m > n / 2.0) {
			m = n - m;
		}
		double d = 0.0;
		for (int i = m + 1; i <= n; i++) {
			d += Math.log(i);
		}
		for (int i = 2; i <= n - m; i++) {
			d -= Math.log(i);
		}
		return d;
	}

	/**
	 * Calculate HyperGenom using lnchoice above
	 * 
	 * @param N
	 * @param M
	 * @param n
	 * @param k
	 * @return double
	 */
	public static double lnHyperGenom(int N, int M, int n, int k) {
		return lnchoice(M, k) + lnchoice(N - M, n - k) - lnchoice(N, n);
	}

	/**
	 * Actual hypergenom value
	 * 
	 * @param N
	 * @param M
	 * @param n
	 * @param k
	 * @return double
	 */
	public static double hyperGenom(int N, int M, int n, int k) {
		return Math.exp(lnHyperGenom(N, M, n, k));
	}

	/**
	 * 
	 * @param N
	 * @param M
	 * @param n
	 * @param k
	 * @return double
	 */
	public static double pHyperGenom(int N, int M, int n, int k) {
		int c = 0;
		double d = 0.0;
		while (c <= k) {
			d += hyperGenom(N, M, n, c++);
		}
		return d;
	}


}
