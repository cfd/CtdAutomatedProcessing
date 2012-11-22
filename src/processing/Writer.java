package processing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.SensorInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Writer {
	XMLOutputter xmlOutput;
	ArrayList<SensorInfo> sortedList = new ArrayList<>();
	Parser parser = new Parser();

	public Writer() {
		xmlOutput = new XMLOutputter(Format.getPrettyFormat());
	}

	/**
	 * @param args
	 */

	public void writeCalcArray(List<Element> sensors) throws IOException {

		int count = 0;
		Document doc = new Document();
		Element testElement = new Element("standinroot");
		doc.setRootElement(testElement);
		

		SensorInfo time = parser.getSensorsMap().get(-1);
		SensorInfo scanCount = parser.getSensorsMap().get(-2);

		sortedList.add(time);
		sortedList.add(scanCount);

		for (Element sensor : sensors) {
			insertSensor("Pressure", sensor);
		}
		for (Element sensor : sensors) {
			insertSensor("Temperature", sensor);
		}
		for (Element sensor : sensors) {
			insertSensor("Conductivity", sensor);
		}
		for (Element sensor : sensors) {
			insertSensor("Fluorescence", sensor);
		}
		for (Element sensor : sensors) {
			insertSensor("Upoly", sensor);
		}
		
		SensorInfo freq = parser.getSensorsMap().get(-5);
		SensorInfo desRate = parser.getSensorsMap().get(-3);
		SensorInfo density = parser.getSensorsMap().get(-4);

		sortedList.add(freq);
		sortedList.add(desRate);
		sortedList.add(density);

		for (Element sensor : sensors) {
			insertSensor("Oxygen", sensor);
		}

//		for () {
//			System.out.println(info.getFullname() + "............");
//
//			Element calcArray = new Element("CalcArrayItem");
//			Element calc = new Element("Calc");
//			Element fullname = new Element("FullName");
//			testElement.addContent(calcArray);
//			calcArray.addContent(calc);
//			calc.addContent(fullname);
//		}

		testElement.setAttribute("Size", "" + sortedList.size());
		for (SensorInfo info : sortedList) {
//			int id = Integer.parseInt(sensor.getAttributeValue("SensorID"));
//			System.out.println(id);
//
//			SensorInfo info = Parser.sensorsMap.get(id);

			if (info != null) {

				Element calcArrayItem = new Element("CalcArrayItem");

				Element calc = new Element("Calc");
				Element fullname = new Element("FullName");

				calcArrayItem.setAttribute("index", "" + count);
				calcArrayItem.setAttribute("CalcId", "" + info.getCalcID());

				calc.setAttribute("UnitID", "" + info.getUnitID());
				calc.setAttribute("Ordinal", "" + info.getOrdinal());
				fullname.setAttribute("value", info.getFullname());

				testElement.addContent(calcArrayItem);
				calcArrayItem.addContent(calc);
				calc.addContent(fullname);

				if (info.getFullname().startsWith("Upoly")) {
					Element calcName = new Element("CalcName");
					calcName.setAttribute("value", "NTU");
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

			}
			count++;
		}
		xmlOutput.output(doc, new FileOutputStream(new File(
				"output.psa")));
		System.out.println("Wrote to file");

	}

	private void insertSensor(String sensorName, Element sensor) {

		SensorInfo info = parser.getSensorsMap().get(Integer.parseInt(sensor
				.getAttributeValue("SensorID")));
		if (info != null) {
			if (info.getFullname().startsWith(sensorName)) {
				sortedList.add(info);
			}
		}
	}

}
