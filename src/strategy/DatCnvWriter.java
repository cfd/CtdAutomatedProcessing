package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import model.SensorInfo;

public class DatCnvWriter implements IPsaWriter {
	private final boolean DEBUG = false;
	private ArrayList<SensorInfo> sensors;
	private Document doc;
	private int amountUserPoly = 0;
	private boolean isLatLongPressure = false;
	private boolean isOxygen = true;

	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("1 strategy");
		System.out.println(orderedSensors);
	}

	@Override
	public void readTemplate() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(new File("psa_templates/DataCnvTemplate.xml"));

		if (DEBUG) {
			System.out.println("I Read The File");
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpperSection(String workingDirectory) {
		if (DEBUG) {
			System.out.println("Writter upper section");
		}
	}

	@Override
	public void writeCalcArray(String userPoly) {
		//Checks if user poly is ISUS V3 Nitrate
		switch(userPoly){
		case "ISUS V3 Aux":
			isLatLongPressure = true;
			userPoly = "ISUS V3 Nitrate";
			break;
		case "Turbidity":
			isOxygen = false;
		}
		
		//counter
		int count = 0;
		
		Element root = doc.getRootElement();
		Element calcArray = root.getChild("CalcArray");

		// Adds size to the calc array
		calcArray.setAttribute("Size", "" + (sensors.size() + 2));

		// Creates the Time Elapsed and Scan Count array items
		Element timeElapsed = calcArrayItemWritter(count++, 84, 52, 0,
				"Time, Elapsed [seconds]", userPoly);
		Element scanCount = calcArrayItemWritter(count++, 72, -1, 0, "Scan Count", userPoly);

		// Adds the Time Elapsed and Scan Count items
		calcArray.addContent(timeElapsed);
		calcArray.addContent(scanCount);
		if (isLatLongPressure){
			Element pressure = calcArrayItemWritter(count++,65,3,0,"Pressure, Digiquartz [db]", userPoly);
			calcArray.addContent(pressure);
		}

		for (SensorInfo sensor : sensors) {
			//Checks if there are two user polynomials might need to do some more here
			if (amountUserPoly > 0 && sensor.getFullName().equals("Upoly 0")){
				continue;
			}
			else if(!isOxygen && sensor.getFullName().equals("Oxygen, SBE 43 [ml/l]")){
				continue;
			}
			calcArray.addContent(calcArrayItemWritter(count++,
					sensor.getCalcID(), sensor.getUnitID(),
					sensor.getOrdinal(), sensor.getFullName(), userPoly));
			
		}
		
		if(isLatLongPressure){
			Element latitude = calcArrayItemWritter(count++,39,4,0,"Latitude [deg]",userPoly);
			calcArray.addContent(latitude);
			latitude = calcArrayItemWritter(count++,39,4,0,"Latitude [deg]",userPoly);
			calcArray.addContent(latitude);
		}

		if (DEBUG) {
			System.out.println(sensors.size() + 2);
		}

		for (SensorInfo sensor : sensors) {
			System.out.println(sensor.getFullName());
		}
		
		//Disables user poly and other fields for another run
		isLatLongPressure = false;
		amountUserPoly = 0;
		isOxygen = true;
		// TODO Auto-generated method stub

	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeToNewPsaFile(String newDirName) throws FileNotFoundException, IOException {

		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(
				"output/" + newDirName + "/DatCnvIMOS.psa")));
		System.out.println("Wrote to file");

	}

	private Element calcArrayItemWritter(int index, int calcID, int unitID,
			int ordinal, String fullname, String userPoly) {
		// Sets up calc and calcArray Elements
		Element calcArrayItem = new Element("CalcArrayItem");
		Element calc = new Element("Calc");

		// Does the full name and adds it
		Element name = new Element("FullName");
		name.setAttribute("value", fullname);
		calc.addContent(name);

		// Sets the calc
		calc.setAttribute("UnitID", "" + unitID);
		calc.setAttribute("Ordinal", "" + ordinal);

		// Sets the calcArrayItem
		calcArrayItem.setAttribute("index", "" + index);
		calcArrayItem.setAttribute("CalcID", "" + calcID);
		calcArrayItem.addContent(calc);

		// Sets up the WindowSize
		Element windowSize = new Element("WindowSize");
		windowSize.setAttribute("value", "2.000000");

		// Sets up the ApplyHysteresisCorrection
		Element hysteresis = new Element("ApplyHysteresisCorrection");
		hysteresis.setAttribute("value", "1");

		// Sets up the ApplyTauCorrection
		Element tau = new Element("ApplyTauCorrection");
		tau.setAttribute("value", "1");

		// Checks if any extra detail is needed
		switch (fullname) {
		case "Descent Rate [m/s]":
			calc.addContent(windowSize);
			break;
		case "Oxygen, SBE 43 [ml/l]":
			calc.addContent(windowSize);
			calc.addContent(hysteresis);
			calc.addContent(tau);
			break;
		case "Upoly 0":
			name.setAttribute("value", fullname + ", " + userPoly);
			Element calcName = new Element("CalcName");
			calcName.setAttribute("value", userPoly);
			calc.addContent(calcName);
			amountUserPoly++;
			break;
		}

		return calcArrayItem;
	}

}