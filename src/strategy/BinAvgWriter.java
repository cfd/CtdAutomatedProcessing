package strategy;

import java.util.ArrayList;

import model.SensorInfo;

public class BinAvgWriter implements IPsaWriter{

	ArrayList<SensorInfo> sensors;
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("4 strategy");
		System.out.println(orderedSensors);
		
	}
	
	@Override
	public void readTemplate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeUpperSection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeCalcArray(String userPoly) {
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
