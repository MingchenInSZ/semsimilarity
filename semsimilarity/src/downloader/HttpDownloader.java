package downloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author mingchen
 * @date 2015.4.13
 * 
 */
public class HttpDownloader extends Thread {
	private String urlStr = "";

	public void download(String url) {
		urlStr = url;
		int length = getFileSize();
		for (int i = 0; i < 12; i++) {
			new InnerThread(url, i * 2517008, (i + 1) * 2517008, i).start();
		}
		System.out.println("All Done");
		System.exit(0);
	}

	/**
	 * 
	 * @return int
	 * 
	 *         get the size of the file
	 */
	public int getFileSize() {
		int len = 0;

		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			setHeader(conn);
			String length = conn.getHeaderField("Content-Length");
			len = Integer.valueOf(length).intValue();
		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return len;
	}

	/**
	 * 
	 * @param conn
	 * 
	 *            Set all the parameters of connection
	 */
	public void setHeader(HttpURLConnection conn) {
		if(conn!=null){
			conn.setRequestProperty("User-Agent", "Mozilla/5.0(windows 7 home)Firefox/3.0.3");
			conn.setRequestProperty("Accept-Language","en-us,en;q=0.7,zh-cn;q=0.3");
			conn.setRequestProperty("Accept-Encoding", "utf-8");
			conn.setRequestProperty("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.7");
			conn.setRequestProperty("Keep-Alive", "300");
			conn.setRequestProperty("connnection", "keep-alive");
			conn.setRequestProperty("If-Modified-Since","Fri, 02 Jan 2009 17:00:05 GMT");
			conn.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
			conn.setRequestProperty("Cache-conntrol", "max-age=0");
		}
	}

	/**
	 * 
	 * @return String
	 * @throws Exception
	 * 
	 *             get file name from url(@exp  www.baidu.com/webfile,return webfile)
	 */
	public String getWebFileName(String urlStr) {
		String name = "";
		String[] seps = urlStr.split("/");
		if (seps.length >= 1) {
			name = seps[seps.length - 1];
		} else {
			try {
				throw new Exception("Maybe it is not a file path");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return name;
	}

	public static void main(String[] args) throws Exception {
		HttpDownloader hd = new HttpDownloader();
		hd.download("http://geneontology.org/ontology/go-basic.obo");
		// System.out.println(hd.getWebFileName("http://geneontology.org/ontology/go-basic.obo"));
	}

	class InnerThread extends Thread {
		private final String url;
		private int start;
		private final int end;
		private final int threadId;
		private RandomSaveFile rsf = null;

		public InnerThread(String url, int start, int end, int threadId) {
			this.url = url;
			this.start = start;
			this.end = end;
			this.threadId = threadId;
			try {
				rsf = new RandomSaveFile(getWebFileName(url), start);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				System.out.println("Block <" + threadId + "> downloading..");
				URL realUrl = new URL(url);
				int buff_length = 1024 * 8;
				HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
				setHeader(conn);
				// conn.setConnectTimeout(10000);
				String property = "bytes=" + start + "-";
				conn.setRequestProperty("RANGE", property);
				InputStream is = conn.getInputStream();
				byte[] buff = new byte[buff_length];
				int length = -1;
				while ((length = is.read(buff)) > 0 && start < end) {
					start += rsf.write(buff, 0, length);
					// System.out.println(start + " " + end + " in thread "+
					// threadId);
				}
				System.out.println("Block <" + threadId + " done!");
				is.close();
				rsf.close();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
