package net.entelijan.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileLoaderUserHome {

	public String sketchPath(String fileName) {
		File file = getCreateFile(fileName);
		return file.getAbsolutePath();
	}

	public InputStream createInput(String fileName) {
		try {
			return new FileInputStream(fileName);
		} catch (Exception e) {
			System.err.println("Error creating input stream. " + e.getMessage());
			return null;
		}
	}

	private File getCreateFile(String fileName) {
		File home = new File(System.getProperty("user.home"));
		File outDir = new File(home, "minim_out");
		outDir.mkdirs();
		File file = new File(outDir, fileName);
		return file;
	}

}
