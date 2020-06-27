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

import it.polimi.web.projectRIA.DAO.ContattoDao;
import it.polimi.web.projectRIA.beans.User;
import it.polimi.web.projectRIA.utils.ConnectionHandler;

/**
 * Servlet implementation class GetContatti
 */
@WebServlet("/GetContacts")
public class GetContacts extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetContacts() {
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
		ContattoDao contactdao = new ContattoDao(connection);
		List<Integer> contacts = new ArrayList<Integer>();

		try {
			contacts = contactdao.contactsOfUser(user.getId());
		} catch (SQLException e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover bank accounts");
			return;
		}

		List<Integer> toSend = new ArrayList<Integer>();

		for (int i = 0; i < contacts.size(); i++) {
			if (!toSend.contains(contacts.get(i)))
				toSend.add(contacts.get(i));
		}

		Gson gson = new GsonBuilder().create();
		String json = gson.toJson(toSend);

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
