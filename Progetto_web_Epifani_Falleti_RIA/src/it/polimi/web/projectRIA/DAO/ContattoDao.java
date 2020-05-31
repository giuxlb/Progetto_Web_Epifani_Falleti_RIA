package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.web.projectRIA.beans.Conto;

public class ContattoDao {
	private Connection con;

	public ContattoDao(Connection c) {
		this.con = c;
	}

	public List<Conto> contactsOfUser(int userid) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.contatto where ownerUserID = ?";
		List<Conto> conti = new ArrayList<Conto>();
		ContoDao cdao = new ContoDao(con);
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return conti;
				else {
					while (result.next()) {
						List<Conto> contiContatto = new ArrayList<Conto>();
						contiContatto = cdao.findContoByUser(result.getInt("contactUserID"));
						for (int i = 0; i < contiContatto.size(); i++) {
							conti.add(contiContatto.get(i));
						}
					}

				}
			}
		}
		return conti; // ritorna tutti i conti di tutti i contatti dello user con id = userid
	}

	public void creaContatto(int userid, int contattoid) throws SQLException {
		String query = "INSERT INTO esercizio4RIA.contatto (ownerUserID,contactUserID) VALUES(?,?)";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userid);
			pstatement.setInt(2, contattoid);
			pstatement.execute();

		}
	}

}
