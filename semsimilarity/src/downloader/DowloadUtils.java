package downloader;

public class DowloadUtils {

	public static void download(String url) {
		DownloadInfo bean = new DownloadInfo(url);
		LogUtils.info(bean);
		BatchDownloadFile down = new BatchDownloadFile(bean);
		new Thread(down).start();

	}

	public static void download(String url, int threadNum) {
		DownloadInfo bean = new DownloadInfo(url, threadNum);
		LogUtils.log(bean);
		BatchDownloadFile down = new BatchDownloadFile(bean);
		new Thread(down).start();
	}

	public static void download(String url, String fileName, String filePath,
			int threadNum) {
		DownloadInfo bean = new DownloadInfo(url, fileName, filePath, threadNum);
		BatchDownloadFile down = new BatchDownloadFile(bean);
		new Thread(down).start();
	}
}
