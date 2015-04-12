package sim.idconvert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import sim.io.objserialize;

/**
 * 
 * @author mingchen
 * @date 2015.4.10
 * 
 */
public class idconverter {

	private static final String wdir = "d:/tmp/bioinfo/aftermid/";

	/**
	 * 
	 * 
	 * @return Statement
	 * 
	 *         Connet to local Mysql db
	 */
	public static Statement ConnectToDB() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/geneonto";
		try {
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, "user", "passwd");
			return conn.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * 
	 * @param fileName
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws SQLException
	 * 
	 *             package all the symbol to go including synonyms
	 */
	public static void symbol2go(String fileName) throws IOException,
	FileNotFoundException, SQLException {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		objserialize out = new objserialize("gene2go.out");
		BufferedReader br = new BufferedReader(new FileReader(new File(
				wdir+fileName)));
		String line = br.readLine();
		while (line != null) {
			if (line.startsWith("!")) {
				line = br.readLine();
				continue;
			}
			String[] arr = line.split("\t");
			String gene = arr[2], go = arr[4];
			if (map.containsKey(gene)) {
				Set<String> list = map.remove(gene);
				list.add(go);
				map.put(gene, list);

			} else {
				Set<String> list = new LinkedHashSet<String>();
				list.add(go);
				map.put(gene, list);
			}
			System.out.println(gene + " " + go + " packaged done");
			line = br.readLine();
		}

		out.objOutput(map);
	}

	public static void extendSymbol2Go() throws ClassNotFoundException,
	IOException, SQLException {
		Statement stmt = ConnectToDB();
		Map<String, LinkedHashSet<String>> map = (HashMap<String, LinkedHashSet<String>>) objserialize
				.readObj("gene2go.out");
		Map<String, LinkedHashSet<String>> obj = new HashMap<String, LinkedHashSet<String>>();
		for (String gene : map.keySet()) {
			ResultSet rs = stmt
					.executeQuery("select synonyms from hgnc where approved_symbol ='"
							+ gene + "';");
			obj.put(gene, map.get(gene));
			while(rs.next()){
				obj.put(rs.getString("synonyms"), map.get(gene));
			}
			rs.close();
			System.out.println(gene + " append done");
		}
		stmt.close();
		objserialize out = new objserialize("gene2go.out");
		out.objOutput(obj);

	}
	public static void main(String[] args) throws FileNotFoundException,
	IOException, SQLException, ClassNotFoundException {
		// idconverter.symbol2go("gene_association.goa_human");
		// idconverter.extendSymbol2Go();

		Map<String, LinkedHashSet<String>> map = (HashMap<String, LinkedHashSet<String>>) objserialize
				.readObj("gene2go.out");
		System.out.println(map.size());
		// for (String s : map.keySet()) {
		// System.out.print(s + "<");
		// for (String go : map.get(s)) {
		// System.out.print(go + ",");
		// }
		// System.out.print(">\n");
		// }
	}

}
