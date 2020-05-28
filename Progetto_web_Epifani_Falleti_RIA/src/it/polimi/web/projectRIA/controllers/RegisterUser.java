package it.polimi.web.projectRIA.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.web.projectRIA.DAO.UserDao;
import it.polimi.web.projectRIA.utils.ConnectionHandler;
import it.polimi.web.projectRIA.utils.EmailCheck;

/**
 * Servlet implementation class RegisterUser
 */
@WebServlet("/RegisterUser")
@MultipartConfig
public class RegisterUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterUser() {
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
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = null;
		String pwd = null;
		String pwdrip = null;
		String name = null;
		String surname = null;

		username = StringEscapeUtils.escapeJava(request.getParameter("usernameR"));
		pwd = StringEscapeUtils.escapeJava(request.getParameter("pwdR"));
		pwdrip = StringEscapeUtils.escapeJava(request.getParameter("pwdripR"));
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		surname = StringEscapeUtils.escapeJava(request.getParameter("surname"));

		if (username == null || pwd == null || pwdrip == null || name == null || surname == null || username.isEmpty()
				|| pwd.isEmpty() || pwdrip.isEmpty() || name.isEmpty() || surname.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Credentials must be not null");
			return;
		}
		if (!pwd.equals(pwdrip)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The passwords don't match");
			return;
		}
		if (!EmailCheck.isValid(username)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("The email is non valid");
			return;
		}

		// query db to authenticate for user
		UserDao userDao = new UserDao(connection);
		boolean valid = false;
		try {
			valid = userDao.registerUser(username, pwd, name, surname);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			System.out.println(e.getStackTrace().toString());
			e.printStackTrace();
			response.getWriter().println("Internal server error, retry later");
			return;
		}

		if (!valid) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("That username already exists");
		} else {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(username);
		}
	}

}
