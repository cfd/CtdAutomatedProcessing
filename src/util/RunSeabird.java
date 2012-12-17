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

	public void writeBatch() {
		//Do you like commenting?
		File file = new File(directory + "\\xmlcons\\" + output);
		
		//Checks if the batch file already exists and deletes it if it does
		if (file.exists()) {
			file.delete();
			System.out.println("Old batch file deleted");
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

	public void setBatch(String folder, String con) {

		// Comment
		System.out.println(folder + "\\" + con +  type);
		//Checks if the con file exists and that it has the psa files
		if (new File(folder + "\\" + con + type).isFile()
				&& new File(folder + "\\" + "BinAvgIMOS.psa").isFile()) {
			commands += "start /wait " + folder
					+ "\\run.bat *\n";
		}
		System.out.println("Command: " + commands);
	}
}