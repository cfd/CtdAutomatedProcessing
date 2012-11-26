package strategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import model.SensorInfo;

public class DatCnvWriter implements IPsaWriter {
	final boolean DEBUG = true;
	ArrayList<SensorInfo> sensors;
	Document readDoc;
	ArrayList<Element> upperSection = new ArrayList<>();

	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("1 strategy");
		System.out.println(orderedSensors);
		if (DEBUG) {
			try {
				readTemplate();
				writeUpperSection();
				writeCalcArray();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void readTemplate() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();

		readDoc = builder.build(new File("psa_templates/DataCnvTemplate.xml"));

		if (DEBUG) {
			System.out.println("I Read The FIle");
		}
		// TODO Auto-generated method stub

	}

	@Override
	public void writeUpperSection() {
		if (DEBUG) {
			System.out.println("Writter upper section");
		}
		Element root = readDoc.getRootElement();
		List<Element> allSection = root.getChildren();
		for (Element element : allSection) {
			if (element.getName().equals("CalcArray")) {
				break;
			}
			upperSection.add(element);
			if (DEBUG) {
				System.out.println(element.getName());
				System.out.println(element.getAttributes());
				System.out.println();
			}

		}
		// TODO Auto-generated method stub
	}

	@Override
	public void writeCalcArray() {
		Element calcArray = new Element("CalcArray");
		for(SensorInfo sensor : sensors){
			System.out.println(sensor.getFullname());
		}
		// TODO Auto-generated method stub

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
