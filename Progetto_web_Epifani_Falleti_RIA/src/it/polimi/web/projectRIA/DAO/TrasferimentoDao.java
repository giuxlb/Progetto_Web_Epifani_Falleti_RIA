package it.polimi.web.projectRIA.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.polimi.web.projectRIA.beans.Conto;
import it.polimi.web.projectRIA.beans.Trasferimento;

public class TrasferimentoDao {

	private Connection con;

	public TrasferimentoDao(Connection c) {
		this.con = c;
	}

	public int createTrasferimento(int destUserID, int destContoID, int userID, int contoID, int importo,
			String causale) throws SQLException {
		ContoDao cdao = new ContoDao(con);

		List<Conto> conti = cdao.findContoByUser(destUserID);

		boolean verified1 = false;

		if (conti == null)
			return 0;

		for (int i = 0; i < conti.size(); i++) {
			if (conti.get(i).getID() == destContoID) // abbiamo trovato il conto
			{
				verified1 = true;
				break;
			}
		} // qui ho controllato se il conto sotto quel contoID è effettivamente dello user
			// id e se il saldo di quel conto, permette un trasferimento di quell'importo

		if (!verified1)
			return 0;

		Conto conto = cdao.findContoByContoID(contoID);
		if (conto.getSaldo() < importo)
			return 1;

		// se verified è ancora false, ritorniamo false

		int importoDest = 0 - importo;
		// qui verified sarà true, quindi possiamo modificare sia l'importo del conto di
		// destinazione, sia quello di quello di origine origine e aggiungere il
		// trasferimento al dbms
		cdao.changeSaldo(importo, destContoID);
		cdao.changeSaldo(importoDest, contoID);

		java.util.Date d = new java.util.Date();

		String query = "INSERT INTO esercizio4.trasferimento (DestContoID,ContoID,causale,importo,data) VALUES(?,?,?,?,?)";
		try (PreparedStatement statement = con.prepareStatement(query)) {
			statement.setInt(1, destContoID);
			statement.setInt(2, contoID);
			statement.setString(3, causale);
			statement.setInt(4, importo);
			statement.setDate(5, new java.sql.Date(d.getTime()));
			statement.execute();

		}

		// TO-DO ricordare a peppe l'aggiunta del parametro session.contoID
		return 2;
	}

	public List<Trasferimento> findTrasferimentibyConto(int contoID) throws SQLException {
		List<Trasferimento> trasferimenti = new ArrayList<Trasferimento>();
		String query = "SELECT * FROM esercizio4.trasferimento where ContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, contoID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()){
					while (result.next()) {
						Trasferimento t = new Trasferimento();
						t.setTrasferimentoID(result.getInt("trasferimentoID"));
						t.setCausale(result.getString("causale"));
						t.setContoID(contoID);
						t.setData(result.getDate("data"));
						t.setImporto(result.getInt("importo"));
						t.setDestContoId(result.getInt("DestContoID"));
						trasferimenti.add(t);
					}
				}
			}
		}
		query = "SELECT * FROM esercizio4.trasferimento where DestContoID = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
			pstatement.setInt(1, contoID);
			try (ResultSet result = pstatement.executeQuery()) {
				if (result.isBeforeFirst()){
					while (result.next()) {
						Trasferimento t = new Trasferimento();
						t.setTrasferimentoID(result.getInt("trasferimentoID"));
						t.setCausale(result.getString("causale"));
						t.setContoID(contoID);
						t.setData(result.getDate("data"));
						t.setImporto(result.getInt("importo"));
						t.setDestContoId(result.getInt("DestContoID"));
						trasferimenti.add(t);
					}
				}
			}
		}
		trasferimenti.sort(new Comparator<Trasferimento>() {
			@Override
			public int compare(Trasferimento t1, Trasferimento t2) {
				if (t1.getData().compareTo(t2.getData()) == 0) {
					return t2.getTrasferimentoID() - t1.getTrasferimentoID();
				} else if (t1.getData().compareTo(t2.getData()) > 0)
					return -1;
				return 1;
			}
		});
		return trasferimenti;
	}

}