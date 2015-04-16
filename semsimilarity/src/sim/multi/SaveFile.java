package sim.multi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SaveFile {
	// file instance
	private RandomAccessFile sFile;

	/**
	 * Constructor function with fileName as parameter
	 * 
	 * @param fileName
	 */
	public SaveFile(String fileName) {
		try {
			sFile = new RandomAccessFile(fileName, "rwd");
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	}

	/**
	 * synchronized method to write a string content
	 * 
	 * @param content
	 */
	public synchronized void write(String content) {
		try {
			sFile.writeBytes(content);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * close the RandomAccessFile
	 */
	public void close() {
		if (sFile != null) {
			try {
				sFile.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}

}
