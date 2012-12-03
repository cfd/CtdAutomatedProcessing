package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import model.SensorInfo;

public class DeriveWriter implements IPsaWriter{
	private Document doc;
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		System.out.println();
		System.out.println("5 strategy");
		System.out.println(orderedSensors);
	}
	
	@Override
	public void readTemplate() throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(new File("psa_templates/DeriveTemplate.xml"));
	}

	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
	}

	@Override
	public void writeCalcArray(String userPoly) {
	}

	@Override
	public void writeLowerSection() {
	}

	@Override
	public void writeToNewPsaFile(String newDirName) throws FileNotFoundException, IOException {
		
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(
				newDirName + "/DeriveIMOS.psa")));
		System.out.println("Wrote to file");
		
	}
}