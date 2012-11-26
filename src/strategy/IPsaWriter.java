package strategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.jdom2.JDOMException;

import model.SensorInfo;

public interface IPsaWriter {
	
	public abstract void setup(ArrayList<SensorInfo> orderedSensors);
	
	public abstract void readTemplate() throws JDOMException, IOException;
	
	public abstract void writeUpperSection();
	
	public abstract void writeCalcArray();
	
	public abstract void writeLowerSection();
	
	public abstract void writeToNewPsaFile() throws FileNotFoundException, IOException;

}
