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

	/**
	 * Takes @param orderedSensors and sets sensors to 
	 * @param orderedSensors and prints '1 Strategy', as well 
	 * as the content of the @param orderedSensors to console.
	 */
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
	}
	
	/**
	 * Takes @param psaTemplateFolderPath and uses it to create
	 * the structure of the .psa file.
	 */
	
	@Override
	public void readTemplate(String psaTemplateFolderPath) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(new File(psaTemplateFolderPath + "\\DataCnvTemplate.xml"));

	}

	/**
	 * Writes the upper section of the .psa file which is above the calcArray. 
	 * 
	 * This includes:
	 * 	- 	@param workingDirectory in inputDir's value attribute, followed 
	 * 		by "raw". similar thing is done for outputDir's value attribute,
	 * 		but it is followed by "batch".
	 */
	
	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {

		Element root = doc.getRootElement();
		Element inPath = root.getChild("InstrumentPath");
		inPath.setAttribute("value", instrumentPath);
		Element inputDir = root.getChild("InputDir");
		inputDir.setAttribute("value", workingDirectory + "raw");
		Element outputDir = root.getChild("OutputDir");
		outputDir.setAttribute("value", workingDirectory + "batch");
	}

	@Override
	public void writeCalcArray(String userPoly) {
		if (userPoly != null) {
			// Checks if user poly is ISUS V3 Nitrate
			switch (userPoly) {
			case "ISUS V3 Aux":
				isLatLongPressure = true;
				//userPoly = "ISUS V3 Nitrate";
				break;
			case "Turbidity":
				isOxygen = false;
			}
		}
		// counter
		int count = 0;

		Element root = doc.getRootElement();
		Element calcArray = root.getChild("CalcArray");

		// Adds size to the calcArray
		calcArray.setAttribute("Size", "" + (sensors.size() + 2));

		// Creates the Time Elapsed and Scan Count array items
		Element timeElapsed = calcArrayItemWritter(count++, 84, 52, 0,
				"Time, Elapsed [seconds]", userPoly);
		Element scanCount = calcArrayItemWritter(count++, 72, -1, 0,
				"Scan Count", userPoly);

		// Adds the Time Elapsed and Scan Count items
		calcArray.addContent(timeElapsed);
		calcArray.addContent(scanCount);
		if (isLatLongPressure) {
			Element pressure = calcArrayItemWritter(count++, 65, 3, 0,
					"Pressure, Digiquartz [db]", userPoly);
			calcArray.addContent(pressure);
		}

		for (SensorInfo sensor : sensors) {
			// Checks if there are two user polynomials might need to do some
			// more here
			if (amountUserPoly > 0 && sensor.getFullName().equals("Upoly 0")) {
				continue;
			} else if (!isOxygen
					&& sensor.getFullName().equals("Oxygen, SBE 43 [ml/l]")) {
				continue;
			}
			calcArray.addContent(calcArrayItemWritter(count++,
					sensor.getCalcID(), sensor.getUnitID(),
					sensor.getOrdinal(), sensor.getFullName(), userPoly));

		}

		if (isLatLongPressure) {
			Element latitude = calcArrayItemWritter(count++, 39, 4, 0,
					"Latitude [deg]", userPoly);
			calcArray.addContent(latitude);
			latitude = calcArrayItemWritter(count++, 39, 4, 0,
					"Latitude [deg]", userPoly);
			calcArray.addContent(latitude);
		}



		// Disables user poly and other fields for another run
		isLatLongPressure = false;
		amountUserPoly = 0;
		isOxygen = true;
		// TODO Auto-generated method stub

	}

	/**
	 * This method is called due to the strategy pattern however is not required for 
	 * this .psa file and is therefore left intentionally blank.
	 */
	
	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Outputs a .psa file to the directory of @param newDirName, while making it's
	 * format 'pretty'.
	 */
	
	@Override
	public void writeToNewPsaFile(String newDirName)
			throws FileNotFoundException, IOException {

		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(newDirName
				+ "/DatCnvIMOS.psa")));
		System.out.println("DataCnvIMOS.psa written");

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