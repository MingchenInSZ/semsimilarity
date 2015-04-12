package sim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import sim.wang.SemSimDump;

/**
 * 
 * @author mingchen
 * 
 *         This is a utility of calculating pathways sim
 */
public class pathwaySim {
	// set the work directory
	private final static String wdir = "d:/tmp/bioinfo/li/";
	private BufferedWriter bw = null;

	/**
	 * 
	 * @param fileName
	 * @return HashMap
	 * 
	 *         prepare the pathways in hashmap format
	 */
	public HashMap<String, LinkedHashSet<String>> getPathways(String fileName) {
		HashMap<String, LinkedHashSet<String>> map = new HashMap<String, LinkedHashSet<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(wdir
					+ fileName)));

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
	 * @param map
	 * @param min
	 * @param max
	 * @return HashMap
	 * 
	 *         This function is used to filter the pathway by setting the min
	 *         and max number of elements
	 */
	public HashMap<String, LinkedHashSet<String>> pathwayFilter(
			HashMap<String, LinkedHashSet<String>> map, int min, int max) {
		Iterator<String> it = map.keySet().iterator();
		Vector<String> vector = new Vector<String>();
		while(it.hasNext()){
			String path = it.next();
			if (map.get(path).size() < min | map.get(path).size() > max) {
				vector.add(path);
			}
		}
		for (String s : vector) {
			map.remove(s);
		}

		return map;
	}

	public void pathwaySemSim(String fileName) throws ClassNotFoundException,
	IOException, InterruptedException {
		HashMap<String, LinkedHashSet<String>> map = pathwayFilter(
				getPathways(fileName), 5, 200);
		// Map<String, HashMap<String, Float>> dump = new HashMap<String,
		// HashMap<String, Float>>();
		bw = new BufferedWriter(new FileWriter(
				new File(wdir + "pathwaysim.res")));
		SemSimDump sem = new SemSimDump();
		Object[] paths = map.keySet().toArray();
		System.out.println("Starting ...");
		for (int i = 0; i < 100; i++) {
			new innerThread(map, paths, sem, i).start();
			// Thread.sleep(100000);
		}

	}

	public static void main(String[] args) throws InterruptedException,
	ClassNotFoundException, IOException {

		pathwaySim psim = new pathwaySim();

		psim.pathwaySemSim("cpdbpathway.gmt");
	}

	/**
	 * 
	 * @author mingchen
	 * @date 2015.4.11
	 * 
	 */
	class innerThread extends Thread {
		// inner thread local variables
		HashMap<String, LinkedHashSet<String>> map;
		Object[] paths;
		SemSimDump sem;
		int curPos;

		/**
		 * 
		 * @param map
		 * @param paths
		 * @param sem
		 * @param curPos
		 * 
		 *            Initialize the current local
		 */
		public innerThread(HashMap<String, LinkedHashSet<String>> map,
				Object[] paths, SemSimDump sem, int curPos) {
			this.map = map;
			this.paths = paths;
			this.sem = sem;
			this.curPos = curPos;
		}

		/**
		 * Override the Thread built-in function run and record all the pathways
		 * similarity
		 */
		@Override
		public void run() {
			System.out.println("In thread " + curPos);
			Map<String, HashMap<String, Float>> dump = new HashMap<String, HashMap<String, Float>>();
			int i = curPos;
			for (int j = i + 1; j < paths.length; j++) {
				Object[] geneset1 = map.get(paths[i]).toArray();
				Object[] geneset2 = map.get(paths[j]).toArray();
				float gs1 = 0.0f, gs2 = 0.0f;
				for (int ii = 0; ii < geneset1.length; ii++) {
					float tmp = 0.0f;
					for (int jj = 0; jj < geneset2.length; jj++) {
						float sim = 0.0f;

						try {
							sim = sem.geneSemSim(geneset1[ii].toString(),
									geneset2[jj].toString());
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (tmp < sim) {
							tmp = sim;
						}
						if (dump.containsKey(geneset2[jj].toString())) {
							HashMap<String, Float> inner = dump
									.remove(geneset2[jj].toString());
							inner.put(geneset1[ii].toString(),
									Float.valueOf(sim));
							dump.put(geneset2[jj].toString(), inner);
						} else {
							HashMap<String, Float> inner = new HashMap<String, Float>();
							inner.put(geneset1[ii].toString(),
									Float.valueOf(sim));
							dump.put(geneset2[jj].toString(), inner);
						}
					}
					gs1 += tmp;
				}
				int ii = 0;
				gs2 = 0.0f;
				for (Object gene : geneset2) {
					float tmp = 0.0f;
					for (Object g : geneset1) {
						if (tmp < dump.get(gene.toString()).get(g.toString())
								.floatValue()) {
							tmp = dump.get(gene.toString()).get(g.toString())
									.floatValue();
						}
					}
					gs2 += tmp;
				}
				float psim = (gs1 + gs2)
						/ (geneset1.length + geneset2.length + 0.0001f);
				System.out.println(i + " " + j + " " + paths.length + " "
						+ psim);
				String str = paths[i] + ":" + paths[j] + ":"
						+ Float.valueOf(psim).toString() + "\n";
				try {
					bw.write(str);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}// end for
		}// end run

	}
}
