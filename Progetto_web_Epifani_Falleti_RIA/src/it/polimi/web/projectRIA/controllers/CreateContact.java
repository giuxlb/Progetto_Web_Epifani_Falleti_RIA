package it.polimi.web.projectRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.web.projectRIA.DAO.ContattoDao;
import it.polimi.web.projectRIA.beans.User;
import it.polimi.web.projectRIA.utils.ConnectionHandler;

/**
 * Servlet implementation class CreateContatto
 */
@WebServlet("/CreateContact")
public class CreateContact extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateContact() {
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
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User u = (User) session.getAttribute("user");
		Integer userId = u.getId();
		Integer contactId = 0;
		try {
			contactId = Integer.parseInt(request.getParameter("contattoID"));

		} catch (NumberFormatException | NullPointerException e) {
			e.printStackTrace();
		}
		ContattoDao contactDao = new ContattoDao(connection);

		try {
			contactDao.createContact(userId, contactId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}

	}

}
