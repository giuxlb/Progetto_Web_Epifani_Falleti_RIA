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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.web.projectRIA.DAO.ContoDao;
import it.polimi.web.projectRIA.DAO.TrasferimentoDao;
import it.polimi.web.projectRIA.beans.Conto;
import it.polimi.web.projectRIA.beans.User;
import it.polimi.web.projectRIA.utils.ConnectionHandler;

/**
 * Servlet implementation class CreateTransfer
 */
@WebServlet("/CreateTransfer")
@MultipartConfig
public class CreateTransfer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateTransfer() {
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
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		boolean isRequestBad = false;
		Integer DestUserID = null;
		Integer DestContoID = null;
		Integer amount = null;
		String causale = null;
		try {
			DestUserID = Integer.parseInt(request.getParameter("destUserID"));
			DestContoID = Integer.parseInt(request.getParameter("destContoID"));
			amount = Integer.parseInt(request.getParameter("amount"));
			causale = StringEscapeUtils.escapeJava(request.getParameter("causale"));
			isRequestBad = causale.isEmpty() || DestUserID < 0 || DestContoID < 0 || amount < 0;
		} catch (NumberFormatException | NullPointerException e) {
			isRequestBad = true;
			e.printStackTrace();
		}
		if (isRequestBad) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect or missing param values");
			return;
		}

		User user = (User) session.getAttribute("user");
		TrasferimentoDao trasferimentoDao = new TrasferimentoDao(connection);
		Integer UserID = user.getId();
		Integer ContoID = (Integer) session.getAttribute("contoid");
		// Date data = System.currentTimeMillis();
		System.out.println("Sono qui");
		int trasferimentoValue = -1;
		try {
			trasferimentoValue = trasferimentoDao.createTrasferimento(DestUserID, DestContoID, UserID, ContoID, amount,
					causale);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}

		Conto c = new Conto();
		ContoDao cdao = new ContoDao(connection);
		try {
			c = cdao.findContoByContoID(ContoID);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}
		switch (trasferimentoValue) {

		case (0):
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.getWriter().println("The User ID you entered doesn't own that bank account");

			break;
		case (1):

			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.getWriter().println("The amount you entered isn't available in your balance");

			break;
		case (2):
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter()
					.println(DestContoID + " " + DestUserID + " " + ContoID + " " + user.getId() + " " + c.getSaldo());
		}
	}

}
