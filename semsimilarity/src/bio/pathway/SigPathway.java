package bio.pathway;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import sim.io.objserialize;
import bio.filter.HyperGenomFast;
import bio.filter.RandomGenerator;
import downloader.LogUtils;

public class SigPathway {
	private static final String wdir = "dataRepository";
	private static final DecimalFormat df = new DecimalFormat("#.0000");
	private  HashMap<String, LinkedHashSet<String>> pathways = null;
	private HashMap<String, String> allGenes = null;// contains all the unique genes
	private HashSet<String> difGenes = null;
	public SigPathway(){
		pathways = getPathways("cpdbpathway.gmt");
		difGenes = getDifGenes("difgenesamr0.05.txt");
		try {
			String realFile = wdir + File.separator + "allUniqueGenes.out";
			allGenes = (HashMap<String, String>) objserialize.readObj(realFile);
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param nperms
	 * @param pval
	 * @return HashSet<Sting>
	 * 
	 *         get all the significant pathway (pval is user set default as
	 *         0.05)
	 */
	public HashMap<String, Double> sigPathwayTest(int nperms, double pval) {
		HashMap<String, Double> sig = new HashMap<String, Double>();
		LogUtils.log("Start testing..");
		for (String pathway : pathways.keySet()) {
			double p = calculatePval(pathways.get(pathway), difGenes, nperms);
			if (p == -1.0) {
				LogUtils.log("Pathway:" + pathway + " No overlap!");
			} else if (p <= pval) {
				sig.put(pathway, Double.valueOf(df.format(p)));
				LogUtils.log("Pathway:" + pathway + " sigf![" + p + "]");
			} else {
				LogUtils.log("Pathway:" + pathway + " not-sigf![" + p + "]");
			}
		}
		return sig;
	}

	/**
	 * 
	 * @param pval
	 * @return HashMap<String,Double>
	 * 
	 *         signifincance in hyperGenome
	 */
	public HashMap<String,Double> sigPathwayTest(double pval){
		HashMap<String,Double> sig = new HashMap<String,Double>();
		LogUtils.log("Start testing..");
		for(String pathway:pathways.keySet()){
			HashSet<String> genes = pathways.get(pathway);
			double p = 1 - HyperGenomFast.pHyperGenom(allGenes.size(),
					difGenes.size(), genes.size(), overlap(genes, difGenes));
			if(p<=pval){
				sig.put(pathway, Double.valueOf(df.format(p)));
				LogUtils.log("Pathway:" + pathway + " sigf![" + p + "]");
			} else {
				LogUtils.log("Pathway:" + pathway + " no-sigf![" + p + "]");
			}
		}
		return sig;
	}
	/**
	 * 
	 * @param pathwayGenes
	 * @param difGenes
	 * @param nperms
	 * @return double
	 * 
	 *         calculate P value using random permutation
	 */
	public double calculatePval(LinkedHashSet<String> pathwayGenes,
			HashSet<String> difGenes, int nperms) {

		int inter = overlap(pathwayGenes, difGenes), count = 0;
		if (inter == 0) {
			return -1.0;
		}
		for (int i = 0; i < nperms; i++) {
			HashSet<String> randGenes = getPermGenes(inter);
			count += sign(inter, overlap(difGenes, randGenes));
			if (i > 0 && i % 100 == 0) {
				LogUtils.log("Iterator:" + i);
			}
		}
		LogUtils.log("Iterator:" + nperms + " [" + count + "]");
		return count * 1.0 / nperms;

	}
	/**
	 * 
	 * @param source
	 * @param target
	 * @return int
	 *    Calculate the overlap of two sets
	 */
	public int overlap(HashSet<String> source, HashSet<String> target) {
		HashSet<String> copy = new HashSet<String>();
		for (String s : source) {
			copy.add(s);
		}
		copy.retainAll(target);
		return copy.size();
	}
	/**
	 * 
	 * @param thres
	 * @param num
	 * @return int
	 * 				 sign function
	 */
	public int sign(int thres,int num){
		if (num >= thres) {
			return 1;
		}
		return 0;
	}
	/**
	 * 
	 * @param num
	 * @return HashSet<String>
	 */
	public HashSet<String> getPermGenes(int num){
		HashSet<Integer> random = RandomGenerator.randomRange(allGenes.size(), 1, num);
		HashSet<String> randGenes = new HashSet<String>();
		for (Integer t : random) {
			randGenes.add(allGenes.get(t.toString()));
		}
		// LogUtils.log("Permuation Genes:" + num);
		return randGenes;
	}

	/**
	 * 
	 * @param fileName
	 * @return HashMap<String,LinkedHashMap<String>>
	 * 
	 *         Get all pathways in HashMap view.
	 */
	public HashMap<String, LinkedHashSet<String>> getPathways(String fileName) {
		HashMap<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(wdir
					+ File.separator + fileName)));

			String line = br.readLine();
			while (line != null) {
				String[] arr = line.split("\t");
				LinkedHashSet<String> set = new LinkedHashSet<String>();
				for (int i = 2; i < arr.length; i++) {
					set.add(arr[i]);
				}
				map.put(arr[0], set);
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 
	 * @param fileName
	 * @return HashSet<String>
	 * 
	 * Get differential expressed genes
	 */
	public HashSet<String> getDifGenes(String fileName){
		HashSet<String> difGenes = new HashSet<String>();
		String realFile = wdir+File.separator+fileName;
		try{
			BufferedReader br = new BufferedReader(new FileReader(new File(realFile)));
			String line = br.readLine();
			while(line!=null){
				difGenes.add(line.trim());
				line = br.readLine();
			}
			br.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return difGenes;
	}

	public static void main(String[] args) {
		SigPathway sig = new SigPathway();
		HashMap<String, Double> map = sig.sigPathwayTest(1000, 0.05);
		LogUtils.info(map.size());
		// for (String s : map.keySet()) {
		// LogUtils.info("Pathway:" + s + ",pval:" + map.get(s).toString());
		// }
	}
}
