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

public class BinAvgWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	Document doc;
	
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("4 strategy");
		System.out.println(orderedSensors);
		
	}
	
	@Override
	public void readTemplate() throws JDOMException, IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder =  new SAXBuilder();
		 doc = builder.build(new File("./psa_templates/BinAvgTemplate.xml"));
	}

	@Override
	public void writeUpperSection(String workingDirectory, String instrumentPath) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeCalcArray(String userPoly) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNewPsaFile(String newDirName) throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileOutputStream(new File(
				newDirName + "/BinAvgIMOS.psa")));
		System.out.println("BinAvgIMOS.psa File Written!");
	}

}
