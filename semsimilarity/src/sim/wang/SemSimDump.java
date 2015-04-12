package sim.wang;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import sim.io.objserialize;
import sim.io.relationship;

/**
 * 
 * @author mingchen
 * 
 *         Calculate sim by predumped
 */
public class SemSimDump {
	private static Map<String, LinkedList<relationship>> pmap = null;
	private static HashMap<String, HashMap<String, Float>> dpsv = null;
	private static Map<String, LinkedHashSet<String>> g2gmap = null;

	/**
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * 
	 *             Initialize the pmap
	 */
	public SemSimDump() throws ClassNotFoundException, IOException {
		pmap = (Map<String, LinkedList<relationship>>) objserialize
				.readObj("parents.out");
		dpsv = (HashMap<String, HashMap<String, Float>>) objserialize
				.readObj("dumpSVal.out");
		g2gmap = (HashMap<String, LinkedHashSet<String>>) objserialize
				.readObj("gene2go.out");
		System.out.println("Load done");
	}

	/**
	 * 
	 * @param goId
	 * @return map<String,Float>
	 * 
	 *         calculate the s-val of go term
	 */
	public HashMap<String, Float> semanticValue(String goId) {
		HashMap<String, Float> queue = new HashMap<String, Float>();
		LinkedList<String> extra = new LinkedList<String>();
		queue.put(goId, Float.valueOf(1.0f));
		extra.add(goId);
		while (!extra.isEmpty()) {
			String curGo = extra.poll();
			float curVal = queue.get(curGo).floatValue();
			// System.out.println(curGo + "  " + curVal);
			for (relationship rel : pmap.get(curGo)) {
				if (queue.containsKey(rel.getGoId())) {
					// if the term has calculated, then update with max s-val
					if (curVal * rel.getIntensity() > queue.get(curGo)
							.floatValue()) {
						queue.remove(rel.getGoId());
						queue.put(rel.getGoId(), curVal * rel.getIntensity());
					}
				} else {
					queue.put(rel.getGoId(), curVal * rel.getIntensity());
				}
				extra.add(rel.getGoId());// push the parent go to ╤сап
			}

		}
		return queue;
	}

	/**
	 * 
	 * @throws IOException
	 * 
	 *             Dump the SemanticValue of all the go terms in parents file
	 * @throws ClassNotFoundException
	 */
	public void dumpSemValue() throws IOException, ClassNotFoundException {
		HashMap<String, HashMap<String, Float>> dump = new HashMap<String, HashMap<String, Float>>();
		LinkedList<String> goes = (LinkedList<String>)objserialize.readObj("ungoes.out");
		for (String go : goes) {
			HashMap<String, Float> list = semanticValue(go);
			dump.put(go, list);
			System.out.println("dump " + go + " done");
		}
		new objserialize("dumpSVal.out").objOutput(dump);
	}

	/**
	 * 
	 * @param go1
	 * @param go2
	 * @return float
	 * 
	 *         calculate the semsim value of go terms
	 */
	public float goSemSim(String go1, String go2) {
		float sim = 0.0f;
		HashMap<String,Float> gs1,gs2;
		if(!dpsv.containsKey(go1)){
			return sim;
		}else{
			gs1 = dpsv.get(go1);
		}
		if (!dpsv.containsKey(go2)){
			return sim;
		}else{
			gs2 = dpsv.get(go2);
		}
		Set<String> res = new LinkedHashSet<String>();
		for (String s : gs1.keySet()) {
			res.add(s);
		}
		res.retainAll(gs2.keySet());
		float sum = 0.0f, gv1 = 0.0f, gv2 = 0.0f;
		for (String s : gs1.keySet()) {
			gv1 += gs1.get(s).floatValue();
			if (res.contains(s)) {
				sum += gs1.get(s).floatValue();
			}
		}
		for (String s : gs2.keySet()) {
			gv2 += gs2.get(s).floatValue();
			if (res.contains(s)) {
				sum += gs2.get(s).floatValue();
			}

		}
		sim = sum / (gv1 + gv2 + 0.0001f);
		return sim;
	}

	/**
	 * 
	 * @param go
	 * @param goset
	 * @return float
	 * 
	 *         calculate go and goset sim
	 */
	public float goAndSetSemSim(String go, Set<String> goset) { // calculate the
		// max one
		float max = 0;
		for (String g : goset) {
			float sim = goSemSim(go, g);
			if (sim > max) {
				max = sim;
			}
		}
		return max;
	}

	/**
	 * 
	 * @param goset1
	 * @param goset2
	 * @return float
	 * 
	 *         calculate the sim of gosets
	 */
	public float goSetSemSim(Set<String> goset1, Set<String> goset2) {
		float gos1 = 0.0f, gos2 = 0.0f;
		Map<String, Float> map = new HashMap<String, Float>();
		for (String go : goset1) {
			float tmp = 0.0f;
			for (String g : goset2) {
				float sim = goSemSim(go, g);
				tmp = Math.max(tmp, sim);
				// dump the calculated sim
				// System.out.println(go + " " + g + " " + sim);
				map.put(g, Float.valueOf(Math.max(sim,
						map.get(g) == null ? 0.0f : map.get(g).floatValue())));
			}
			gos1 += tmp;
		}
		for (String go : map.keySet()) {
			gos2 += map.get(go).floatValue();
		}

		return (gos1 + gos2) / (goset1.size() + goset2.size() + 0.0001f);
	}

	/**
	 * 
	 * @param gene1
	 * @param gene2
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * 
	 *             Calculate the semsim of genes
	 */
	public float geneSemSim(String gene1, String gene2) throws IOException,
	ClassNotFoundException {
		Map<String, LinkedHashSet<String>> map = g2gmap;
		Set<String> gs1, gs2;
		if (gene1 != "" && map.containsKey(gene1)) {
			gs1 = map.get(gene1);
		} else {
			return 0.0f;
		}
		if (gene2 != "" && map.containsKey(gene2)) {
			gs2 = map.get(gene2);
		} else {
			return 0.0f;
		}
		return goSetSemSim(gs1, gs2);
	}

	public static void main(String[] args) throws ClassNotFoundException,
	IOException {
		SemSimDump ssd = new SemSimDump();
		// ssd.dumpSemValue();
		// System.out.println(ssd.goSemSim("GO:0004601", "GO:0043027"));
		System.out.println(ssd.geneSemSim("PTGS1", "CD27"));
		// String tmp =
		// "GO:0004888,GO:0005515,GO:0005576,GO:0005887,GO:0007166,GO:0008588,GO:0009897,GO:0016064,GO:0043027,GO:0043066,GO:0043154,GO:0045471,GO:0045579,GO:0046330,GO:0070062,GO:0070233,GO:0097191";
		// Set<String> set = new LinkedHashSet<String>();
		// for (String s : tmp.split(",")) {
		// set.add(s);
		// }
		// System.out.println(ssd.goAndSetSemSim("GO:0002770", set));
		//
		// System.out.println(ssd.semanticValue("GO:0002770"));
		// HashMap<String, HashMap<String, Float>> dpsv = (HashMap<String,
		// HashMap<String, Float>>) objserialize
		// .readObj("dumpSVal.out");
		//
		// if (dpsv.containsKey("GO:0002770")) {
		// System.out.println(dpsv.get("GO:0002770"));
		// }

	}

}
