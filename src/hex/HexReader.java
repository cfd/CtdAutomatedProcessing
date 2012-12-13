package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Scanner;

import util.ConnectDB;

public class HexReader {

	private final static LinkedHashMap<String,String> months = new LinkedHashMap<String,String>();
	private static Connection con;
	private static Statement statement;
	
	static {
		//initalize LinkedHashMap
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
		//TODO move somwhere else
		//initalize Database
		ConnectDB db = new ConnectDB();
		con = db.getDdConnection();
		try{
			statement = con.createStatement();
			ResultSet results = getConFile();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			while (results.next()){
				String instrumentID = results.getString("Instrument_ID");
				String startDate = results.getString("Start_Date");
				Date start = new Date();
				String endDate = results.getString("End_Date");
				if(endDate != null && endDate.toLowerCase().equals("current")){
					Date date = new Date();
					System.out.println("I do things: " + dateFormat.format(date));
				}
				System.out.println(instrumentID + ", " + startDate + ", " + endDate);
			}
			con.close();
		} catch (SQLException e){
			System.out.println("I crashed here");
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
	
	private ResultSet getConFile(){
		try{
			String sql = String.format("SELECT * FROM Instrument_Calibration as IC, Instrument_Details as ID WHERE IC.Serial_No = ID.Serial_no and IC.Serial_No = '%s'", serialNo);
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
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
