package strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.SensorInfo;

import org.jdom2.Element;

public class AlignWriter implements IPsaWriter{
	
	ArrayList<SensorInfo> sensors;
	
	@Override
	public void setup(ArrayList<SensorInfo> orderedSensors) {
		sensors = orderedSensors;
		System.out.println();
		System.out.println("3 strategy");
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
	public void writeCalcArray() {
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
