package downloader;

/**
 * 
 * @author mingchen
 * @date 2015.4.13
 * 
 */
public class DownloadInfo {
	// download information
	private String url;
	private String fileName;
	private String filePath;
	private int splitter;
	private final static String FILE_PATH = "dataRepository";
	private final static int SPLITTER_NUM = 5;

	/**
	 * 
	 */
	public DownloadInfo() {
		super();
	}

	/**
	 * 
	 * @param url
	 */
	public DownloadInfo(String url) {
		this(url, null, null, SPLITTER_NUM);
	}

	/**
	 * 
	 * @param url
	 * @param splitter
	 */
	public DownloadInfo(String url, int splitter) {
		this(url, null, null, splitter);
	}

	/**
	 * 
	 * @param url
	 * @param fileName
	 * @param filePath
	 * @param splitter
	 */
	public DownloadInfo(String url, String fileName, String filePath,
			int splitter) {
		super();
		if (url == null || "".equals(url)) {
			throw new RuntimeException("URL is not null!");
		}
		this.url = url;
		this.fileName = fileName == null || "".equals(fileName) ? getFileName(url):fileName;
		this.filePath = filePath == null||"".equals(filePath)?FILE_PATH:filePath;
		this.splitter = splitter<1?SPLITTER_NUM:splitter;
	}

	/**
	 * 
	 * @param url
	 * @return String
	 */
	private String getFileName(String url){
		return url.substring(url.lastIndexOf("/") + 1, url.length());
	}

	/**
	 * 
	 * @return String
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		if (url == null || "".equals(url)) {
			throw new RuntimeException("URL is not null!");
		}
		this.url = url;
	}

	/**
	 * 
	 * @return String
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName == null || "".equals(fileName) ? getFileName(url)
				: fileName;
	}

	/**
	 * 
	 * @return String
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * 
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath == null || "".equals(filePath) ? FILE_PATH
				: filePath;
	}

	/**
	 * 
	 * @return int
	 */
	public int getSplitter() {
		return splitter;
	}

	/**
	 * 
	 * @param splitter
	 */
	public void setSplitter(int splitter) {
		this.splitter = splitter < 1 ? SPLITTER_NUM : splitter;
	}

	/**
	 * override toStirng
	 */
	@Override
	public String toString() {
		return url + "#" + fileName + "#" + filePath + "#" + splitter;
	}
}
