package hex;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HexReader {
	 
	public static void main(String[] args) {
	 
	        // Location of file to read
	        File file = new File("\\\\pearl\\temp\\adc-jcu2012\\config\\19plus1_4409_20120905\\data\\raw\\GB12071.hex");
	 
	        try {
	 
	            Scanner scanner = new Scanner(file);
	            
	            int count = 1;
	
	            while (scanner.hasNextLine() && count++ < 10) {
	                String line = scanner.nextLine();
	                if (line.startsWith("* Temperature")) {
	                	System.out.println(line);
	                	int tempSerial = Integer.parseInt(line.substring(line.length()- 4));
	                	System.out.println(tempSerial);
	                }
	                
	                if (line.startsWith("* System UpLoad Time")) {
	                	System.out.println(line);
	                	String uploadDate = line.substring(line.length()- 20);
	                	System.out.println(uploadDate);
	                }  
	                
	            }
	            scanner.close();     
	        }
	        
	        catch (FileNotFoundException e) {
	        		e.printStackTrace();
	        }
	 }
}
