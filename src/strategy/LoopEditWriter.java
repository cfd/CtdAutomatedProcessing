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

public class LoopEditWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	Document doc;
	
	/**
	 * Takes @param orderedSensors and sets sensors to 
	 * @param orderedSensors and prints '6 Strategy', as well 
	 * as the content of the @param orderedSensors to console.
	 */
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("6 strategy");
		System.out.println(orderedSensors);
	}
	
	/**
	 * Takes @param psaTemplateFolderPath and uses it to create
	 * the structure of the .psa file.
	 */
	
	@Override
	public void readTemplate(String psaTemplate) throws JDOMException, IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder =  new SAXBuilder();
		 doc = builder.build(new File(psaTemplate + "\\LoopEditTemplate.xml"));
	}

	/**
	 * Writes the upper section of the .psa file which is above the calcArray. 
	 */
	
	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
		Element root = doc.getRootElement();
		Element inputDir = root.getChild("InputDir");
		inputDir.setAttribute("value", workingDirectory + "batch");
		Element outputDir = root.getChild("OutputDir");
		outputDir.setAttribute("value", workingDirectory + "batch");
	}

	/**
	 * This method is called due to the strategy pattern however is not required for 
	 * this .psa file and is therefore left intentionally blank.
	 */
	
	@Override
	public void writeCalcArray(String userPoly) {
	}

	/**
	 * This method is called due to the strategy pattern however is not required for 
	 * this .psa file and is therefore left intentionally blank.
	 */
	
	@Override
	public void writeLowerSection() {
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
				newDirName + "/LoopEditIMOS.psa")));
		System.out.println("LoopEditIMOS.psa File Written!");
	}
}
