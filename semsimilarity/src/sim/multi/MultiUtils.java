package sim.multi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import downloader.LogUtils;

public class MultiUtils {
	public static void main(String[] args) {
		MultiCalculation mc = new MultiCalculation("difgenesamr0.05.txt", 12,
				"difGeneSim.txt");
		Thread main = new Thread(mc);
		main.start();
		combineFiles("difGeneSim.txt", 12);

	}

	public static void combineFiles(String prefix,int appendix){
		try
		{
			String fileName = "dataRepository"+File.separator+prefix;
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
			String eol = new Properties(System.getProperties()).getProperty("line.separator");
			for(int i = 0;i<appendix;i++){
				File file = new File(fileName + String.valueOf(i));
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line = br.readLine();
				while(line!=null){
					bw.write(line + eol);
					line = br.readLine();
				}
				br.close();
				file.delete();
				LogUtils.log(file.getName() + " combined ");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
