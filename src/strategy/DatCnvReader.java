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

import util.RunSeabird;

public class DatCnvReader {
	private static final String DIRECTORY = "\\\\pearl\\temp\\adc-jcu2012";
	private static ArrayList<SensorInfo> sensors = new ArrayList<>();
	private static boolean DEBUG = true;

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		ArrayList<XmlconReader> writers = new ArrayList<>();

		XmlconReader alignWriter = new XmlconReader(new AlignWriter());
		XmlconReader filterWriter = new XmlconReader(new FilterWriter());
		XmlconReader binAvgWriter = new XmlconReader(new BinAvgWriter());
		XmlconReader deriveWriter = new XmlconReader(new DeriveWriter());
		XmlconReader loopEditWriter = new XmlconReader(new LoopEditWriter());

		File dir = new File(DIRECTORY + "/config");
		
		// Comment in when you want sea bird to run
		RunSeabird runSeabird = new RunSeabird(DIRECTORY, ".con", "conProcessSeabirds.bat");

		// Loops through all the folders in config
		for (File xml : dir.listFiles()) {
			String xmlName = xml.getName().replaceFirst("[.][^.]+$", "");

			File datCnv = new File(xml + "\\DatCnvIMOS.psa");

			// Checks if datCnvImos is in the directory if not skips it
			if (datCnv.exists() && !(new File(xml + "\\BinAvgIMOS.psa").isFile())) {

				// Gets all the files and looks for .con
				for (File con : xml.listFiles()) {

					// If there is a .con and DatCnvIMOS.psa do the rest
					if (con.getName().endsWith(".con")) {
						try {
							List<Element> calcArrayItems = readDataCnv(datCnv);

							writers.add(alignWriter);
							writers.add(filterWriter);
							writers.add(binAvgWriter);
							writers.add(deriveWriter);
							writers.add(loopEditWriter);

							populatSensorArray(calcArrayItems);

							System.out.println("I am doing things");
							String outputDirName = xml.toString();

							// Where the batch, final and raw files are located
							String workingDirectory = outputDirName
									+ "\\data\\";

							// Where the xml con is
							String xmlLocation = outputDirName + "\\"
									+ xml.getName();

							for (XmlconReader writer : writers) {
								try {
									writer.getWriterType().setup(sensors);
									writer.getWriterType()
											.readTemplate(
													DIRECTORY
															+ "\\utilities\\psa_templates");
									writer.getWriterType().writeUpperSection(
											workingDirectory, xmlLocation);
									writer.getWriterType().writeCalcArray("");
									writer.getWriterType().writeLowerSection();
									writer.getWriterType().writeToNewPsaFile(
											outputDirName);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							sensors.clear();
							
							runSeabird.setBatch(outputDirName, xmlName);				
							

						} catch (IOException | JDOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		//I like commenting
		runSeabird.writeBatch();
	}

	/**
	 * Populates the sensors array list based on the calcArray items.
	 * 
	 * @param calcArrayItems
	 */
	
	private static void populatSensorArray(List<Element> calcArrayItems) {
		// Gets all the items for the sensors array
		for (Element calcArray : calcArrayItems) {
			// Index is not used anywhere other then debugging
			int index = Integer.parseInt(calcArray.getAttributeValue("index"));
			int calcID = Integer
					.parseInt(calcArray.getAttributeValue("CalcID"));
			Element calc = calcArray.getChild("Calc");
			int unitID = Integer.parseInt(calc.getAttributeValue("UnitID"));
			int ordinal = Integer.parseInt(calc.getAttributeValue("Ordinal"));
			Element name = calc.getChild("FullName");
			String fullname = name.getAttributeValue("value");
			if (DEBUG) {
				System.out
						.printf("index: %d%ncalcId: %d%nunitId: %d%nordinal: %d%nfullname: %s%n%n",
								index, calcID, unitID, ordinal, fullname);
			}
			if (!fullname.equals("Scan Count")){
				sensors.add(new SensorInfo(unitID, 0, calcID, ordinal, fullname));
			}
		}
	}

	/**
	 * Gets the list of elements from DataCnv
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	
	public static List<Element> readDataCnv(File file) throws IOException,
			JDOMException {
		SAXBuilder builder = new SAXBuilder();

		Document readDoc = builder.build(file);

		Element rootEle = readDoc.getRootElement();
		Element calcArray = rootEle.getChild("CalcArray");
		List<Element> calcArrayItems = calcArray.getChildren();

		if (DEBUG) {
			System.out.println();
			System.out.println(calcArrayItems.toString());
		}
		return calcArrayItems;
	}
}