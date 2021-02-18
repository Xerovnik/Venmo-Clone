package com.techelevator.tenmo.model;
 import org.springframework.http.HttpStatus;
	 import org.springframework.web.bind.annotation.ResponseStatus;
	 
public class TransferException {
	 
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	 public class TransferExecutionException extends Exception {
	     private static final long serialVersionUID = 1L;
	 }

}
