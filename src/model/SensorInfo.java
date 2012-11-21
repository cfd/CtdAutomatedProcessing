package model;

public class SensorInfo {
	
	private int unitID;
	private int sensorID;
	private int calcID;
	private int ordinal;

	private String fullname;
	
	public SensorInfo(int unitID, int sensorID, int calcID, int ordinal, String fullname) {
		super();
		this.unitID = unitID;
		this.sensorID = sensorID;
		this.calcID = calcID;
		this.ordinal = ordinal;
		this.fullname = fullname;
	}

	public int getUnitID() {
		return unitID;
	}


	public void setUnitID(int unitID) {
		this.unitID = unitID;
	}


	public int getSensorID() {
		return sensorID;
	}


	public void setSensorID(int sensorID) {
		this.sensorID = sensorID;
	}


	public int getCalcID() {
		return calcID;
	}


	public void setCalcID(int calcID) {
		this.calcID = calcID;
	}
	
	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}



	public String getFullname() {
		return fullname;
	}


	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
}
