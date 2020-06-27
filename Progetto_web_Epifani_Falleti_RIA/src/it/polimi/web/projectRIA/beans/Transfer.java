package it.polimi.web.projectRIA.beans;

import java.sql.Date;

public class Transfer {
	private int transferID;
	private Date data;
	private int amount;
	private String causal;
	private int countID;
	private int destCountId;

	public int getTransferID() {
		return this.transferID;
	}

	public void setTransferID(int id) {
		this.transferID = id;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getCausal() {
		return causal;
	}

	public void setCausal(String causal) {
		this.causal = causal;
	}

	public int getCountID() {
		return countID;
	}

	public void setCountID(int countID) {
		this.countID = countID;
	}

	public int getDestCountId() {
		return destCountId;
	}

	public void setDestCountId(int destContoId) {
		this.destCountId = destContoId;
	}
}
