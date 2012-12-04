package strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class Writer {

	private static LinkedHashMap<Integer, SensorInfo> sensorsMap;
	private static ArrayList<SensorInfo> orderedSensors;

	private static Connection con;
	private static Statement statement;
	private static String userPoly;
	private static final String DIRECTORY = "\\\\pearl\\temp\\adc-jcu2012";

	private IPsaWriter writerType;

	public Writer(IPsaWriter psaWriter) {
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

			ResultSet results = getAllAttributes();
			while (results.next()) {
				int sensorID = results.getInt("sensor_ID");
				int calcID = results.getInt("calc_ID");
				int unitID = results.getInt("unit_ID");
				int ordinal = results.getInt("ordinal");
				String name = results.getString("full_name");
				getSensorsMap()
						.put(sensorID,
								new SensorInfo(unitID, sensorID, calcID,
										ordinal, name));
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
		ArrayList<Writer> writers = new ArrayList<>();

		Writer datCnvWriter = new Writer(new DatCnvWriter());
		Writer alignWriter = new Writer(new AlignWriter());
		Writer filterWriter = new Writer(new FilterWriter());
		Writer binAvgWriter = new Writer(new BinAvgWriter());
		Writer deriveWriter = new Writer(new DeriveWriter());
		Writer loopEditWriter = new Writer(new LoopEditWriter());

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

				// Where the psa writes to
				String outputDirName = DIRECTORY + "\\config\\" + xml.getName();
				new File(outputDirName).mkdir();

				// Makes the data stuff
				new File(outputDirName + "/data").mkdir();
				new File(outputDirName + "/data/raw").mkdir();
				new File(outputDirName + "/data/batch").mkdir();
				new File(outputDirName + "/data/final").mkdir();

				// Where the batch, final and raw files are located
				String workingDirectory = outputDirName + "\\data\\";

				// Where the xml con is
				String xmlLocation = outputDirName + "\\" + xml.getName();

				for (Writer writer : writers) {
					try {
						writer.getWriterType().setup(orderedSensors);
						writer.getWriterType().readTemplate();
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

				InputStream inStream = null;
				OutputStream outStream = null;

				try {
					inStream = new FileInputStream(xml);
					outStream = new FileOutputStream(new File(outputDirName
							+ "/" + xml.getName()));

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
		}
	}
}