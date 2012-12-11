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
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class FilterWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	Document doc;
	
	/**
	 * takes @param orderedSensors and sets sensors to 
	 * @param orderedSensors and prints '2 Strategy', & as well 
	 * as the content of the @param orderedSensors to console.
	 */
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("2 strategy");
		System.out.println(orderedSensors);
	}

	/**
	 * takes @param psaTemplateFolderPath and uses it to create
	 * the structure of the psa file
	 */
	@Override
	public void readTemplate(String psaTemplateFolderPath) 
			throws JDOMException, IOException {
		SAXBuilder builder =  new SAXBuilder();
		doc = builder.build
				(new File(psaTemplateFolderPath + "\\FilterTemplate.xml"));
	}

	/**
	 * writes the upper section of the psa file which is above the calcArray. 
	 * 
	 * This includes:
	 * 	-	writing the the size of the inputFileArray as zero. This does not 
	 * 		matter.
	 * 	- 	@param workingDirectory in inputDir's value attribute, followed 
	 * 		by "batch". similar thing is done for outputDir's value attribute.
	 * 
	 * the parameter @param instrumentPath is not used in this writer class.
	 */
	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
		// TODO Auto-generated method stub
		
		Element root = doc.getRootElement();
		Element inputFileArray = root.getChild("InputFileArray");
		inputFileArray.setAttribute("size", "" + 0);
		
		//Sets the input and output dir
		Element inputDir = root.getChild("InputDir");
		inputDir.setAttribute("value", workingDirectory + "batch");
		Element outputDir = root.getChild("OutputDir");
		outputDir.setAttribute("value", workingDirectory + "batch");		
	}

	/**
	 * this method populates the calcarray of the psa file, it takes @param userpoly
	 * incase the calcarray includes a Upoly sensor.
	 */
	@Override
	public void writeCalcArray(String userPoly) {
		//CalcArray Element from
		
		Element root = doc.getRootElement();
		Element calcArray = root.getChild("CalcArray");
		
		//sets the size of the calcArray
		calcArray.setAttribute("Size", "" + sensors.size());
		
		//counter
		int counter = 0;
		
		//places sensors in calcArray
		for (SensorInfo sensor : sensors){
			
			//set up CalcArrayItem
			Element calcArrayItem = new Element("CalcArrayItem");
			calcArrayItem.setAttribute("index", "" + counter++);
			calcArrayItem.setAttribute("CalcID", 
					"" + sensor.getCalcID());
			
			//adds calcArrayItem to calcArray
			calcArray.addContent(calcArrayItem);
			
			//set up Calc
			Element calc = new Element("Calc");
			calc.setAttribute("UnitID", 
					"" + sensor.getUnitID());
			calc.setAttribute("Ordinal", 
					"" + sensor.getOrdinal());
			
			//add calc to calcaArrayItem
			calcArrayItem.addContent(calc);
			
			//set up FullName
			Element fullName = new Element("FullName");
			if (sensor.getFullName().startsWith("Upoly")) {
				fullName.setAttribute("value", "Upoly 0, " + sensor.getFullName() + ", "  + userPoly);
			}
			else {
				fullName.setAttribute("value", "" + sensor.getFullName() );
			}

			//add fullname to calc
			calc.addContent(fullName);
			
			//set up elements unqiue to 'Upoly 0, Upoly 0, ISUS V3 Nitrate'
			if (sensor.getFullName().startsWith("Upoly")) {
				
				Element calcName = new Element("CalcName");
				calcName.setAttribute("value", "Upoly 0, " + userPoly);
				calc.addContent(calcName);
				
			}
			//set up elements unqiue to 'Oxygen, SBE 43'
			else if (sensor.getFullName().startsWith("Oxygen")) {
				
				//set up windowsize
				Element windowSize = new Element("WindowSize");
				windowSize.setAttribute("value", "2.000000");

				//set up applyHysteresisCorrection
				Element applyHysteresisCorrection = 
						new Element("ApplyHysteresisCorrection");
				applyHysteresisCorrection.setAttribute("value", "1");

				//set up applyTauCorrection
				Element applyTauCorrection = 
						new Element("ApplyTauCorrection");
				applyTauCorrection.setAttribute("value", "1");

				calc.addContent(windowSize);
				calc.addContent(applyHysteresisCorrection);
				calc.addContent(applyTauCorrection);
			}
			//set up elements unqiue to Descent Rate [m/s]
			else if (sensor.getFullName().startsWith("Descent")){
				Element windowSize = new Element("WindowSize");
				windowSize.setAttribute("value", "2.000000");
				calc.addContent(windowSize);
			}
		}
	}

	/**
	 * this fills out the lower section of the psa file which is below the calcArray.
	 * 
	 * In the filter writer it has a section called the FilterTypeArray, this just 
	 * includes an index for each sensor in the array and a value for value. 
	 * 
	 * All values should be 1, except the first one which is 2 (for reasons unknown).
	 */
	@Override
	public void writeLowerSection() {
		
		//FilterTypeArray Element from
		Element root = doc.getRootElement();
		Element filterTypeArray = root.getChild("FilterTypeArray");
		
		boolean first = true;
		int counter = 0;
		
		/*
		 * loops for each sensor but doesn't use any of them in the
		 * actual loop, hence the SuppressWarning.
		 */
		for (@SuppressWarnings("unused") SensorInfo sensor: sensors){
			
			Element arrayItem = new Element("ArrayItem");
			
			arrayItem.setAttribute("index", "" + counter++);
			
			
			int value;
			if (first){
				value = 2;
				first = false;
			} else {
				value = 1;
			}
			
			arrayItem.setAttribute("value", "" + value);
			filterTypeArray.addContent(arrayItem);
		}
	}

	/**
	 * outputs a psa file to the directory of @param newDirName, while making it's
	 * format 'pretty'.
	 */
	@Override
	public void writeToNewPsaFile(String newDirName) throws FileNotFoundException, IOException {
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(
				newDirName + "/FilterIMOS.psa")));
		System.out.println("Wrote to file");
	}

}
