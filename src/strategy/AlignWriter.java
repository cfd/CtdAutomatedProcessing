package strategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.SensorInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;

import processing_old.Parser;

public class AlignWriter implements IPsaWriter {
	
	XMLOutputter xmlOutput;
	
	public AlignWriter() {
		xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	}
	
	ArrayList<SensorInfo> sensors;
	Document doc;	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("3 strategy");
		System.out.println(orderedSensors);
		
	}

	@Override
	public void readTemplate() throws JDOMException, IOException {
		// TODO Auto-generated method stub
		SAXBuilder builder =  new SAXBuilder();
		doc =  builder.build(new File(".\\psa_templates\\AlignTemplate.xml"));
	}

	@Override
	public void writeUpperSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeCalcArray() {
		// TODO Auto-generated method stub
		int count = 0;
		Element rootElement = doc.getRootElement();
		Element calcArray = rootElement.getChild("CalcArray");
		calcArray.setAttribute("Size", sensors.size() + "");
		
		for (SensorInfo info : sensors) {
		
			/*	
			 * int id = Integer.parseInt(sensor.getAttributeValue("SensorID"));
			 *	System.out.println(id);
			 *	SensorInfo info = Parser.sensorsMap.get(id);
			 */

			if (info != null) {

				Element calcArrayItem = new Element("CalcArrayItem");

				Element calc = new Element("Calc");
				Element fullname = new Element("FullName");

				calcArrayItem.setAttribute("index", "" + count);
				calcArrayItem.setAttribute("CalcId", "" + info.getCalcID());

				calc.setAttribute("UnitID", "" + info.getUnitID());
				calc.setAttribute("Ordinal", "" + info.getOrdinal());
				fullname.setAttribute("value", info.getFullname());

				calcArray.addContent(calcArrayItem);
				calcArrayItem.addContent(calc);
				calc.addContent(fullname);
				if (info.getFullname().startsWith("Upoly")) {
					fullname.setAttribute("value", "Upoly 0, " + info.getFullname());
					Element calcName = new Element("CalcName");
					calcName.setAttribute("value", "Upoly 0, NTU");
					calc.addContent(calcName);
				}
				if (info.getFullname().startsWith("Oxygen, SBE 43")) {
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
				if (info.getFullname().startsWith("Descent")){
					Element windowSize = new Element("WindowSize");
					windowSize.setAttribute("value", "2.000000");
					calc.addContent(windowSize);
				}
			count++;
		}
		}
	}

	@Override
	public void writeLowerSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeToNewPsaFile() throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		
		xmlOutput.output(doc, new FileOutputStream(new File(
				".\\output\\AlignIMOS.psa")));
		System.out.println("AlignIMOS.psa File Written!");
	}



}
