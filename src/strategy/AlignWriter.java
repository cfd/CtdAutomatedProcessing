package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;

import model.SensorInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;

public class AlignWriter implements IPsaWriter {

	XMLOutputter xmlOutput;
	
	public AlignWriter() {
		xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	}

	ArrayList<SensorInfo> sensors;
	Document doc;
	
	/**
	 * takes @param orderedSensors and sets sensors to 
	 * @param orderedSensors and prints '3 Strategy', & as well 
	 * as the content of the @param orderedSensors to console.
	 */
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("3 strategy");
		System.out.println(orderedSensors);
	}

	/**
	 * takes @param psaTemplateFolderPath and uses it to create
	 * the structure of the psa file
	 */
	@Override
	public void readTemplate(String psaTemplateFolderPath) throws JDOMException,
			IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(new File(psaTemplateFolderPath + "\\AlignTemplate.xml"));
	}

	/**
	 * writes the upper section of the psa file which is above the calcArray. 
	 * 
	 * This includes:
	 * 	- 	@param workingDirectory in inputDir's value attribute, followed 
	 * 		by "batch". similar thing is done for outputDir's value attribute.
	 * 
	 * the parameter @param instrumentPath is not used in this writer class.
	 * 
	 * at the end of this method it removes pressure from the sensors as it's 
	 * no longer needed.
	 */
	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
		// TODO Auto-generated method stub
		// //UPPER SECTION NOT COMPLETED YET!!!/////
		Element root = doc.getRootElement();
		Element inputDir = root.getChild("InputDir");
		inputDir.setAttribute("value", workingDirectory + "batch");
		Element outputDir = root.getChild("OutputDir");
		outputDir.setAttribute("value", workingDirectory + "batch");
		
		//To remove Pressure from align
		sensors.remove(0);
	}

	/**
	 * this method populates the calcarray of the psa file, it takes @param userpoly
	 * incase the calcarray includes a Upoly sensor.
	 */
	@Override
	public void writeCalcArray(String userPoly) {
		// TODO Auto-generated method stub
		int count = 0;
		Element rootElement = doc.getRootElement();
		Element calcArray = rootElement.getChild("CalcArray");
		calcArray.setAttribute("Size", sensors.size() + "");

		for (SensorInfo info : sensors) {

			if (info != null) {

				Element calcArrayItem = new Element("CalcArrayItem");

				Element calc = new Element("Calc");
				Element fullname = new Element("FullName");

				calcArrayItem.setAttribute("index", "" + count);
				calcArrayItem.setAttribute("CalcID", "" + info.getCalcID());

				calc.setAttribute("UnitID", "" + info.getUnitID());
				calc.setAttribute("Ordinal", "" + info.getOrdinal());
				fullname.setAttribute("value", info.getFullName());

				calcArray.addContent(calcArrayItem);
				calcArrayItem.addContent(calc);
				calc.addContent(fullname);

				if (info.getFullName().startsWith("Upoly")) {
					fullname.setAttribute("value",
							"Upoly 0, " + info.getFullName() + ", " + userPoly);
					Element calcName = new Element("CalcName");
					calcName.setAttribute("value", "Upoly 0, " + userPoly);
					calc.addContent(calcName);
				}
				if (info.getFullName().startsWith("Oxygen, SBE 43")) {
					Element windowSize = new Element("WindowSize");
					windowSize.setAttribute("value", "2.000000");
					Element applyH = new Element("ApplyHysteresisCorrection");
					applyH.setAttribute("value", "1");
					Element tau = new Element("ApplyTauCorrection");
					tau.setAttribute("value", "1");
					calc.addContent(windowSize);
					calc.addContent(applyH);
					calc.addContent(tau);
				}
				if (info.getFullName().startsWith("Descent")) {
					Element windowSize = new Element("WindowSize");
					windowSize.setAttribute("value", "2.000000");
					calc.addContent(windowSize);
				}
				count++;
			}
		}
	}

	/**
	 * this fills out the lower section of the psa file which is below the calcArray.
	 * 
	 * In the filter writer it has a section called the FilterTypeArray, this just 
	 * includes an index for each sensor in the array and a value for value. 
	 * 
	 * NEED MORE DOCUMENTING
	 */
	@Override
	public void writeLowerSection() {
		// TODO MORE DOCUMENTATION
		int count1 = 0;
		Element rootElement = doc.getRootElement();
		Element valArray = rootElement.getChild("ValArray");
		valArray.setAttribute("size", sensors.size() + "");

		for (SensorInfo info : sensors) {

			if (info != null) {

				Element valArrayItem = new Element("ValArrayItem");

				valArrayItem.setAttribute("index", "" + count1);

				if (info.getFullName().startsWith("Temperature")) {
					valArrayItem.setAttribute("value", "0.500000");
				} else {
					valArrayItem.setAttribute("value", "0.000000");
				}

				valArrayItem.setAttribute("variable_name", info.getFullName());

				int i = info.getFullName().indexOf('[');
				if (i != -1) {
					valArrayItem.setAttribute("variable_name",
							info.shortenName(i));
				}

				if (info.getFullName().startsWith("Upoly")) {
					valArrayItem.setAttribute("variable_name",
							"User Polynomial");
				}

				if (info.getFullName().startsWith("Frequency")) {
					valArrayItem.setAttribute("variable_name", "Frequency");
				}

				valArray.addContent(valArrayItem);
			}
			count1++;
		}
	}

	/**
	 * outputs a psa file to the directory of @param newDirName, while making it's
	 * format 'pretty'.
	 */
	@Override
	public void writeToNewPsaFile(String newDirName)
			throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		xmlOutput.output(doc, new FileOutputStream(new File(newDirName
				+ "/AlignIMOS.psa")));
		System.out.println("AlignIMOS.psa File Written!");
	}
}
