package model;

/**
 * SensorInfo is the class in which sensor info is used to carry across information for the
 * individual sensors. They're primary used in the .psa writers for writing the CalcArray 
 * section.
 */

public class SensorInfo {

	private int unitID;
	private int sensorID;
	private int calcID;
	private int ordinal;

	private String fullName;
	
	public SensorInfo(int unitID, int sensorID, int calcID, int ordinal, String fullname) {
		super();
		this.unitID = unitID;
		this.sensorID = sensorID;
		this.calcID = calcID;
		this.ordinal = ordinal;
		this.fullName = fullname;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullname) {
		this.fullName = fullname;
	}
	
	public String shortenName(int i) {
			return fullName.substring(0, i-1);
	}
}
