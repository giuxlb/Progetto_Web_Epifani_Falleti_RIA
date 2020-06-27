package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.web.projectRIA.beans.BankAccount;

public class BankAccountDao {
	private Connection con;

	public BankAccountDao(Connection c) {
		this.con = c;
	}

	public List<BankAccount> findBankAccountByUser(int userId) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.conto where userID = ?";
		List<BankAccount> bankAccounts = new ArrayList<BankAccount>();
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return bankAccounts;
				else {
					while (result.next()) {
						BankAccount c = new BankAccount();
						c.setID(result.getInt("contoID"));
						c.setBalance(result.getInt("saldo"));
						c.setUserID(userId);
						bankAccounts.add(c);
					}

				}
			}
		}
		return bankAccounts;
	}

	public void changeBalance(int amount, int bankAccountid) throws SQLException {
		String queryUpdate = "UPDATE esercizio4RIA.conto set saldo = ? where contoID = ?";
		String query = "SELECT saldo FROM esercizio4RIA.conto where contoID = ?";
		int actualBalance;
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return;
				else {
					result.next();
					actualBalance = result.getInt("saldo");
					actualBalance += amount;

					try (PreparedStatement statement = con.prepareStatement(queryUpdate)) {
						statement.setInt(1, actualBalance);
						statement.setInt(2, bankAccountid);
						statement.executeUpdate();
					}

				}
			}
		}

	}

	public BankAccount findBankAccountByBankAccountID(int bankAccountID) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.conto where contoID = ?";
		BankAccount bankAccount = new BankAccount();
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, bankAccountID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					bankAccount.setID(result.getInt("contoID"));
					bankAccount.setBalance(result.getInt("saldo"));
					bankAccount.setUserID(result.getInt("userID"));
				}
			}
		}
		return bankAccount;
	}
}
