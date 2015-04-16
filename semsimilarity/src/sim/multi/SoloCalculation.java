package sim.multi;

import java.io.IOException;
import java.util.Properties;

import sim.wang.SemSimDump;
import downloader.LogUtils;

public class SoloCalculation extends Thread {

	private final int startPos;
	private final int endPos;
	private final int threadId;
	public final SaveFile sFile;
	private final Object[] objs;
	private final SemSimDump ssd;
	private final int length;
	private boolean isOver = false;
	private int count = 0;

	public SoloCalculation(String fileName, int startPos, int endPos,
			int threadId, int length, Object[] objs, SemSimDump ssd) {
		this.startPos = startPos;
		this.endPos = endPos;
		this.threadId = threadId;
		this.objs = objs;
		this.ssd = ssd;
		this.length = length;
		sFile = new SaveFile(fileName + String.valueOf(threadId));
	}

	@Override
	public void run() {
		int start = startPos;
		while (start < endPos && !isOver) {
			try {
				for (int i = start + 1; i < length; i++) {
					float sim = ssd.geneSemSim(objs[start].toString(),objs[i].toString());
					String line = objs[start].toString()+"\t"+objs[i].toString()+"\t"+String.valueOf(sim)
							+ new Properties(System.getProperties()).getProperty("line.separator");
					sFile.write(line);
					if (count > 0 && count % 1000 == 0) {
						LogUtils.log("Thread ID" + threadId + " " + count);
					}
					count += 1;
				}
				start += 1;
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		isOver = true;
		LogUtils.log("Thread ID" + threadId + " Done!");

	}

	public boolean isOver() {
		return isOver;
	}

	public int getDoneCount() {
		return count;
	}
}
