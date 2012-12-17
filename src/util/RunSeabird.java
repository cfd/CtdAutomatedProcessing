package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class RunSeabird {
	private String directory;
	private String type;
	private String output;
	private String commands = "";

	public RunSeabird(String directory, String type, String output) {
		this.directory = directory;
		this.type = type;
		this.output = output;
	}

	/**
	 * @param args
	 */
	public void run() {
		// setFolders();
		// String commands = getBatch();
	}

	public void writeBatch() {
		File file = new File(directory + "\\xmlcons\\" + output);
		if (file.exists()) {
			file.delete();
		}
		// Creates a new Print Writer
		PrintWriter fout = null;
		try {

			fout = new PrintWriter(file.getAbsolutePath());
			fout.print(commands + "\nEXIT [/B] [exitCode] ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			fout.close();
		}
	}

	public void setBatch(String folder) {
		String file = directory + "\\config\\" + folder;
		// Comment
		if (new File(file + "\\" + folder + type).isFile()
				&& new File(file + "\\" + "BinAvgIMOS.psa").isFile()) {
			commands += "start /wait " + directory + "\\config\\" + folder
					+ "\\run.bat *\n";
		}
		System.out.println(commands);
	}

	// private void setFolders() {
	// // ArrayList<String> folders = new ArrayList<>();
	// // DO foldery things
	// File dir = new File(directory + "\\config\\");
	// for (File file : dir.listFiles()) {
	// folders.add(file.getName());
	// }
	// }
}