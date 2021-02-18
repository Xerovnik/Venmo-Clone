package com.techelevator.tenmo.dao;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.controller.Transfer;
import com.techelevator.tenmo.model.Balance;
import com.techelevator.tenmo.model.ToFromTransfer;
import com.techelevator.tenmo.model.TransferDetails;

@Component
public class TransferSqlDAO implements TransferDAO{
	
	private JdbcTemplate jdbcTemplate;
	
	public TransferSqlDAO(DataSource dataSource){
		jdbcTemplate=new JdbcTemplate(dataSource);
		
	}

	@Override
	public Balance getUserBalance(int userId) {
		String sql="SELECT balance FROM accounts WHERE user_id=?";
		SqlRowSet results=jdbcTemplate.queryForRowSet(sql, userId);
		
		Balance userBalance=new Balance();
		if (results.next()) {
			
			double balanceFromDatabase = results.getDouble("balance");
			
			userBalance.setBalance(balanceFromDatabase);
		}
		return userBalance;
	}

	@Override
	public Transfer moneyTransfer(Transfer transfer) {
Double balance = getUserBalance(transfer.getFromId()).getBalance();
		Double amountVal = transfer.getAmount();
		
		System.err.println(amountVal + "  "+ balance);
		
		  if(balance >= amountVal) {
		  
		 
		Double tranAmount = transfer.getAmount();
		Integer userID = transfer.getToId();
		Integer currentUser = transfer.getFromId(); 
		
		String sql_fromUser="update accounts set balance = balance - ? where user_id = ?";
		jdbcTemplate.update(sql_fromUser, transfer.getAmount(), transfer.getToId());
		
		String sql_toUser="update accounts set balance = balance + ? where user_id = ?";
		jdbcTemplate.update(sql_toUser, transfer.getAmount(), transfer.getFromId());
		
		String sqlTransfers = "INSERT INTO transfers (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
		jdbcTemplate.update(sqlTransfers, currentUser, userID, tranAmount);
		  }else {
			  System.err.println("not enough funds");
			  
			  transfer = null;
		  }
		
		
return transfer;
	}
	
	public List<ToFromTransfer> myFromTransfers(int pId) {
		List<ToFromTransfer> fromTrans = new ArrayList<>();
		String sqlFromTransfer = "select transfers.transfer_id, users.username, transfers.amount "
				+ "from transfers "
				+ "inner join accounts on transfers.account_from = accounts.account_id "
				+ "inner join users on accounts.user_id = users.user_id "
				+ "where transfers.account_to =? "
				+ "UNION "
				+ "select transfers.transfer_id, users.username, transfers.amount "
				+ "from transfers "
				+ "inner join accounts on transfers.account_to = accounts.account_id "
				+ "inner join users on accounts.user_id = users.user_id "
				+ "where transfers.account_from =? ";
		SqlRowSet fromResult = jdbcTemplate.queryForRowSet(sqlFromTransfer, pId, pId);
	
		while (fromResult.next()) {
			int transId = fromResult.getInt("transfer_id");
			String name = fromResult.getString("username");
			Double amount = fromResult.getDouble("amount");
			ToFromTransfer transfer = new ToFromTransfer(transId, name, amount);
			fromTrans.add(transfer);
		
		}
		
		return fromTrans;
	}
public List <TransferDetails> transDetails (int tId) {
		
		List <TransferDetails> tDetails = new ArrayList<>();
		
		String sql = "select transfers.transfer_id as id, "
				+ "(select users.username from transfers "
				+ "inner join accounts on transfers.account_to = accounts.account_id "
				+ "inner join users on accounts.user_id = users.user_id "
				+ "where transfers.transfer_id =?) as From, "
				+ "users.username as to, transfer_types.transfer_type_desc as Type, "
				+ "transfer_statuses.transfer_status_desc as status, transfers.amount "
				+ "from transfers "
				+ "inner join accounts on transfers.account_from = accounts.account_id "
				+ "inner join users on accounts.user_id = users.user_id "
				+ "inner join transfer_types on transfer_types.transfer_type_id = transfers.transfer_type_id "
				+ "inner join transfer_statuses on transfer_statuses.transfer_status_id = transfers.transfer_status_id "
				+ "where transfers.transfer_id = ?";
		
		SqlRowSet results = jdbcTemplate.queryForRowSet(sql, tId, tId);
		while (results.next()) {
			int id = results.getInt("id");
			String from = results.getString("from");
			String to = results.getString("to");
			String type = results.getString("type");
			String status = results.getString("status");
			double amount = results.getDouble("amount");
			System.out.println(from);
			
						
			TransferDetails transferDetails = new TransferDetails(id, from, to, type, status, amount);
			
			tDetails.add(transferDetails);
			
			
		}
		
		return tDetails;
	}
}
