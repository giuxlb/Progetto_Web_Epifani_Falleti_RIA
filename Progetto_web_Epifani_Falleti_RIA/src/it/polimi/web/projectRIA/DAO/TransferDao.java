package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.polimi.web.projectRIA.beans.BankAccount;
import it.polimi.web.projectRIA.beans.Transfer;

public class TransferDao {

	private Connection con;

	public TransferDao(Connection c) {
		this.con = c;
	}

	public int createTransfer(int destUserID, int destBankAccountID, int userID, int bankAccountID, int amount,
			String purpose) throws SQLException {
		BankAccountDao cdao = new BankAccountDao(con);

		List<BankAccount> bankAccounts = cdao.findBankAccountByUser(destUserID);

		boolean verified1 = false;

		if (bankAccounts == null)
			return 0;

		for (int i = 0; i < bankAccounts.size(); i++) {
			if (bankAccounts.get(i).getID() == destBankAccountID) {
				verified1 = true;
				break;
			}
		}
		if (!verified1)
			return 0;

		BankAccount bankAccount = cdao.findBankAccountByBankAccountID(bankAccountID);
		if (bankAccount.getBalance() < amount)
			return 1;

		int bankAccountDest = 0 - amount;

		cdao.changeBalance(amount, destBankAccountID);
		cdao.changeBalance(bankAccountDest, bankAccountID);

		java.util.Date d = new java.util.Date();

		String query = "INSERT INTO esercizio4RIA.trasferimento (DestContoID,ContoID,causale,importo,data) VALUES(?,?,?,?,?)";
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, destBankAccountID);
			statement.setInt(2, bankAccountID);
			statement.setString(3, purpose);
			statement.setInt(4, amount);
			statement.setDate(5, new java.sql.Date(d.getTime()));
			statement.execute();

		}

		return 2;
	}

	public List<Transfer> findTransferByBankAccountId(int bankAccountID) throws SQLException {
		List<Transfer> transfers = new ArrayList<Transfer>();
		String query = "SELECT * FROM esercizio4RIA.trasferimento where ContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()) {
					while (result.next()) {
						Transfer t = new Transfer();
						t.setTransferID(result.getInt("trasferimentoID"));
						t.setCausal(result.getString("causale"));
						t.setCountID(bankAccountID);
						t.setData(result.getDate("data"));
						t.setAmount(result.getInt("importo"));
						t.setDestCountId(result.getInt("DestContoID"));
						transfers.add(t);
					}
				}
			}
		}
		query = "SELECT * FROM esercizio4RIA.trasferimento where DestContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()) {
					while (result.next()) {
						Transfer t = new Transfer();
						t.setTransferID(result.getInt("trasferimentoID"));
						t.setCausal(result.getString("causale"));
						t.setCountID(bankAccountID);
						t.setData(result.getDate("data"));
						t.setAmount(result.getInt("importo"));
						t.setDestCountId(result.getInt("DestContoID"));
						transfers.add(t);
					}
				}
			}
		}
		transfers.sort(new Comparator<Transfer>() {
			@Override
			public int compare(Transfer t1, Transfer t2) {
				if (t1.getData().compareTo(t2.getData()) == 0) {
					return t2.getTransferID() - t1.getTransferID();
				} else if (t1.getData().compareTo(t2.getData()) > 0)
					return -1;
				return 1;
			}
		});
		return transfers;
	}

}
