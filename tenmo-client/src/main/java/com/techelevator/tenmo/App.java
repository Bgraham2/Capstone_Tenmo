package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;
import io.cucumber.java.bs.A;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final int APPROVED = 2;
    private final int PENDING = 1;
    private final int REJECTED = 3;
    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    //Merge transfers from current user with transfers to current user
    public List<Transfer> merge(List<Transfer> firstList, List<Transfer> secondList){
        List<Transfer> list = new ArrayList<>();
        Stream.of(firstList, secondList).forEach(list::addAll);

        return list;
    }

    //Fully Functional / Working
	private BigDecimal viewCurrentBalance() {
        // TODO Auto-generated method stub
        // Get the id of the current user
        long userId = currentUser.getUser().getId();
        // Get balance of the current user by id
        Account accountOfCurrentUser =  accountService.getBalance(userId);
        //System.out.println("Current balance is: " + accountOfCurrentUser.getBalance());
        System.out.println("Current balance is: " + NumberFormat.getCurrencyInstance().format(accountOfCurrentUser.getBalance()));
        System.out.println("Current logged in user's id: " +currentUser.getUser().getId());

        return accountOfCurrentUser.getBalance();
    }

    //Fully Functional / Working
	private List<Transfer> viewTransferHistory() {
		// TODO Auto-generated method stub
        Account account = accountService.getAccountFromUserId(currentUser.getUser().getId());
        List<Transfer> transferFromCurrentUser = transferService.transferFromList((long) account.getAccountid());
        List<Transfer>transferToCurrentUser = transferService.transferToList((long) account.getAccountid());
        List<Transfer>mergedList = merge(transferFromCurrentUser,transferToCurrentUser);
        System.out.println("__________________________________________________");
        System.out.println("Transactions");
        for (Transfer t: transferFromCurrentUser){
            System.out.println("ID: " + t.getId() + " To: " + t.getAccountto() + " " + NumberFormat.getCurrencyInstance().format(t.getAmount()));
        }
        for (Transfer t: transferToCurrentUser){
            System.out.println("ID: " + t.getId() + " From: " + t.getAccountfrom() + " " + NumberFormat.getCurrencyInstance().format(t.getAmount()));
        }
        System.out.println("__________________________________________________");
        return mergedList;
	}

    //Fully functional - working
	private List<Transfer> viewPendingRequests() {
		// TODO Auto-generated method stub
        Account currentUserAccount = accountService.getAccountFromUserId(currentUser.getUser().getId());
		List<Transfer> pendingTransfers = transferService.pendingTransfers((long) currentUserAccount.getAccountid());
        try{
            for (Transfer transfer : pendingTransfers) {
                System.out.println("Transaction Id: " + "(" + transfer.getId() + ") " + transfer.getAccountfrom() + " for the amount of " + transfer.getAmount());
                approveOrReject();
            }
        }catch(NullPointerException e){
            System.out.println("Please choose a valid id.");
        }

        return pendingTransfers;
	}
     public String approveOrReject(){
        Long input = (long)consoleService.promptForMenuSelection("Select transaction by id: ");
        Transfer transfer  = transferService.singlePendingTransfer(input);
        System.out.println("Id: "+ transfer.getId() + " " + transfer.getAmount() + " " + transfer.getAccountfrom());
        int choice = 0;
        while (choice != 1 || choice != 2){

            choice = consoleService.promptForMenuSelection("Approve [1] or Reject [2]");
            if (choice == 1 || choice == 2){
                break;
            }
        }
        if(choice == 1){
            transfer.setTransferstatusid(APPROVED);
            transferService.transferMoney(transfer.getAccountfrom(),transfer.getAccountto(), transfer.getAmount());
            transferService.update(transfer);

        } else {
            transfer.setTransferstatusid(REJECTED);
            transferService.update(transfer);
        }
        return "Stop being red for now";
     }

    //Fully Functional - Working
	private void sendBucks() {
		// TODO Auto-generated method stub
        //List of Available recipients
        List<User> userList = getAllUsers();

        try {
            //Capture selection of recipient
            int userSelection = consoleService.promptForInt("Select user to receive funds");

            //Getting Id of Recipient
            long sendMoneyToUser = userList.get(userSelection - 1).getId();

            if (userSelection < userList.size()) {
                Account accountOfCurrentUser = accountService.getBalance(currentUser.getUser().getId());
                Account accountofTargetUser = accountService.getBalance(sendMoneyToUser);

                BigDecimal moneyToSend = consoleService.promptForBigDecimal("Enter Your Funds: ");

                //Sends money from current user to selected user.
                transferService.transferMoney(accountOfCurrentUser.getAccountid(), accountofTargetUser.getAccountid(), moneyToSend);

                System.out.println("You sent: " + moneyToSend + " TE bucks to " + userList.get(userSelection - 1).getUsername());
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Please select a valid Id");
        }


        }

	private void requestBucks() {
		// TODO Auto-generated method stub
		List<User> userList = getAllUsers();
        try {
            int userSelection = consoleService.promptForInt("Select user to request TE bucks from");
            long requestFundsFromUser = userList.get(userSelection - 1).getId();
            Account accountFromUser = accountService.getAccountFromUserId(requestFundsFromUser);
            Account accountToCurrent = accountService.getAccountFromUserId(currentUser.getUser().getId());
            BigDecimal amountToRequest = consoleService.promptForBigDecimal("Enter request amount: ");
            //Creating a transfer request from current user - to receive funds from selected user.
            transferService.requestMoney(amountToRequest, accountToCurrent.getAccountid(), accountFromUser.getAccountid());
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Please select a valid Id.");
        }
	}

    public List<User> getAllUsers(){
        List<User> userList = accountService.getListOfUsers(currentUser.getUser().getUsername());
        int counter = 0;
        for (User user : userList){
            counter++;
            System.out.println( counter +" : " +user.getUsername());
        }
        return userList;
    }

}
