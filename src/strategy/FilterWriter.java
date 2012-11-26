package strategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.SensorInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class FilterWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	Document doc;
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("2 strategy");
		System.out.println(orderedSensors);
	}

	@Override
	public void readTemplate() 
			throws JDOMException, IOException {
		SAXBuilder builder =  new SAXBuilder();
		doc = builder.build
				(new File("./psa_templates/FilterTemplate.xml"));
	}

	@Override
	public void writeUpperSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeCalcArray() {
		// TODO Auto-generated method stub
		//CalcArray Element from
		
		Element root = doc.getRootElement();
		Element calcArray = root.getChild("CalcArray");
		
		calcArray.setAttribute("Size", "" + sensors.size());
	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNewPsaFile() {
		// TODO Auto-generated method stub
		
	}

}