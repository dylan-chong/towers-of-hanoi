package swen221.assignment1;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Access resources from `src/main/resources/` within a jar
 */
public class Resources {
	/**
	 * Do not write to this file! This creates a temporary file. So it can be
	 * accessed when it is compressed inside a jar
	 *
	 * @return Temporary file (copied from the original)
	 */
	public File getResourceFile(String srcFileName) throws FileNotFoundException {
		try {
			File tempFile = copyToTempFile(srcFileName);
			tempFile.deleteOnExit();
			return tempFile;
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				throw (FileNotFoundException) e;
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Reads the file, even if the file is inside a Jar
	 *
	 * @param srcFileName The name of the file to read (e.g. 'test.txt')
	 * @return A {@link List} of the lines in the file
	 * @throws FileNotFoundException Probably thrown when the file is not found
	 */
	public List<String> readLinesOfTextFile(String srcFileName)
			throws FileNotFoundException {
		File file = getResourceFile(srcFileName);
		Path path = Paths.get(file.toURI());
		try {
			return Files.readAllLines(path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Required for reading resource files when in a Jar. It also gets rid of
	 * the requirement of knowing exactly what directory the file is in.
	 */
	private File copyToTempFile(String srcFileName) throws IOException {

		String tempFileName = "temp-" + srcFileName + "-" + System.currentTimeMillis();
		File tempFile = File.createTempFile(tempFileName, null);

		try (InputStream inputStream = getClass()
				.getResourceAsStream("/" + srcFileName);
			 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

			if (inputStream == null) throw new FileNotFoundException();

			int currentByte;
			while ((currentByte = inputStream.read()) != -1) {
				outputStream.write(currentByte);
			}
		}

		return tempFile;
	}
}
