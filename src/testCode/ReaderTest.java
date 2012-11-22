package testCode;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class ReaderTest {

	@Test
	public void findRoot() {

		SAXBuilder builder =  new SAXBuilder();
		Document readDoc;
		
		try {
			readDoc =  builder.build(new File("./NRS1_6180_20120917.xmlcon"));
		} catch (JDOMException | IOException e) {
			System.err.println("Didn't find the file");
			//e.printStackTrace();
			Assert.fail();
			return;
		}
		
		//prints name of root
		String root = readDoc.getRootElement().toString().substring(11, 
				readDoc.getRootElement().toString().length() - 3);
		
		Assert.assertEquals("SBE_InstrumentConfiguration", root);
	}
	
	@Test
	public void findSensor(){

		SAXBuilder builder =  new SAXBuilder();
		Document readDoc;
		
		try {
			readDoc =  builder.build(new File("./NRS1_6180_20120917.xmlcon"));
		} catch (JDOMException | IOException e) {
			System.err.println("Didn't find the file");
			//e.printStackTrace();
			Assert.fail();
			return;
		}
		
		Element sensorArrayRoot = readDoc.getRootElement().getChild("Instrument").
				getChild("SensorArray");
		
		String[] names = {
				"TemperatureSensor",
				"ConductivitySensor",
				"PressureSensor",
				"UserPolynomialSensor",
				"FluoroWetlabECO_AFL_FL_Sensor",
				"OxygenSensor",
				"NotInUse"
			};
		
		for (int i = 0; i < 7; i++){
			Assert.assertEquals(names[i], sensorArrayRoot.getChildren().get(i).
					getChildren().get(0).getName());
		}
	}
}
