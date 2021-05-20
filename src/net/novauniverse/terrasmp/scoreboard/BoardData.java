package net.novauniverse.terrasmp.scoreboard;

public class BoardData {
	private String atLocation;
	private String inFaction;
	private String power;
	private String factionPower;

	public BoardData() {
		this.atLocation = "";
		this.inFaction = "";
		this.power = "";
		this.factionPower = "";
	}
	
	public void setAtLocation(String atLocation) {
		this.atLocation = atLocation;
	}

	public String getAtLocation() {
		return atLocation;
	}

	public void setInFaction(String inFaction) {
		this.inFaction = inFaction;
	}

	public String getInFaction() {
		return inFaction;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getPower() {
		return power;
	}

	public void setFactionPower(String factionPower) {
		this.factionPower = factionPower;
	}

	public String getFactionPower() {
		return factionPower;
	}
}