package it.polimi.web.projectRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.web.projectRIA.DAO.ContoDao;
import it.polimi.web.projectRIA.DAO.TrasferimentoDao;
import it.polimi.web.projectRIA.beans.Count;
import it.polimi.web.projectRIA.beans.Transfer;
import it.polimi.web.projectRIA.beans.User;
import it.polimi.web.projectRIA.utils.ConnectionHandler;

/**
 * Servlet implementation class GetContoDetailsa
 */
@WebServlet("/GetContoDetail")
public class GetContoDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetContoDetail() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		Integer contoID = null;
		try {
			contoID = Integer.parseInt(request.getParameter("contoid"));

		} catch (NumberFormatException | NullPointerException e) {
			// only for debugging e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
		}
		ContoDao cdao = new ContoDao(connection);
		User u = (User) session.getAttribute("user");
		List<Count> conti = new ArrayList<Count>();
		try {
			conti = cdao.findContoByUser(u.getId());
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover bank accounts");
			return;
		}

		boolean ok = false;
		for (int i = 0; i < conti.size(); i++) {
			if (conti.get(i).getID() == contoID) {
				ok = true;
				break;
			}
		}

		if (!ok) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "You don't own that bank account");
			return;
		}
		session.setAttribute("contoid", contoID);
		TrasferimentoDao tDao = new TrasferimentoDao(connection);

		List<Transfer> trasferimenti = new ArrayList<Transfer>();
		try {
			trasferimenti = tDao.findTrasferimentibyConto(contoID);
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover transfers");
			return;
		}
		System.out.println(trasferimenti.size());
		if (trasferimenti.size() == 0) {
			System.out.println("NON CI SONO TRASFERIMENTI");
			response.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
			response.getWriter().write("No transfers available");
			return;
		}
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(trasferimenti);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
