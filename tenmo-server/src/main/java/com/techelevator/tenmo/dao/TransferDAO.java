package com.techelevator.tenmo.dao;

import java.security.Principal;

import com.techelevator.tenmo.controller.Transfer;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.TransferException;
import com.techelevator.tenmo.model.TransferException.TransferExecutionException;

public interface TransferDAO {
	
	public Balance getUserBalance(int userId);
	
	public Transfer moneyTransfer(Transfer transfer);
	
	

}
