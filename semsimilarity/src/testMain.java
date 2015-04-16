import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import sim.idconvert.idconverter;
import sim.util.pathwaySim;
import downloader.LogUtils;


public class testMain {

	public static void main(String[] args) {
		pathwaySim psim = new pathwaySim();
		HashMap<String,LinkedHashSet<String>> pathways = psim.getPathways("cpdbpathway.gmt");
		HashSet<String> genes = new HashSet<String>();
		for (String s : pathways.keySet()) {
			for (String g : pathways.get(s)) {
				genes.add(g);
			}
		}
		LogUtils.log("Total distinct genes:" + genes.size());
		idconverter idc = new idconverter();
		Statement stmt = idc.ConnectToDB();
		try {
			ResultSet rs = stmt
					.executeQuery("select approved_symbol from hgnc where status = \"Approved\";");
			while (rs.next()) {
				String sym = rs.getString("approved_symbol");
				genes.remove(sym);

			}
			rs.close();
			stmt.close();
			LogUtils.log("Left size:" + genes.size());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
