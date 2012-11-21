package processing;

import java.sql.Connection;
import java.sql.Statement;
import java.util.LinkedHashMap;
import util.ConnectDB;

import model.SensorInfo;

public class Parser {
	
	private LinkedHashMap<Integer, SensorInfo> sensorsMap;
	private static Connection con;
	private Statement statement;
	
	public Parser() {
		sensorsMap = new LinkedHashMap<>();
		
		populateSensorsMap();
	}
	



	private void populateSensorsMap() {
		ConnectDB db = new ConnectDB();
		//call database
		//make objects of SensorInfo
		//put in map
		//sensorID as key, object created as value.
		
	}




	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Parser parser = new Parser();
		
		
		
		Writer wrtr = new Writer();
		Reader rdr = new Reader();
		
			try {
				//wrtr.writeCalcArray();
				rdr.read("C:\\Users\\Chris\\Desktop\\AIMS2012\\ctddata\\ctd\\imos-processed\\config\\SBE19plusV2_6180\\SBE19plusV2_NRS1_6180_20120917\\NRS1_6180_20120917.xmlcon");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
}
