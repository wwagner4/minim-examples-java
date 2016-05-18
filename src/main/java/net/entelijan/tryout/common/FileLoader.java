package net.entelijan.tryout.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader {

	public String sketchPath(String fileName) {
		File file = getCreateFile(fileName);
		return file.getAbsolutePath();
	}

	public InputStream createInput(String fileName) {
		try {
			File file = getCreateFile(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			return new FileInputStream(file);
		} catch (IOException e) {
			System.err.println("Error creating input stream. " + e.getMessage());
			return null;
		}
	}

	private File getCreateFile(String fileName) {
		File home = new File(System.getProperty("user.home"));
		File file = new File(home, fileName);
		return file;
	}

}
