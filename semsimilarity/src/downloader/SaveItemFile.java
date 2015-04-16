package downloader;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 
 * @author mingchen
 * @date 2015.4.13
 * 
 */
public class SaveItemFile {
	private final RandomAccessFile itemFile;

	/**
	 * 
	 * @throws IOException
	 */
	public SaveItemFile() throws IOException {
		this("", 0);
	}

	/**
	 * 
	 * @param name
	 * @param pos
	 * 
	 * @throws IOException
	 */
	public SaveItemFile(String name,long pos) throws IOException{
		itemFile = new RandomAccessFile(name, "rw");
		itemFile.seek(pos);

	}

	/**
	 * 
	 * @param buff
	 * @param start
	 * @param length
	 * @return int
	 */
	public synchronized int write(byte[] buff,int start,int length){
		int i = -1;
		try{
			itemFile.write(buff, start, length);
			i = length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (itemFile != null) {
			itemFile.close();
		}
	}

}
