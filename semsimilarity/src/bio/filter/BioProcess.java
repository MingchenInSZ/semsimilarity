package bio.filter;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import sim.idconvert.idconverter;
import sim.io.objserialize;

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
	 * 
	 * @return int
	 */
	public int getTotalDistinctGenes() {
		return totalDistinctGenes;
	}
	public static void main(String[] args) {
		BioProcess bio = new BioProcess();
		bio.dumpAllGenes("allUniqueGenes.out");
	}
}
