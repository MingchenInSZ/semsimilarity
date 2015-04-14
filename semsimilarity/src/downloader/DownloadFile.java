package downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class DownloadFile extends Thread {
	public String url;
	public long startPos;
	private final  long endPos;
	private final  int threadId;
	private boolean isDownloadOver = false;
	public final SaveItemFile itemFile;
	private static final int BUFF_LENGTH = 1024 * 8;
	private int downloadSize = 0;
	private long timeConsum;
	private final long finalStartPos;

	/**
	 * 
	 * @param url
	 * @param name
	 * @param startPos
	 * @param endPos
	 * @param threadId
	 * @throws IOException
	 */
	public DownloadFile(String url, String name, long startPos, long endPos,
			int threadId) throws IOException {
		super();
		this.url = url;
		this.startPos = startPos;
		finalStartPos = startPos;
		this.endPos = endPos;
		this.threadId = threadId;
		itemFile = new SaveItemFile(name, startPos);

	}

	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		while (startPos < endPos && !isDownloadOver) {
			try {
				URL url = new URL(this.url);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(10000);
				setHeader(conn);
				String property = "bytes=" + startPos + "-";
				conn.setRequestProperty("RANGE", property);
				LogUtils.log("Start " + threadId + ":" + property + endPos);
				InputStream is = conn.getInputStream();
				byte[] buff = new byte[BUFF_LENGTH];
				int length = -1;
				LogUtils.log("#start#Thread:"+threadId+",startPos:"+startPos+",endPos:"+endPos);
				int cur = 0;
				while ((length = is.read(buff)) > 0 && startPos < endPos
						&& !isDownloadOver) {
					startPos += itemFile.write(buff, 0, length);
					// System.out.println(buff.toString());
					downloadSize += length;
				}
				LogUtils.log("over#Thread:" + threadId + ",startPos:"
						+ startPos + ",endPos:" + endPos);
				LogUtils.log("Thread:" + threadId + " execute over!");
				isDownloadOver = true;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SocketTimeoutException e) {
				LogUtils.log("Time out.. retrying...." + threadId);
			} catch (IOException e) {

				e.printStackTrace();
			}

		}
		long endTime = System.currentTimeMillis();
		timeConsum = endTime - startTime;
		if (endPos <= startPos && !isDownloadOver) {
			LogUtils.log("Thread:" + threadId + ",startPos:" + startPos
					+ ",endPos:" + endPos + "[startPos>=endPos, not need download file]");
			isDownloadOver = true;
		}

	}

	/**
	 * 
	 * @param conn
	 *            print header information
	 */
	public static void printHeader(URLConnection conn) {
		Map<String, List<String>> map = conn.getHeaderFields();
		for (String key : map.keySet()) {
			LogUtils.log(key + ":" + conn);
		}
	}

	/**
	 * 
	 * @param conn
	 * 
	 *            Set all the parameters of connection
	 */
	public static void setHeader(HttpURLConnection conn) {
		if (conn != null) {
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0(windows 7 home)Firefox/3.0.3");
			conn.setRequestProperty("Accept-Language",
					"en-us,en;q=0.7,zh-cn;q=0.3");
			conn.setRequestProperty("Accept-Encoding", "utf-8");
			conn.setRequestProperty("Accept-Charset",
					"ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			conn.setRequestProperty("Keep-Alive", "300");
			conn.setRequestProperty("connnection", "keep-alive");
			conn.setRequestProperty("If-Modified-Since",
					"Fri, 02 Jan 2009 17:00:05 GMT");
			conn.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
			conn.setRequestProperty("Cache-conntrol", "max-age=0");
		}
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isDownloadOver() {
		return isDownloadOver;
	}

	/**
	 * @return long
	 */
	public long getStartPos() {
		return finalStartPos;
	}

	/**
	 * 
	 * @return long
	 */
	public long getEndPos() {
		return endPos;
	}
	/**
	 * @return int
	 */
	public int getBlockSize(){
		return downloadSize;
	}

	/**
	 * 
	 * @return int
	 */
	public int getThreadID() {
		return threadId;
	}

	/**
	 * 
	 * @return long
	 */
	public long getConsumedTime() {
		return timeConsum;
	}

}
