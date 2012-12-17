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

public class BinAvgWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	Document doc;
	

	/**
	 * Takes @param orderedSensors and sets sensors to 
	 * @param orderedSensors and prints '4 Strategy', as well 
	 * as the content of the @param orderedSensors to console.
	 */
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("4 strategy");
		System.out.println(orderedSensors);
		
	}

	/**
	 * Takes @param psaTemplateFolderPath and uses it to create
	 * the structure of the .psa file
	 */
	
	@Override
	public void readTemplate(String psaTemplateFolderPath) throws JDOMException, IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder =  new SAXBuilder();
		 doc = builder.build(new File(psaTemplateFolderPath + "\\BinAvgTemplate.xml"));
	}

	/**
	 * Writes the upper section of the .psa file which is above the calcArray. 
	 * 
	 * This includes:
	 * 	- 	@param workingDirectory in inputDir's value attribute, followed 
	 * 		by "batch". A similar thing is done for outputDir's value attribute,
	 * 		but workingDir is followed by final.
	 * 
	 * The parameter @param instrumentPath is not used in this writer class.
	 * 
	 * At the end of this method, pressure is removed from the sensors as it is 
	 * no longer required.
	 */
	
	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
		// TODO Auto-generated method stub
		Element root = doc.getRootElement();
		Element inputDir = root.getChild("InputDir");
		inputDir.setAttribute("value", workingDirectory + "batch");
		Element outputDir = root.getChild("OutputDir");
		outputDir.setAttribute("value", workingDirectory + "final");
		
	}

	/**
	 * Doesn't need to do anything.
	 */
	
	@Override
	public void writeCalcArray(String userPoly) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Doesn't need to do anything.
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
	public void writeToNewPsaFile(String newDirName) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(
				newDirName + "/BinAvgIMOS.psa")));
		System.out.println("BinAvgIMOS.psa File Written!");
	}

}
