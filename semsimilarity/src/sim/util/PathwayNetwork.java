package sim.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import sim.io.objserialize;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

import downloader.LogUtils;

public class PathwayNetwork {
	private final HashSet<String> geneSet;
	private final HashMap<String, Double> normNet;
	private final HashMap<String, Double> tumorNet;
	private final HashMap<String, Double> semNet;
	private HashMap<String, LinkedHashSet<String>> sigPathway;

	public PathwayNetwork() {
		geneSet = new GeneSimNetwork().getGenes("difgenesamr0.05.txt");
		normNet = loadNetwork("norm_09.out");
		tumorNet = loadNetwork("tumor_08.out");
		semNet = loadNetwork("sem_07.out");
		try {
			sigPathway = new pathwaySim().getSigPathway("sigpathway.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param type
	 */
	public void pathwaySim(String type) {
		if (type == null || "".equals(type)) {
			throw new RuntimeException("Type cannot be null");
		}
		String realFile = "dataRepository" + File.separator + "pathwaysim_"
				+ type + "_";
		if(type.equalsIgnoreCase("norm"))
		{
			simCalculation(normNet, realFile + ".csv");
		}else if(type.equalsIgnoreCase("tumor")){

			simCalculation(tumorNet, realFile + ".csv");
		}else if(type.equalsIgnoreCase("sem")){

			simCalculation(semNet, realFile + ".csv");
		}else{
			throw new RuntimeException("type should be one of [norm,tumor,sem], not"+type);
		}
	}

	/**
	 * 相似性计算
	 * 
	 * @param dataIn
	 * @param fileName
	 */
	public void simCalculation(HashMap<String, Double> dataIn, String fileName) {
		Object[] objs = sigPathway.keySet().toArray();
		int npaths = objs.length;
		CsvWriter cw = new CsvWriter(fileName, ',', Charset.forName("UTF-8"));
		for (int i = 0; i < npaths - 1; i++) {
			HashSet<String> source = pathwayOverlap(sigPathway.get(objs[i].toString()),geneSet);
			for (int j = 0; j < npaths; j++) {
				HashSet<String> target = pathwayOverlap(sigPathway.get(objs[j].toString()),geneSet);
				// 相似性计算
				double sim = geneSetSim(source, target, dataIn);
				// 存储数据到csv文件中
				String[] contents = { objs[i].toString(), objs[j].toString(),
						String.valueOf(sim) };
				try {
					cw.writeRecord(contents);
				} catch (IOException e) {
					e.printStackTrace();
				}
				LogUtils.log(objs[i].toString()+"["+i+"]"+objs[j].toString()+"["+j+"] ["+npaths+"]");
			}
		}
		cw.close();
	}

	/**
	 * pathway overlap
	 * @param source
	 * @param target
	 * @return  HashSet<String>
	 */
	public HashSet<String> pathwayOverlap(HashSet<String> source,HashSet<String> target) {
		HashSet<String> inter = new HashSet<String>();
		for (String s : source) {
			inter.add(s);
		}
		inter.retainAll(target);
		return inter;
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param dataIn
	 * @return double
	 */
	public double geneSetSim(HashSet<String> source, HashSet<String> target,HashMap<String, Double> dataIn) {
		double d = 0.0;
		HashMap<String, Double> back = new HashMap<String, Double>();
		for(int i=0;i<source.size();i++){
			Object [] obj = source.toArray();
			double tmp = 0.0;
			for(int j=0;j<target.size();j++){
				Object objt = target.toArray()[j];
				String key = obj[i].toString() + ":" + objt.toString();
				if (dataIn.keySet().contains(key)) {
					tmp = Math.max(tmp, dataIn.get(key).doubleValue());
					// 存储当前j所在基因的最大相似值
					Double dobj = back.containsKey(objt.toString())?Double.valueOf(Math.max(back.get(objt.toString()).doubleValue(),
							dataIn.get(key).doubleValue())):dataIn.get(key);
					back.put(objt.toString(), dobj);
				}
			}
			d += tmp;
		}
		for (String s : back.keySet()) {
			d += back.get(s).doubleValue();
		}
		return d / (source.size() + target.size() + 0.001);
	}
	/**
	 * Load the network
	 * 
	 * @param fileName
	 * @return HashMap<String, Double>
	 */
	public HashMap<String, Double> loadNetwork(String fileName) {
		String realFile = "dataRepository" + File.separator + fileName;
		try {
			return (HashMap<String, Double>) objserialize.readObj(realFile);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void removeRepeats(String fileName) {
		String outFile = fileName.replace(".csv", "_clean.csv");
		HashSet<String> back = new HashSet<String>();
		try {
			CsvReader cr = new CsvReader("dataRepository/" + fileName);
			CsvWriter cw = new CsvWriter("dataRepository/"+outFile,',',Charset.forName("UTF-8"));
			while (cr.readRecord()) {
				String[] line = cr.getValues();
				String key = line[0]+":"+line[1];
				if(line[0].equals(line[1])){
					continue;
				}
				if (!back.contains(key)) {
					back.add(key);
					back.add(line[1] + ":" + line[0]);
					line[0] = modifString(line[0]);
					line[1] = modifString(line[1]);
					cw.writeRecord(line);
				}

			}
			cw.close();
			cr.close();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param term
	 * @return String
	 */
	public String modifString(String term) {
		String[] terms = term.split("\\_");
		return terms[0] + "_" + terms[terms.length - 1];
	}

	/**
	 * 
	 * @param pathwayFile
	 */
	public void pathwayOverlapStatistics(String[] pathwayFile) {
		HashMap<String, HashSet<String>> map = new HashMap<String, HashSet<String>>();
		// read all the data
		for (String file : pathwayFile) {
			String realFile = "dataRepository" + File.separator + file;
			try {
				CsvReader cr = new CsvReader(realFile);
				HashSet<String> inner = new HashSet<String>();
				while (cr.readRecord()) {
					String[] items = cr.getValues();
					inner.add(items[0] + "+" + items[1]);
				}
				map.put(file, inner);
			} catch (FileNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		// print all the statistics
		Object [] objs = map.keySet().toArray();
		for(int i=0;i<objs.length-1;i++)
		{
			for(int j=i+1;j<objs.length;j++){
				HashSet<String> inter = pathwayOverlap(map.get(objs[i].toString()),map.get(objs[j].toString()));
				LogUtils.log("=====Overlap "+objs[i].toString()+" and "+objs[j].toString()+":");
				LogUtils.log(objs[i].toString() + " edges:"
						+ map.get(objs[i].toString()).size() + " Nodes:"
						+ getNodes(map.get(objs[i].toString())).size());

				LogUtils.log(objs[j].toString() + " edges:"
						+ map.get(objs[j].toString()).size() + " Nodes:"
						+ getNodes(map.get(objs[j].toString())).size());
				LogUtils.log("Overlap edges:"
						+ inter.size()
						+ " nodes:"
						+ pathwayOverlap(getNodes(map.get(objs[i].toString())),
								getNodes(map.get(objs[j].toString()))).size());
				LogUtils.log("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			}
		}
	}

	/**
	 * 
	 * @param edges
	 * @return HashSet<String>
	 */
	public HashSet<String> getNodes(HashSet<String> edges) {
		HashSet<String> nodes = new HashSet<String>();
		for (String s : edges) {
			String[] items = s.split("\\+");
			nodes.add(items[0]);
			nodes.add(items[1]);
		}
		return nodes;
	}

	public void pathwayOutput2Csv(String fileName) {
		String realFile = "dataRepository" + File.separator + fileName;
		CsvWriter cw = new CsvWriter(realFile, ',', Charset.forName("UTF-8"));

		for(String pathway:sigPathway.keySet()){
			String[] ms = modifString(pathway).split("\\_");
			String[] contents = { ms[0], ms[1],
					String.valueOf(sigPathway.get(pathway).size()) };
			try {
				cw.writeRecord(contents);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		cw.close();
		LogUtils.log("Write to Csv Done!");
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		PathwayNetwork pn = new PathwayNetwork();
		// pn.pathwaySim("norm");
		// pn.pathwaySim("tumor");
		// pn.pathwaySim("sem");
		// System.out.println(pn.modifString("FOXM1 transcription factor network_foxm1pathway_PID"));
		// pn.removeRepeats("path_tumor_04.csv");
		// pn.removeRepeats("path_sem_04.csv");
		// String[] sarr = { "path_norm_02_clean.csv",
		// "path_tumor_04_clean.csv", "path_sem_04_clean.csv" };
		//
		// pn.pathwayOverlapStatistics(sarr);
		pn.pathwayOutput2Csv("sigPathwayDesp.csv");
	}
}
