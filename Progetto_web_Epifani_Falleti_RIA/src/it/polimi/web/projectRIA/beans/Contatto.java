package it.polimi.web.projectRIA.beans;

public class Contatto {
	private int rubricaID;
	private int OwnerUserID;
	private int ContactUserID;
	
	public int getContactUserID() {
		return ContactUserID;
	}
	
	public void setContactUserID(int contactUserID) {
		ContactUserID = contactUserID;
	}
	
	public int getOwnerUserID() {
		return OwnerUserID;
	}
	
	public void setOwnerUserID(int ownerUserID) {
		OwnerUserID = ownerUserID;
	}
	
	public int getRubricaID() {
		return rubricaID;
	}
	
	public void setRubricaID(int rubricaID) {
		this.rubricaID = rubricaID;
	}
	
}
