package net.entelijan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileLoaderResources {

	public String sketchPath(String fileName) {
		System.out.println("sp fileName:" + fileName);
		File file = getCreateFile(fileName);
		return file.getAbsolutePath();
	}

	public InputStream createInput(String fileName) {
		try {
			File file = getCreateFile(fileName);
			return new FileInputStream(file);
		} catch (Exception e) {
			System.err.println("Error creating input stream. " + e.getMessage());
			return null;
		}
	}

	private File getCreateFile(String fileName) {
		File work = new File(System.getProperty("user.dir"));
		File outDir = new File(work, "src/main/resources");
		outDir.mkdirs();
		return new File(outDir, fileName);
	}

}
