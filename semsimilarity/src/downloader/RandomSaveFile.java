package downloader;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author mingchen
 * @date 2015.4.13
 * 
 *       save file by randomaccess
 */
public class RandomSaveFile {

	private RandomAccessFile raf = null;

	public RandomSaveFile() throws IOException {

	}

	/**
	 * 
	 * @param name
	 * @param pos
	 * @throws IOException
	 * 
	 */
	public RandomSaveFile(String name, long pos) throws IOException {
		raf = new RandomAccessFile("dataRepository/" + name, "rw");
		raf.seek(pos);

	}

	/**
	 * 
	 * @param buff
	 * @param start
	 * @param length
	 * @return int
	 * 
	 * 
	 */
	public synchronized int write(byte[] buff, int start, int length) {
		int i = -1;
		try {
			raf.write(buff, start, length);
			i = length;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * close the randomaccessfile raf
	 */
	public void close() {
		try {
			if (raf != null) {
				raf.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
