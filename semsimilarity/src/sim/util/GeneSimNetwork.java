package sim.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

import sim.wang.SemSimDump;
import downloader.LogUtils;

public class GeneSimNetwork {

	private static final String wdir = "dataRepository";

	/**
	 * 
	 * @param fileName
	 * @return HashSet<String>
	 * 
	 *         Read genes
	 */
	public HashSet<String> getGenes(String fileName) {
		HashSet<String> difGenes = new HashSet<String>();
		String realFile = wdir + File.separator + fileName;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					realFile)));
			String line = br.readLine();
			while (line != null) {
				difGenes.add(line.trim());
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return difGenes;
	}

	public void dumpGeneSim(String fileName) {
		String realFile = wdir + File.separator + fileName;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(realFile)));
			HashSet<String> genes = getGenes("difgenesamr0.05.txt");
			Object[] objs = genes.toArray();
			SemSimDump ssd = new SemSimDump();
			for (int i = 0; i < objs.length - 1; i++) {
				for (int j = i + 1; j < objs.length; j++) {
					float f = ssd.geneSemSim(objs[i].toString(),
							objs[j].toString());
					bw.write(objs[i].toString()
							+ "\t"
							+ objs[j].toString()
							+ "\t"
							+ String.valueOf(f)
							+ new Properties(System.getProperties())
					.getProperty("line.separator"));
					LogUtils.log(objs[i].toString()+"_"+i+":"+objs[j].toString()+"_"+j+" ["+f+"]");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		GeneSimNetwork gsn = new GeneSimNetwork();
		gsn.dumpGeneSim("difGeneSim.txt");
	}

}
