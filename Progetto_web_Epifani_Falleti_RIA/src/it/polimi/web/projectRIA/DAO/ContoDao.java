package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.web.projectRIA.beans.Count;

public class ContoDao {
	private Connection con;

	public ContoDao(Connection c) {
		this.con = c;
	}

	public List<Count> findContoByUser(int userId) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.conto where userID = ?";
		List<Count> conti = new ArrayList<Count>();
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return conti;
				else {
					while (result.next()) {
						Count c = new Count();
						c.setID(result.getInt("contoID"));
						c.setBalance(result.getInt("saldo"));
						c.setUserID(userId);
						conti.add(c);
					}

				}
			}
		}
		return conti;
	}

	public void changeSaldo(int importo, int contoid) throws SQLException {
		String queryUpdate = "UPDATE esercizio4RIA.conto set saldo = ? where contoID = ?";
		String query = "SELECT saldo FROM esercizio4RIA.conto where contoID = ?";
		int saldoAttuale;
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, contoid);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return;
				else {
					result.next();
					saldoAttuale = result.getInt("saldo");
					saldoAttuale += importo;

					try (PreparedStatement statement = con.prepareStatement(queryUpdate)) {
						statement.setInt(1, saldoAttuale);
						statement.setInt(2, contoid);
						statement.executeUpdate();
					}

				}
			}
		}

	}

	public Count findContoByContoID(int contoID) throws SQLException {
		String query = "SELECT * FROM esercizio4RIA.conto where contoID = ?";
		Count conto = new Count();
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, contoID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (!result.isBeforeFirst())
					return null;
				else {
					result.next();
					conto.setID(result.getInt("contoID"));
					conto.setBalance(result.getInt("saldo"));
					conto.setUserID(result.getInt("userID"));
				}
			}
		}
		return conto;
	}
}
