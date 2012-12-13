package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Scanner;

import util.ConnectDB;

public class HexReader {

	private final static LinkedHashMap<String, String> months = new LinkedHashMap<String, String>();
	private static Connection con;
	private static Statement statement;
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"dd/MM/yyyy");

	static {
		// initalize LinkedHashMap
		months.put("jan", "01");
		months.put("feb", "02");
		months.put("mar", "03");
		months.put("apr", "04");
		months.put("may", "05");
		months.put("jun", "06");
		months.put("jul", "07");
		months.put("aug", "08");
		months.put("sep", "09");
		months.put("oct", "10");
		months.put("nov", "11");
		months.put("dec", "12");
	}

	private static boolean DEBUG = true;
	private String serialNo;
	private Date calibrationDate;

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
						System.out.println("Date: "
								+ calibrationDate.toString());
					}

				}

			}
			scanner.close();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			getConInfo();
		} catch (ParseException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void getConInfo() throws ParseException, SQLException {
		// initalize Database
		ConnectDB db = new ConnectDB();
		con = db.getDdConnection();

		statement = con.createStatement();
		ResultSet results = getConFile();

		while (results.next()) {
			String instrumentID = results.getString("Instrument_ID");

			Date startDate = new Date();
			Date endDate = new Date();

			String stringStartDate = results.getString("Start_Date");
			String stringEndDate = results.getString("End_Date");
			
			//Checks for null values
			if (stringEndDate == null || stringStartDate == null){
				continue;
			}
			//Checks if there needs to be current date
			else if(stringEndDate.toLowerCase().equals("current")){
				startDate = dateFormat.parse(stringStartDate);
			}else{
				startDate = dateFormat.parse(stringStartDate);
				endDate = dateFormat.parse(stringEndDate);
			}
			
			if(DEBUG){
				System.out.println(instrumentID + ", " + stringStartDate + ", "
					+ stringEndDate);
				System.out.printf("Start Date: %s%nEnd Date: %s%n", dateFormat.format(startDate), dateFormat.format(endDate));
			}
			
			if((startDate.before(calibrationDate) || startDate.equals(calibrationDate)) && endDate.after(calibrationDate)){
				if(DEBUG){
					System.out.println("InstrumentID is: " + instrumentID);
				}
				break;
			}
			
		}
		con.close();
	}

	/**
	 * Formats the date to the same way as it is in the datebase
	 * 
	 * @param badDate
	 * @return
	 */
	private Date formatDate(String badDate) {
		String[] dateSplit = badDate.split(" ");
		String day = dateSplit[1];
		String month = months.get(dateSplit[0].toLowerCase());
		String year = dateSplit[2];

		if (DEBUG) {
			System.out.printf("Day: %s, Month: %s, Year: %s%n", day, month,
					year);
		}

		try {
			Date date = dateFormat.parse(day + "/" + month + "/" + year);
			return date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		// return String.format("%s/%s/%s", day, month, year);
	}

	private ResultSet getConFile() {
		try {
			String sql = String
					.format("SELECT * FROM Instrument_Calibration as IC, Instrument_Details as ID WHERE IC.Serial_No = ID.Serial_no and IC.Serial_No = '%s'",
							serialNo);
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date getCalibrationDate() {
		return calibrationDate;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public static void main(String args[]) {
		HexReader reader = new HexReader(
				"\\\\pearl\\temp\\adc-jcu2012\\config\\19plus1_4409_20120905\\data\\raw\\GB12071.hex");
		reader.run();
	}
}
