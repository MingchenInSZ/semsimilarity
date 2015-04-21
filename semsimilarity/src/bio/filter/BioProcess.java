package bio.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import sim.idconvert.idconverter;
import sim.io.objserialize;
import downloader.LogUtils;

public class BioProcess {

	private final static String wdir = "dataRepository";
	private int totalDistinctGenes;
	public BioProcess() {
	}

	/**
	 * 
	 * @param fileName
	 * 
	 *            dump all the unique genes in hgnc database
	 */
	private void dumpAllGenes(String fileName) {
		String realFileName = wdir+File.separator+fileName;
		HashMap<String, String> map = new HashMap<String, String>();
		Statement stmt = idconverter.ConnectToDB();
		String sql = "select approved_symbol,synonyms from hgnc; ";
		try {
			ResultSet rs = stmt.executeQuery(sql);

			int count = 1;
			while (rs.next()) {
				String app_sym = rs.getString("approved_symbol");
				String gene = "";
				if (app_sym.contains("withdraw")) {
					gene = app_sym.substring(0, app_sym.indexOf("~"));
				} else {
					gene = app_sym;
				}
				map.put(String.valueOf(count++), gene);
			}
			System.out.println("dump total genes:" + map.size());
			totalDistinctGenes = map.size();
			new objserialize(realFileName).objOutput(map);
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 * @param fileName
	 * @return HashMap
	 * 
	 */
	public HashMap<String, String> loadPathways(String fileName) {
		String realFileName = wdir + File.separator + fileName;
		try {
			return (HashMap<String, String>) objserialize.readObj(realFileName);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Load the Norm network
	 * 
	 * @param fileName
	 * 
	 */
	public void dumpNormNet(String fileName) {
		String realFile = wdir+File.separator+fileName;
		HashMap<String, Double> map = new HashMap<String, Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(realFile)));
			String line = br.readLine();
			while (line != null) {
				String[] items = line.trim().split("\t");
				map.put(items[0] + ":" + items[1], Double.valueOf(items[2]));
				line = br.readLine();
			}
			String version = fileName.substring(fileName.lastIndexOf("_"),fileName.indexOf("."));
			new objserialize(wdir+File.separator+"norm" + version + ".out").objOutput(map);
			LogUtils.log("Norm net dump done [" + version + "]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Load the Tumor network
	 * 
	 * @param fileName
	 * 
	 */
	public void dumpTumorNet(String fileName) {
		String realFile = wdir+File.separator+fileName;
		HashMap<String, Double> map = new HashMap<String, Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(realFile)));
			String line = br.readLine();
			while (line != null) {
				String[] items = line.trim().split("\t");
				map.put(items[0] + ":" + items[1], Double.valueOf(items[2]));
				line = br.readLine();
			}
			String version = fileName.substring(fileName.lastIndexOf("_"),fileName.indexOf("."));
			new objserialize(wdir+File.separator+"tumor" + version + ".out").objOutput(map);
			LogUtils.log("Tumor net dump done [" + version + "]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Load sem network;
	 * 
	 * @param fileName
	 * 
	 */
	public void dumpSemNet(String fileName) {
		String realFile = wdir+File.separator+fileName;
		HashMap<String, Double> map = new HashMap<String, Double>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(realFile)));
			String line = br.readLine();
			while (line != null) {
				String[] items = line.trim().split("\t");
				map.put(items[0] + ":" + items[1], Double.valueOf(items[2]));
				line = br.readLine();
			}
			String version = fileName.substring(fileName.lastIndexOf("_"),fileName.indexOf("."));
			new objserialize(wdir+File.separator+"sem" + version + ".out").objOutput(map);
			LogUtils.log("Sem net dump done [" + version + "]");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return int
	 */
	public int getTotalDistinctGenes() {
		return totalDistinctGenes;
	}

	/**
	 * The Main function
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BioProcess bio = new BioProcess();
		// bio.dumpAllGenes("allUniqueGenes.out");
		bio.dumpNormNet("norm_trim_09.txt");
		bio.dumpTumorNet("tumor_trim_08.txt");
		bio.dumpSemNet("dif_trim_07.txt");
	}
}
