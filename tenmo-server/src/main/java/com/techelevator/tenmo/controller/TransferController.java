package com.techelevator.tenmo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.TransferSqlDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.ToFromTransfer;
import com.techelevator.tenmo.model.TransferDetails;
import com.techelevator.tenmo.model.TransferException.TransferExecutionException;
import com.techelevator.tenmo.model.User;


@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
	

	@Autowired
	TransferDAO transferDao;
	
	
	@Autowired
	UserDAO userDao;
	
	@Autowired
	TransferSqlDAO transferSqlDao;
	
	@RequestMapping(path="/get-balance", method=RequestMethod.GET)
	public Balance getBalance(Principal principal) {
		int id=userDao.findIdByUsername(principal.getName());
		Balance balance=transferDao.getUserBalance(id);
		return balance;
	}
	
	@RequestMapping(path="/user-list", method=RequestMethod.GET)
	public List<User> userSelect(){
		
		
	List<User> userList = userDao.findAll();
		
			return userList;
	}
	
	@RequestMapping(path="/money-transfer", method=RequestMethod.POST)
	public Transfer moneyTransferMethod(@RequestBody Transfer transfer)  {
		
		System.out.println(transfer.getFromId() + " " + transfer.getToId() + " " + transfer.getAmount());
		
		Transfer finishedTransfer=transferDao.moneyTransfer(transfer);
		
		return finishedTransfer;
	}
	
	
	@RequestMapping(path = "/fromTransfer", method = RequestMethod.GET)
	public List<ToFromTransfer> myToFromTransfer(Principal principal) {
		int id = userDao.findIdByUsername(principal.getName());
		List<ToFromTransfer> fromTransfer = transferSqlDao.myFromTransfers(id);
	
		
		return fromTransfer;
	}
	@RequestMapping (path = "/transdetails/{id}", method = RequestMethod.GET)
	public List <TransferDetails> tdetails (@PathVariable int id ) {
		
		List <TransferDetails>  transdetails =   transferSqlDao.transDetails(id);
		
		return  transdetails;
	}
}
