package strategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import model.SensorInfo;


/**
 * This file implements the strategy pattern within the 
 * program that is used to create the six different .psa files.
 */

public interface IPsaWriter {
	
	public abstract void setup(ArrayList<SensorInfo> orderedSensors);
	
	public abstract void readTemplate(String psaTemplateFolderPath) throws JDOMException, IOException;
	
	public abstract void writeUpperSection(String workingDirectory, String instrumentPath);
	
	public abstract void writeCalcArray(String userPoly);
	
	public abstract void writeLowerSection();
	
	public abstract void writeToNewPsaFile(String outputDirName) throws FileNotFoundException, IOException;

}
