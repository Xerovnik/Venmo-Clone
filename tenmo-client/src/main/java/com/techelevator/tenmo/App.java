package com.techelevator.tenmo;

import java.security.Principal;
import java.util.Scanner;

import com.techelevator.projects.model.ToFromTransfer;

import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.TransferDetails;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.tenmo.services.UserService;
import com.techelevator.view.ConsoleService;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";

	private static final String MENU_OPTION_EXIT = "Exit";
	private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN,
			MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS,
			MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS,
			MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private AuthenticatedUser currentUser;
	private ConsoleService console;
	private AuthenticationService authenticationService;
	private UserService userService = new UserService();

	
	public static void main(String[] args) {
		App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
		app.run();
	}

	public App(ConsoleService console, AuthenticationService authenticationService) {
		this.console = console;
		this.authenticationService = authenticationService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");

		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while (true) {
			String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		System.out.println("The current balance is: " + userService.getBalanceObject().getBalance());
	}

	private void viewTransferHistory() {
		
		ToFromTransfer[] tFTransferList = userService.toFromTransfer();
		System.out.println("-------------------------------------------------------- ");
		System.out.println("Transfer Id" + "   " + "From/To" + "   " + "Amount");
		System.out.println("-------------------------------------------------------- ");
		for (ToFromTransfer tft : tFTransferList) {
			System.out.println("     "+tft.getTransfer_id() + "        " + tft.getToFromUserName() + "     " + tft.getAmount());
		}
		System.out.println("-------------------------------------------------------- ");

		Scanner scanner=new Scanner(System.in);
		System.out.println("Please enter transfer ID to view details");
		
		int id = scanner.nextInt();
		
		
		TransferDetails[] transDetails = userService.transdetails(id);
		
		for (TransferDetails tf : transDetails ) {
			System.out.println("-------------------------------------------------------- ");
			System.out.println("Transfer Details");
			System.out.println("-------------------------------------------------------- ");
			System.out.println("id: " + tf.getId());
			System.out.println("From: " + tf.getFrom());
			System.out.println("To: " + tf.getTo());
			System.out.println("Type: " + tf.getType());
			System.out.println("Status: " + tf.getStatus() );
			System.out.println("Amount: " + tf.getAmount());
			System.out.println("-------------------------------------------------------- ");
		}

	}

	private void viewPendingRequests() {

	}

	private void sendBucks() {
		Principal principal = null;
		
		// display user list

		Scanner moneyAmount = new Scanner(System.in);
		System.out.println("Please input the amount you wish to transfer: ");
		try {
		String moneyInput = moneyAmount.nextLine();
		Double moneyAmountValue = Double.parseDouble(moneyInput);

			Scanner scanner = new Scanner(System.in);
			userService.toTransfer(currentUser);
			System.out.println("Please input the ID of the account to which you want to transfer: ");
			String userInput = scanner.nextLine();
			Integer userInputValue = Integer.parseInt(userInput);
			Integer userId = currentUser.getUser().getId();
			userService.executeTransfer(userId, userInputValue, moneyAmountValue);
		} catch(Exception e) {
			System.out.println("Invalid data. Please try again.");
		}
		

	}

	private void requestBucks() {

	}

	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while (!isAuthenticated()) {
			String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
		while (!isRegistered) // will keep looping until user is registered
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				authenticationService.register(credentials);
				isRegistered = true;
				System.out.println("Registration successful. You can now login.");
			} catch (AuthenticationServiceException e) {
				System.out.println("REGISTRATION ERROR: " + e.getMessage());
				System.out.println("Please attempt to register again.");
			}
		}
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) // will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
			try {
				currentUser = authenticationService.login(credentials);
				String token = currentUser.getToken();
				UserService.AUTH_TOKEN = token;
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: " + e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}

	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
