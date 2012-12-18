package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectDB {
	Connection dbConnect;
	
	public Connection getDdConnection(){
		return dbConnect;
	}
	
	public ConnectDB(){
		try{
			Class.forName("org.sqlite.JDBC");
			dbConnect = DriverManager.getConnection("jdbc:sqlite://pearl/temp/adc-jcu2012/utilities/db/sensor_info.sqlite3");
			System.out.println("connected to database");
		}  catch (Exception e) {
			System.out.println("failed to connect to database");
            e.printStackTrace();
        }
	}
}
