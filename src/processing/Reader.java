package processing;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Reader {
	
	
	public void read(String path) throws IOException, JDOMException{
		
		Writer writer = new Writer();
		

		SAXBuilder builder =  new SAXBuilder();
		Document readDoc;
		
//		String[] filePaths = {
//				"./NRS1_6180_20120917.xmlcon",
//				"./NRS2_6390_01102011_O2andNTU.xmlcon",
//				"./IMOS_ANMN_NRS_CTP_300712_NRS_DAR_01.XMLCON",
//				"./19plus1_4409_20121115_GPS.xmlcon",
//				"./191_0597_20090206.xmlcon"
//		};
		
			
			readDoc =  builder.build(new File(path));
			
			
			
			
			System.out.println("===========================================================");
			System.out.println("\t\t\t  ===");
			System.out.println("===========================================================\n");
			
			
			Element rootEle = readDoc.getRootElement();
			Element sensorArrayEle = rootEle.getChild("Instrument").getChild("SensorArray");
			List<Element> sensors = sensorArrayEle.getChildren();
			System.out.println("IDs to look up in hashmap for information:");
			writer.writeCalcArray(sensors);
			
			
			
			
			
			
			System.out.println("===========================================================");
			System.out.println("\t\t\t  ===");
			System.out.println("===========================================================\n");
			
			
		
			
			
//			//prints name of root
//			String root = readDoc.getRootElement().toString().substring(11, readDoc.getRootElement().toString().length() - 3);
//			
//			System.out.println("Root: " + root);
//			
//			Element sensorArrayRoot = readDoc.getRootElement().getChild("Instrument").getChild("SensorArray");
//			
//			//prints Sensors
//			for (Element sensor: sensorArrayRoot.getChildren("Sensor")){
//	
//				//Prints the sensor No, Type & ID
//				System.out.print("\n#" + (Integer.parseInt(sensor.getAttributeValue("index")) + 1));
//				System.out.print(" " + sensor.getChildren().get(0).getName());
//				System.out.println(" ID" + sensor.getAttributeValue("SensorID"));
//				
//				
//				if (sensor.getChildren().get(0).getName().equals("NotInUse"));
//	
//				//Print Elements
//				for (Element sensorAttribute : sensor.getChildren().get(0).getChildren()){
//					
//					System.out.print("\t"+ sensorAttribute.getName() + ": ");
//					
//					switch (sensorAttribute.getName()){
//						case "Coefficients":
//						case "CalibrationCoefficients":
//							for (Element coefficients : sensorAttribute.getChildren()){
//								System.out.print("\n\t\t"+ coefficients.getName() + ": ");
//								System.out.print(coefficients.getText().trim());
//							}
//							System.out.println();
//							break;
//						default:
//							System.out.println(sensorAttribute.getText().trim());
//							break;
//					}
//				}
//			}
//			System.out.println("\n-----------------------------------------------------------");
//			System.out.println("\t\t\t</ROOT>");
//			System.out.println("-----------------------------------------------------------\n");
//		
//		System.out.println("===========================================================");
//		System.out.println("\t\t\t  FIN");
//		System.out.println("===========================================================\n");
	}
}
