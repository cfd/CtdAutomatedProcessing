package processing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import util.ConnectDB;

import model.SensorInfo;

public class Parser {
	
	public static LinkedHashMap<Integer, SensorInfo> sensorsMap;
	private static Connection con;
	private Statement statement;
	
	public Parser() {
		sensorsMap = new LinkedHashMap<>();
		
		populateSensorsMap();
	}
	



	private void populateSensorsMap() {
		ConnectDB db = new ConnectDB();
		con = db.getDdConnection();
		try {
			statement = con.createStatement();
			
			ResultSet results = getAllAttributes();
			while(results.next()){
				int sensorID = results.getInt("sensor_ID");
				int calcID = results.getInt("calc_ID");
				int unitID = results.getInt("unit_ID");
				int ordinal = results.getInt("ordinal");
				String name = results.getString("full_name");
				sensorsMap.put(sensorID, new SensorInfo(unitID, sensorID, calcID, ordinal, name));
			}
			
			System.out.println("Sensor Map");
			System.out.println(sensorsMap.toString());
			System.out.println(sensorsMap.get(3).toString());
			con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * Returns a ResultSet of all records from the sensor_info table
	 * @return ResultSet
	 */
	public ResultSet getAllAttributes(){
		try{
			String sql = "SELECT * FROM sensor_info";
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Parser parser = new Parser();
		
		

		Reader rdr = new Reader();
		
			try {
				
				rdr.read("NRS1_6180_20120917.xmlcon");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
