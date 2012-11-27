package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;

import model.SensorInfo;

public class LoopEditWriter implements IPsaWriter{

	XMLOutputter xmlOutput;
	
		public LoopEditWriter() {
		xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	}
	
	ArrayList<SensorInfo> sensors;
	Document doc;
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("6 strategy");
		System.out.println(orderedSensors);
	}
	
	@Override
	public void readTemplate() throws JDOMException, IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder =  new SAXBuilder();
		 doc = builder.build(new File("./psa_templates/LoopEditTemplate.xml"));
	}

	@Override
	public void writeUpperSection() {
		// TODO Auto-generated method stub
		////UPPER SECTION NOT COMPLETED YET!!!/////
	}

	@Override
	public void writeCalcArray(String userPoly) {
		// TODO Auto-generated method stub
		//CALC ARRAY NOT REQUIRED
	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub
		//LOWER SECTION NOT REQUIRED
	}

	@Override
	public void writeToNewPsaFile() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		xmlOutput.output(doc, new FileOutputStream(new File(
				"output/LoopEditIMOS.psa")));
		System.out.println("LoopEditIMOS.psa File Written!");
	}
}
