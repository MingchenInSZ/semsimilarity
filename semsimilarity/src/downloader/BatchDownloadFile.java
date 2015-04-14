package downloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class BatchDownloadFile implements Runnable {

	private final DownloadInfo downloadInfo;
	private long[] startPos;
	private long[] endPos;
	private static final int SLEEP_SECONDS = 3000;
	private DownloadFile[] fileItem;
	private int length;
	private boolean first = true;
	private boolean stop = false;

	private final File tempFile;

	public BatchDownloadFile(DownloadInfo downloadInfo) {
		this.downloadInfo = downloadInfo;
		String tempPath = this.downloadInfo.getFilePath() + File.separator
				+ this.downloadInfo.getFileName() + ".position";
		tempFile = new File(tempPath);
		if (tempFile.exists()){
			first = false;
			try{
				readPosInfo();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			startPos = new long[downloadInfo.getSplitter()];
			endPos = new long[downloadInfo.getSplitter()];

		}
	}

	@Override
	public void run() {
		if (first) {
			length = getFileSize();
			if (length == -1) {
				LogUtils.log("file length is know!");
				stop = true;
			} else if (length == -2) {
				LogUtils.log("read file length is error");
				stop = true;
			} else if (length > 0) {
				for (int i = 0, len = startPos.length; i < len; i++) {
					int size = i * (length / len);
					startPos[i] = size;
					if (i == len - 1) {
						endPos[i] = length;
					} else {
						size = (i + 1) * (length / len);
						endPos[i] = size;
					}
					LogUtils.log("start-end position:["+i+"]"+startPos[i]+"-"+endPos[i]);
				}
			} else {
				LogUtils.log("get file length is error, download is stop!");
				stop = true;
			}
		}
		if(!stop){
			fileItem = new DownloadFile[startPos.length];
			for(int i =0;i<startPos.length;i++){
				try{
					fileItem[i] = new DownloadFile(downloadInfo.getUrl(),downloadInfo.getFilePath()+File.separator+downloadInfo.getFileName(),startPos[i],endPos[i],i);
					fileItem[i].start();
					LogUtils.log("Thread:" + i + ",startPos:" + startPos[i]
							+ ",endPos:" + endPos[i] + " started!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			int older = 0;
			while (!stop) {
				try {
					int per = (int) (getDownload() * 1.0 / length * 100) % 100;
					if (per - older >= 1) {
						LogUtils.log("downloading...[" + per + "%]");
						older = per;
					}
					Thread.sleep(SLEEP_SECONDS);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				boolean isStop = true;
				for (int i = 0; i < startPos.length; i++) {
					if (!fileItem[i].isDownloadOver()) {
						isStop = false;
						break;
					}
				}
				stop = isStop;
			}
			try {
				writePosInfo();
				for(int i=0;i<startPos.length;i++)
				{
					fileItem[i].itemFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			LogUtils.info("Download task is finished!");
			getStatistics();
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void writePosInfo()throws IOException{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(tempFile));
		dos.writeInt(startPos.length);
		for (int i = 0; i < startPos.length; i++) {
			dos.writeLong(fileItem[i].getStartPos());
			dos.writeLong(fileItem[i].getEndPos());
			LogUtils.info("["+fileItem[i].getStartPos()+"#"+fileItem[i].getEndPos()+"]");
		}
		dos.close();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readPosInfo() throws IOException {

		DataInputStream dis = new DataInputStream(new FileInputStream(tempFile));
		int startPosLength = dis.readInt();
		startPos = new long[startPosLength];
		endPos = new long[startPosLength];
		for (int i = 0; i < startPosLength; i++) {
			startPos[i] = dis.readLong();
			endPos[i] = dis.readLong();
		}
		dis.close();
	}

	/**
	 * 
	 * @return int
	 * 
	 *         get the file size
	 */
	private int getFileSize() {
		int fileLength = -1;
		try {
			URL url = new URL(downloadInfo.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			DownloadFile.setHeader(conn);
			int stateCode = conn.getResponseCode();
			if (stateCode != HttpURLConnection.HTTP_OK
					&& stateCode != HttpURLConnection.HTTP_PARTIAL) {
				LogUtils.log("Error Code:" + stateCode);
				return -2;
			} else if (stateCode >= 400) {
				LogUtils.log("Error Code:" + stateCode);
				return -2;
			} else {
				fileLength = conn.getContentLength();
				LogUtils.log("FileLength:" + fileLength);
			}
			DownloadFile.printHeader(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return fileLength;
	}

	/**
	 * 
	 * @return int
	 * 
	 *         get the downloaded filesize(total)
	 */
	private int getDownload() {
		int size = 0;
		for(DownloadFile df:fileItem){
			size += df.getBlockSize();
		}
		return size;
	}

	public void getStatistics() {
		for (DownloadFile df : fileItem) {
			LogUtils.log("Thread:       " + df.getThreadID());
			LogUtils.log("Download size:" + (df.getEndPos() - df.getStartPos())+" bytes");
			LogUtils.log("Time lapse:   " + df.getConsumedTime());
			double speed = (df.getEndPos() - df.getStartPos())*1.0/df.getConsumedTime();
			// String print = String.format("%5.3d", speed);
			LogUtils.log("Speed:        " + new DecimalFormat("#.00").format(speed));
			LogUtils.log("--------------------------------------------");
		}
	}

}
