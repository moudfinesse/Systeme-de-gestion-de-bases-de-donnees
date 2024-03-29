package fichier;

import java.io.EOFException;
import java.io.File;

import java.io.IOException;
import java.io.RandomAccessFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sgbd.PageId;
import sgbd.Constants;

public class DiskManager {
	private static DiskManager INSTANCE = null;

	private DiskManager() {
	}

	public static DiskManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DiskManager();
		}
		return INSTANCE;
	}

	public void createFile(int fileIdx) throws IOException {
		String directoryName = "./DB";
		String fileName = "Data_" + fileIdx + ".rf";
		File file = new File(directoryName + "/" + fileName);
		File directory = new File(directoryName);
		if (!file.exists()) {
			if (!directory.exists()) {
				Files.createDirectory(Paths.get(directoryName));
			}

			Path path = Paths.get(directoryName + "/" + fileName);

			Files.createFile(path);
		}
	}

	public PageId addPage(int fileIdx) throws IOException {

		String directoryName = "./DB";
		String fileName = "Data_" + fileIdx + ".rf";
		RandomAccessFile randomAccessFile = new RandomAccessFile(Paths.get(directoryName + "/" + fileName).toString(),
				"rw");
		long taille = (int) randomAccessFile.length();
		int id = (int) (taille / Constants.PAGE_SIZE);
		// System.out.println("id : "+ id);
		randomAccessFile.seek(taille);
		for (int i = 0; i < Constants.PAGE_SIZE; i++) {
			randomAccessFile.writeByte(0);
		}
		randomAccessFile.close();
		return new PageId(fileIdx, id);
	}

	public void writePage(PageId pageId, byte[] buff) throws IOException {
		String fileName = "./DB/Data_" + pageId.getFileIdx() + ".rf";
		RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
		randomAccessFile.seek(pageId.getPageIdx() * Constants.PAGE_SIZE);
		randomAccessFile.write(buff);
		randomAccessFile.close();
	}

	public void readPage(PageId pageId, byte[] buff) throws IOException {
		// System.err.println("Debut ReadPage : " + getClass());
		String fileName = "./DB/Data_" + pageId.getFileIdx() + ".rf";
		RandomAccessFile Tas = new RandomAccessFile(fileName, "rw");
		Tas.seek(pageId.getPageIdx() * Constants.PAGE_SIZE);
		try {
			Tas.readFully(buff);
		} catch (EOFException e) {
			System.out.println(e);
			Tas.close();
		}
		Tas.close();
	}
}