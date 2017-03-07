package com.symphony.database;

public class OTPData {

	
	private boolean status;
	private String otp;
	private String message;
	
	public OTPData(){}
	
	public OTPData(boolean status , String otp,String message){
		
		this.otp = otp ;
		this.status = status;
		this.message=message;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}

