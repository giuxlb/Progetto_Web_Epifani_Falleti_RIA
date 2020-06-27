package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContattoDao {
	private Connection con;

	public ContattoDao(Connection c) {
		this.con = c;
	}

	public List<Integer> contactsOfUser(int userid) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.contatto where ownerUserID = ?";
		List<Integer> contatti = new ArrayList<Integer>();

		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return contatti;
				else {
					while (result.next()) {
						contatti.add(result.getInt("contactUserID"));
					}

				}
			}
		}
		return contatti; // ritorna tutti i conti di tutti i contatti dello user con id = userid
	}

	public void createContact(int userid, int contattoid) throws SQLException {
		String query = "INSERT INTO esercizio4RIA.contatto (ownerUserID,contactUserID) VALUES(?,?)";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			pstatement.setInt(2, contattoid);
			pstatement.execute();

		}
	}

}
