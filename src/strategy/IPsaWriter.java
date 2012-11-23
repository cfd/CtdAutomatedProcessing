package strategy;

import java.util.ArrayList;

import model.SensorInfo;

public interface IPsaWriter {
	
	public abstract void setup(ArrayList<SensorInfo> orderedSensors);
	
	public abstract void readTemplate();
	
	public abstract void writeUpperSection();
	
	public abstract void writeCalcArray();
	
	public abstract void writeLowerSection();
	
	public abstract void writeToNewPsaFile();

}
