package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RunSeabird {
	private String directory;
	private ArrayList<String> folders = new ArrayList<>();
	public RunSeabird(String directory) {
		this.directory = directory;
	}

	/**
	 * @param args
	 */
	public void run() {
		setFolders();
		String commands = getBatch();
		writeBatch(commands);
	}

	private void writeBatch(String commands) {
			File file = new File(directory + "\\xmlcons\\processSeabirds.bat");
			// Creates a new Print Writer
			PrintWriter fout = null;
			try {
				fout = new PrintWriter(file.getAbsolutePath());
				fout.print(commands + "\nEXIT [/B] [exitCode] ");			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally{
				fout.close();
			}
	}

	private String getBatch() {
		String commands = "";
		for (String folder : folders) {
			commands += "start /wait " + directory + "\\config\\" + folder
					+ "\\seabird.bat *\n";
		}
		System.out.println(commands);		
		return commands;
	}

	private void setFolders() {
		// ArrayList<String> folders = new ArrayList<>();
		// DO foldery things
		File dir = new File(directory + "\\config\\");
		for (File file : dir.listFiles()) {
			folders.add(file.getName());
		}
	}
}