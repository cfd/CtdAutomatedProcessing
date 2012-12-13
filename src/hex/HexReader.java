package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class HexReader {

	private final static LinkedHashMap<String,String> months = new LinkedHashMap<String,String>();
	
	static {
		months.put("jan","01");
		months.put("feb","02");
		months.put("mar","03");
		months.put("apr","04");
		months.put("may","05");
		months.put("jun","06");
		months.put("jul","07");
		months.put("aug","08");
		months.put("sep","09");
		months.put("oct","10");
		months.put("nov","11");
		months.put("dec","12");
	}
	
	private static boolean DEBUG = true;
	private String serialNo;
	private String calibrationDate;

	private String path;

	public HexReader(String path) {
		this.path = path;
	}

	public void run() {

		// Location of file to read
		File file = new File(path);

		try {

			Scanner scanner = new Scanner(file);

			int count = 1;

			while (scanner.hasNextLine() && count++ < 10) {
				String line = scanner.nextLine();
				if (line.startsWith("* Temperature")) {
					serialNo = line.split("=")[1].trim();
					if (DEBUG) {
						System.out.println(line);
						System.out.println(serialNo);
					}
					
				}

				if (line.startsWith("* System UpLoad Time")) {
					calibrationDate = formatDate(line.split("=")[1].trim());					
					if (DEBUG) {
						System.out.println(line);
						System.out.println(calibrationDate);
					}
					
				}

			}
			scanner.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Formats the date to the same way as it is in the datebase
	 * @param badDate
	 * @return
	 */
	private String formatDate(String badDate){
		String[] dateSplit = badDate.split(" ");
		String day = dateSplit[1];
		String month = months.get(dateSplit[0].toLowerCase());
		String year = dateSplit[2];
		
		if (DEBUG){
			System.out.printf("Day: %s, Month: %s, Year: %s%n", day, month, year);
		}
		
//		//Converts the date from 3 character to 2 number
//		switch (month.toLowerCase()){
//		case "jan" :
//			month = "01";
//			break;
//		case "feb" :
//			month = "02";
//			break;
//		case "mar" :
//			month = "03";
//			break;
//		case "apr" :
//			month = "04";
//			break;
//		case "may" :
//			month = "05";
//			break;
//		case "jun" :
//			month = "06";
//			break;
//		case "jul" :
//			month = "07";
//			break;
//		case "aug" :
//			month = "08";
//			break;
//		case "sep" :
//			month = "09";
//			break;
//		case "oct" :
//			month = "10";
//			break;
//		case "nov" :
//			month = "11";
//			break;
//		case "dec" :
//			month = "12";
//			break;
//		}
		
		return String.format("%s/%s/%s", day, month, year);
	}
	
	public String getCalibrationDate(){
		return calibrationDate;
	}
	
	public String getSerialNo(){
		return serialNo;
	}

	public static void main(String args[]) {
		HexReader reader = new HexReader(
				"\\\\pearl\\temp\\adc-jcu2012\\config\\19plus1_4409_20120905\\data\\raw\\GB12071.hex");
		reader.run();
	}
}
