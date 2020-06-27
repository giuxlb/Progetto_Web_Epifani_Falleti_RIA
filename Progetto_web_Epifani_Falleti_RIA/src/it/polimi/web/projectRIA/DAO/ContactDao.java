package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContactDao {
	private Connection con;

	public ContactDao(Connection c) {
		this.con = c;
	}

	public List<Integer> contactsOfUser(int userid) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.contatto where ownerUserID = ?";
		List<Integer> contacts = new ArrayList<Integer>();

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return contacts;
				else {
					while (result.next()) {
						contacts.add(result.getInt("contactUserID"));
					}

				}
			}
		}
		return contacts;
	}

	public void createContact(int userid, int bankAccountID) throws SQLException {
		String query = "INSERT INTO esercizio4RIA.contatto (ownerUserID,contactUserID) VALUES(?,?)";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			pstatement.setInt(2, bankAccountID);
			pstatement.execute();

		}
	}

}
