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
	final boolean DEBUG = false;
	ArrayList<SensorInfo> sensors;
	Document doc;

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
	public void writeUpperSection() {
		if (DEBUG) {
			System.out.println("Writter upper section");
		}
	}

	@Override
	public void writeCalcArray(String userPoly) {
		Element root = doc.getRootElement();
		Element calcArray = root.getChild("CalcArray");

		// Adds size to the calc array
		calcArray.setAttribute("Size", "" + (sensors.size() + 2));

		// Creates the Time Elapsed and Scan Count array items
		Element timeElapsed = calcArrayItemWritter(0, 84, 52, 0,
				"Time, Elapsed [seconds]", userPoly);
		Element scanCount = calcArrayItemWritter(1, 72, -1, 0, "Scan Count", userPoly);

		// Adds the Time Elapsed and Scan Count items
		calcArray.addContent(timeElapsed);
		calcArray.addContent(scanCount);

		int index = 2;
		for (SensorInfo sensor : sensors) {
			calcArray.addContent(calcArrayItemWritter(index++,
					sensor.getCalcID(), sensor.getUnitID(),
					sensor.getOrdinal(), sensor.getFullName(), userPoly));
		}

		if (DEBUG) {
			System.out.println(sensors.size() + 2);
		}

		for (SensorInfo sensor : sensors) {
			System.out.println(sensor.getFullName());
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeToNewPsaFile() throws FileNotFoundException, IOException {

		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		;
		xmlOutput.output(doc, new FileOutputStream(new File(
				"output/DatCnvIMOS.psa")));
		System.out.println("Wrote to file");

	}

	private Element calcArrayItemWritter(int index, int calcID, int unitID,
			int ordinal, String fullname, String userPoly) {
		//Sets up calc and calcArray Elements
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
		
		//Sets up the WindowSize
		Element windowSize = new Element("WindowSize");
		windowSize.setAttribute("value", "2.000000");
		
		//Sets up the ApplyHysteresisCorrection
		Element hysteresis = new Element("ApplyHysteresisCorrection");
		hysteresis.setAttribute("value", "1");
		
		//Sets up the ApplyTauCorrection
		Element tau = new Element("ApplyTauCorrection");
		tau.setAttribute("value", "1");
		
		//Checks if any extra detail is needed
		switch(fullname){
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
		}

		return calcArrayItem;
	}

}