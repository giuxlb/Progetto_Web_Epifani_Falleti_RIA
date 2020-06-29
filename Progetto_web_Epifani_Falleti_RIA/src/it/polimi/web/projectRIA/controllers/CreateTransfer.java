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

import it.polimi.web.projectRIA.DAO.BankAccountDao;
import it.polimi.web.projectRIA.DAO.TransferDao;
import it.polimi.web.projectRIA.beans.BankAccount;
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
		Integer DestBankAccountID = null;
		Integer amount = null;
		String purpose = null;
		User user = (User) session.getAttribute("user");
		TransferDao trasferimentoDao = new TransferDao(connection);
		Integer UserID = user.getId();
		Integer bankAccountID = (Integer) session.getAttribute("contoid");
		try {
			DestUserID = Integer.parseInt(request.getParameter("destUserID"));
			DestBankAccountID = Integer.parseInt(request.getParameter("destContoID"));
			amount = Integer.parseInt(request.getParameter("amount"));
			purpose = StringEscapeUtils.escapeJava(request.getParameter("causale"));
			isRequestBad = purpose.isEmpty() || DestUserID < 0 || DestBankAccountID < 0 || amount <= 0
					|| bankAccountID == DestBankAccountID;
		} catch (NumberFormatException | NullPointerException e) {
			isRequestBad = true;
			e.printStackTrace();
		}
		if (isRequestBad) {
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.getWriter().println("The request is bad");
			return;
		}

		int transferValue = -1;
		try {
			transferValue = trasferimentoDao.createTransfer(DestUserID, DestBankAccountID, UserID, bankAccountID,
					amount, purpose);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}

		BankAccount c = new BankAccount();
		BankAccountDao cdao = new BankAccountDao(connection);
		try {
			c = cdao.findBankAccountByBankAccountID(bankAccountID);
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad request");
		}
		switch (transferValue) {

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
			response.getWriter().println(DestBankAccountID + " " + DestUserID + " " + bankAccountID + " " + user.getId()
					+ " " + c.getBalance());
		}
	}

}
