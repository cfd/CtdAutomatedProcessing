package strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import model.SensorInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import util.ConnectDB;
import util.HexReader;
import util.RunSeabird;

public class XmlconReader {

	private static LinkedHashMap<Integer, SensorInfo> sensorsMap;
	private static  ArrayList<SensorInfo> orderedSensors;

	private Connection con;
	private Statement statement;
	private static String userPoly;
	private static final String DIRECTORY = "\\\\pearl\\temp\\adc-jcu2012";

	private IPsaWriter writerType;

	public XmlconReader(IPsaWriter psaWriter) {
		this.writerType = psaWriter;
		sensorsMap = new LinkedHashMap<>();
		orderedSensors = new ArrayList<>();
	}

	public static LinkedHashMap<Integer, SensorInfo> getSensorsMap() {
		return sensorsMap;
	}

	public IPsaWriter getWriterType() {
		return writerType;
	}

	public List<Element> readXmlcon(File file) throws IOException,
			JDOMException {
		SAXBuilder builder = new SAXBuilder();

		Document readDoc = builder.build(file);

		Element rootEle = readDoc.getRootElement();
		Element sensorArrayEle = rootEle.getChild("Instrument").getChild(
				"SensorArray");
		List<Element> sensorsInXmlcon = sensorArrayEle.getChildren();
		for (Element e : sensorsInXmlcon) {
			Element child = e.getChildren().get(0);
			if (child.getName().equals("UserPolynomialSensor")) {
				Element sensorName = child.getChild("SensorName");
				userPoly = sensorName.getValue();
			}
			System.out.println(e.getAttributeValue("SensorID"));
		}
		System.out.println();
		return sensorsInXmlcon;
	}

	// private String getFileName(String file) {
	// File xmlFile = new File(file);
	// String fileName = xmlFile.getName();
	// return null;
	// }

	public void populateSensorsMap() {
		ConnectDB db = new ConnectDB();
		con = db.getDdConnection();
		try {
			statement = con.createStatement();
			System.out.println(con);
			System.out.println(statement);
			ResultSet results = getAllAttributes();
			while (results.next()) {
				int sensorID = results.getInt("sensor_ID");
				int calcID = results.getInt("calc_ID");
				int unitID = results.getInt("unit_ID");
				int ordinal = results.getInt("ordinal");
				String name = results.getString("full_name");
				getSensorsMap()
						.put(sensorID, new SensorInfo(unitID, sensorID, calcID, ordinal, name));
			}
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ResultSet getAllAttributes() {
		try {
			String sql = "SELECT * FROM sensor_info";
			ResultSet rs = statement.executeQuery(sql);
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void sortSensors(List<Element> sensorsInXmlcon) {
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Pressure", sensor);
		}
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Temperature", sensor);
		}
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Conductivity", sensor);
		}
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Fluorescence", sensor);
		}
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("OBS", sensor);
		}
		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Upoly", sensor);
		}

		SensorInfo freq = sensorsMap.get(-5);
		SensorInfo desRate = sensorsMap.get(-3);
		SensorInfo density = sensorsMap.get(-4);

		orderedSensors.add(freq);
		orderedSensors.add(desRate);
		orderedSensors.add(density);

		for (Element sensor : sensorsInXmlcon) {
			insertSensor("Oxygen", sensor);
		}

	}

	private void insertSensor(String sensorName, Element sensor) {

		SensorInfo info = sensorsMap.get(Integer.parseInt(sensor
				.getAttributeValue("SensorID")));
		if (info != null) {
			if (info.getFullName().startsWith(sensorName)) {
				orderedSensors.add(info);
			}
		}
	}

	public static void main(String args[]) {
		ArrayList<XmlconReader> writers = new ArrayList<>();

		XmlconReader datCnvWriter = new XmlconReader(new DatCnvWriter());
		XmlconReader alignWriter = new XmlconReader(new AlignWriter());
		XmlconReader filterWriter = new XmlconReader(new FilterWriter());
		XmlconReader binAvgWriter = new XmlconReader(new BinAvgWriter());
		XmlconReader deriveWriter = new XmlconReader(new DeriveWriter());
		XmlconReader loopEditWriter = new XmlconReader(new LoopEditWriter());
		
		// Comment in when you want sea bird to run
		RunSeabird runSeabird = new RunSeabird(DIRECTORY, ".xmlcon", "xmlProcessSeabirds.bat");

		datCnvWriter.populateSensorsMap();

		File dir = new File(DIRECTORY + "/xmlcons");

		for (File xml : dir.listFiles()) {
			if (xml.getName().endsWith(".xmlcon")) {
				try {
					List<Element> sensorsInXmlcon = datCnvWriter
							.readXmlcon(xml);
					datCnvWriter.sortSensors(sensorsInXmlcon);
					// String FileName =
					// getFileName("xmlcons/NRS2_6390_01102011_O2andNTU.xmlcon");

				} catch (Exception e) {
					e.printStackTrace();
				}

				writers.add(datCnvWriter);
				writers.add(alignWriter);
				writers.add(filterWriter);
				writers.add(binAvgWriter);
				writers.add(deriveWriter);
				writers.add(loopEditWriter);

				// Where the psa writes tod

				String outputDirName = DIRECTORY + "\\config\\"
						+ xml.getName().replaceFirst("[.][^.]+$", "");

				createDirectory(outputDirName);

				// new File(outputDirName).mkdir();
				//
				// // Makes the data stuff
				// new File(outputDirName + "/data").mkdir();
				// new File(outputDirName + "/data/raw").mkdir();
				// new File(outputDirName + "/data/batch").mkdir();
				// new File(outputDirName + "/data/final").mkdir();

				// Where the batch, final and raw files are located
				String workingDirectory = outputDirName + "\\data\\";

				// Where the xml con is
				String xmlLocation = outputDirName + "\\" + xml.getName();

				for (XmlconReader writer : writers) {
					try {
						writer.getWriterType().setup(orderedSensors);
						writer.getWriterType().readTemplate(
								DIRECTORY + "\\utilities\\psa_templates");
						writer.getWriterType().writeUpperSection(
								workingDirectory, xmlLocation);
						writer.getWriterType().writeCalcArray(userPoly);
						writer.getWriterType().writeLowerSection();
						writer.getWriterType().writeToNewPsaFile(outputDirName);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// for (SensorInfo i : orderedSensors) {
					// System.out.println(i.getFullname());

					// }
				}
				orderedSensors.clear();

				// Creates the bat files for the seabird processing
				createRunBat(outputDirName);
				createProcessSbbat(outputDirName, xml.getName());
				runSeabird.setBatch(xml.getName());
				
				// Moves the file
				moveCon(xml, outputDirName);

			} else if (xml.getName().endsWith(".con")) {
				String outputDirName = DIRECTORY + "\\config\\"
						+ xml.getName().replaceFirst("[.][^.]+$", "");

				createDirectory(outputDirName);

				// Creates the bat files for the seabird processing
				createRunBat(outputDirName);
				createProcessSbbat(outputDirName, xml.getName());

				// Moves the file
				moveCon(xml, outputDirName);
			}
		}
		
		runSeabird.writeBatch();
//		// Comment in when you want sea bird to run
//		RunSeabird runSeabird = new RunSeabird(DIRECTORY, ".xmlcon", "xmlProcessSeabirds.bat");
//		runSeabird.run();
		
		findHex(new File(DIRECTORY + "/hex"));
	}
	
	private static void findHex(File hexDir){
		for (File hex : hexDir.listFiles()){
			if(hex.isDirectory()){
				findHex(hex);
			}else if(hex.getName().endsWith(".hex")) {
				HexReader reader = new HexReader(hex);
				reader.run();
			}
		}
	}

	private static void createDirectory(String outputDirName) {
		new File(outputDirName).mkdir();

		// Makes the data stuff
		new File(outputDirName + "/data").mkdir();
		new File(outputDirName + "/data/raw").mkdir();
		new File(outputDirName + "/data/batch").mkdir();
		new File(outputDirName + "/data/final").mkdir();

	}

	private static void moveCon(File xml, String outputDirName) {

		InputStream inStream = null;
		OutputStream outStream = null;

		try {
			inStream = new FileInputStream(xml);
			outStream = new FileOutputStream(new File(outputDirName + "/"
					+ xml.getName()));

			byte[] buffer = new byte[1024];
			int length;

			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, length);
			}

			inStream.close();
			outStream.close();

			// delete the original file
			xml.delete();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates the process batch file for each directory
	 * 
	 * @param outputDirName
	 * @param name
	 */
	private static void createProcessSbbat(String outputDirName, String name) {
		File file = new File(outputDirName + "\\process.sbbat");
		PrintWriter fout = null;
		try {
			fout = new PrintWriter(file.getAbsolutePath());
			// Write the stuff

			// /datcnv
			// i"\\pearl\temp\adc-jcu2012\config\19plus1_4409_20120905.xmlcon\data\raw\%1.hex"
			// /c"\\pearl\temp\adc-jcu2012\config\19plus1_4409_20120905.xmlcon\19plus1_4409_20120905.xmlcon"
			// /p"\\pearl\temp\adc-jcu2012\config\19plus1_4409_20120905.xmlcon\DatCnvIMOS.psa"
			// /o"\\pearl\temp\adc-jcu2012\config\19plus1_4409_20120905.xmlcon\data\batch"
			// /aC
			fout.println("@Use: sbebatch AIMS-IMOS_CTD_batch.bat");
			fout.println("datcnv /i\"" + outputDirName
					+ "\\data\\raw\\%1.hex\" /c\"" + outputDirName + "\\"
					+ name + "\" /p\"" + outputDirName + "\\DatCnvIMOS.psa"
					+ "\" /o\"" + outputDirName + "\\data\\batch\" /aC");

			// filter
			// /i"\\pearl\aims-data\CTD\imos-processed\data\Batch\%1C.cnv"
			// /p"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\FilterIMOS.psa"
			// /o"\\pearl\aims-data\CTD\imos-processed\data\Batch" /aF
			fout.println("filter /i\"" + outputDirName
					+ "\\data\\batch\\%1C.cnv\" /p\"" + outputDirName
					+ "\\FilterIMOS.psa\" /o\"" + outputDirName
					+ "\\data\\batch\" /aF");

			// alignctd
			// /i"\\pearl\aims-data\CTD\imos-processed\data\Batch\%1CF.cnv"
			// /p"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\AlignIMOS.psa"
			// /o"\\pearl\aims-data\CTD\imos-processed\data\Batch" /aA
			fout.println("alignctd /i\"" + outputDirName
					+ "\\data\\batch\\%1CF.cnv\" /p\"" + outputDirName
					+ "\\AlignIMOS.psa\" /o\"" + outputDirName
					+ "\\data\\batch\" /aA");

			// loopedit
			// /i"\\pearl\aims-data\CTD\imos-processed\data\Batch\%1CFA.cnv"
			// /p"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\LoopEditIMOS.psa"
			// /o"\\pearl\aims-data\CTD\imos-processed\data\Batch" /aL
			fout.println("loopedit /i\"" + outputDirName
					+ "\\data\\batch\\%1CFA.cnv\" /p\"" + outputDirName
					+ "\\LoopEditIMOS.psa\" /o\"" + outputDirName
					+ "\\data\\batch\" /aL");

			// derive
			// /i"\\pearl\aims-data\CTD\imos-processed\data\Batch\%1CFAL.cnv"
			// /c"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\19plus2.con"
			// /p"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\DeriveIMOS.psa"
			// /o"\\pearl\aims-data\CTD\imos-processed\data\Batch" /aD
			fout.println("derive /i\"" + outputDirName
					+ "\\data\\batch\\%1CFAL.cnv\" /c\"" + outputDirName + "\\"
					+ name + "\" /p\"" + outputDirName + "\\DeriveIMOS.psa"
					+ "\" /o\"" + outputDirName + "\\data\\batch\" /aD");

			// binavg
			// /i"\\pearl\aims-data\CTD\imos-processed\data\Batch\%1CFALD.cnv"
			// /p"\\Pearl\aims-data\CTD\imos-processed\config\SBE19plusV2_4525\SBE19plusV2_4525_20040204\BinAvgIMOS.psa"
			// /o"\\pearl\aims-data\CTD\imos-processed\data\final" /aB
			fout.println("binavg /i\"" + outputDirName
					+ "\\data\\batch\\%1CFALD.cnv\" /p\"" + outputDirName
					+ "\\BinAvgIMOS.psa\" /o\"" + outputDirName
					+ "\\data\\final\" /aB");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			fout.close();
		}

	}

	private static void createRunBat(String outputDirName) {
		File file = new File(outputDirName + "\\run.bat");
		// Creates a new Print Writer
		PrintWriter fout = null;
		try {
			fout = new PrintWriter(file.getAbsolutePath());
			fout.println("sbebatch " + outputDirName
					+ "\\process.sbbat *\nEXIT [/B] [exitCode] ");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			fout.close();
		}
	}
}