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
import it.polimi.web.projectRIA.beans.Conto;
import it.polimi.web.projectRIA.beans.User;
import it.polimi.web.projectRIA.utils.ConnectionHandler;

/**
 * Servlet implementation class GetConti
 */
@WebServlet("/GetConti")
public class GetConti extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetConti() {
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
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		ContoDao contodao = new ContoDao(connection);
		List<Conto> conti = new ArrayList<Conto>();
		try {
			conti = contodao.findContoByUser(user.getId());
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover bank accounts");
			return;
		}

		if (conti.size() == 0) {
			System.out.println("NON CI SONO conti");
			response.setStatus(HttpServletResponse.SC_LENGTH_REQUIRED);
			response.getWriter().write("No bank accounts available");
			return;
		}
		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(conti);

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
