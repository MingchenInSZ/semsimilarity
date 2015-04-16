package sim.multi;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import sim.util.GeneSimNetwork;
import sim.wang.SemSimDump;
import bio.filter.NumberFormatter;
import downloader.LogUtils;

public class MultiCalculation implements Runnable {
	private final HashSet<String> genes;
	private final int length;
	private int split = 6;
	private final String fileName;
	private boolean isStop = false;
	private final int[] starts;
	private final int[] ends;
	private SoloCalculation[] items;
	private SemSimDump ssd;

	public MultiCalculation(String fileName, int split, String storeFile) {
		if (split < 6) {
			split = 6;
		}
		this.fileName = "dataRepository" + File.separator + storeFile;
		genes = new GeneSimNetwork().getGenes(fileName);
		length = genes.size();
		this.split = split;
		starts = new int[split];
		ends = new int[split];
		try {
			ssd = new SemSimDump();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		for (int i = 0; i < split; i++) {
			int size = length / split;
			starts[i] = i * size;
			if (i == split - 1) {
				ends[i] = length;
			} else {
				ends[i] = (i + 1) * size;
			}
		}

		if (!isStop) {
			items = new SoloCalculation[split];
			for (int i = 0; i < split; i++) {
				items[i] = new SoloCalculation(fileName, starts[i], ends[i], i,
						length, genes.toArray(), ssd);
				items[i].start();
				LogUtils.log("Thread ID:" + i + " starting...");
			}

			while (!isStop) {
				boolean tag = true;
				for (int i = 0; i < split; i++) {
					if (!items[i].isOver()) {
						tag = false;
						break;
					}
				}
				int t = getCount();
				if (t % 3000 == 0) {
					LogUtils.log("Total :"+NumberFormatter.decimalFormat("#.00%", 2.0*t/(length*(length-1))));
				}
				isStop = tag;
			}
		}
		for (SoloCalculation s : items) {
			s.sFile.close();
		}
		LogUtils.log("work done!");
	}

	public int getCount() {
		int total = 0;
		for (SoloCalculation s : items) {
			total += s.getDoneCount();
		}
		return total;
	}

}
