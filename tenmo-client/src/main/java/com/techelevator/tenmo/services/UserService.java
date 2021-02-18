package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.techelevator.projects.model.ToFromTransfer;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Balance;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.TransferDetails;
import com.techelevator.tenmo.models.User;
import com.techelevator.view.ConsoleService;

import io.cucumber.core.plugin.JSONFormatter;

public class UserService {
	
	
	  public static String AUTH_TOKEN = "";
	  private final String INVALID_RESERVATION_MSG = "Invalid data. Please try again";
	  private final String BASE_URL = "http://localhost:8080";
	  private final RestTemplate restTemplate = new RestTemplate();


	  
	  
	  public Balance getBalanceObject() {
		  
		  Balance currentBalance = new Balance();
		  try {
			  currentBalance=restTemplate.exchange(BASE_URL+"/get-balance", HttpMethod.GET, makeAuthEntity(), Balance.class).getBody();
			  return currentBalance;
			  
		  }catch(Exception e) {
			  System.out.println("Please try again");
		  }
		  
		  return null;
		  
	  }

	  private HttpEntity makeAuthEntity() {
			 
		    HttpHeaders headers = new HttpHeaders();
		    headers.setBearerAuth(AUTH_TOKEN);
		    HttpEntity entity = new HttpEntity<>(headers);
		    return entity;
	  }
	  private HttpEntity makeTransferAuthEntity(Transfer transfer) {
		  HttpHeaders headers = new HttpHeaders();
		  headers.setBearerAuth(AUTH_TOKEN);
		  HttpEntity entity=new HttpEntity<>(transfer, headers);
		  return entity;
	  }
	  
	  public void toTransfer(AuthenticatedUser currentUser){
		  
		  try {
			  User[] myUserList=restTemplate.exchange(BASE_URL+"/user-list", HttpMethod.GET, makeAuthEntity(), User[].class).getBody();
			
			  for(User userResults : myUserList) {
				  if(userResults.getId()!=currentUser.getUser().getId()) {			//how to get id for current user session
					  System.out.println( "user Id: " + userResults.getId() +" User: "+ userResults.getUsername());
				  }
			  }
			  
		  }catch(Exception e) {
			  System.out.println("Please try again");
		  }
		  		  
		  
	  }
		 
	  public void executeTransfer(Integer toId, Integer fromId, Double amount) {
		  
		  Transfer transfer=new Transfer();
		  transfer.setToId(toId);
		  transfer.setFromId(fromId);
		  transfer.setAmount(amount);
		  
		  
		 Transfer returnTran = restTemplate.exchange(BASE_URL+"/money-transfer", HttpMethod.POST, makeTransferAuthEntity(transfer), Transfer.class).getBody();
		  
		  if(returnTran == null) {
		  		  System.err.println("Transaction unsuccessful!");
		 
		  }else {
			  System.out.println("Transaction successful!");
		  
	 }
		  
		  
		  }  

	  
	  public ToFromTransfer[] toFromTransfer() {
			ToFromTransfer[] tFTransfer = restTemplate.exchange(BASE_URL + "/fromTransfer", HttpMethod.GET, makeAuthEntity(), ToFromTransfer[].class).getBody();
			return tFTransfer;
		}
	  
	  public  TransferDetails[] transdetails (int id)  {
			
			TransferDetails []   tDetails = restTemplate.exchange(BASE_URL + "/transdetails/" + id ,HttpMethod.GET, makeAuthEntity(), TransferDetails[].class).getBody();
		
		
			return tDetails;
		}
	  
	  }
	  
	  
	  
	  
	  

