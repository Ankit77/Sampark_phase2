package com.symphony.database;

public class CheckData {
	
	
	private String checkId;
	private String checkDistName;
	private boolean checkFlag;
	
	private boolean checkStatus;
	private String checkLat;
	private String checkLng;
	private String checkDistKey;
	
	private String checkUserLat;
	private String checkUserLng;
	
	public String getCheckUserLat() {
		return checkUserLat;
	}
	public void setCheckUserLat(String checkUserLat) {
		this.checkUserLat = checkUserLat;
	}
	public String getCheckUserLng() {
		return checkUserLng;
	}
	public void setCheckUserLng(String checkUserLng) {
		this.checkUserLng = checkUserLng;
	}
	public CheckData(){}
	public CheckData(String distKey , String distName , String lat , String lng , String checkStatus){
		
		this.checkDistKey = distKey;
		this.checkDistName = distName;
		this.checkLat = lat;
		this.checkLng = lng;
		this.checkStatus = Boolean.valueOf(checkStatus);
	}
	public String getCheckId() {
		return checkId;
	}
	public void setCheckId(String checkId) {
		this.checkId = checkId;
	}
	public String getCheckDistName() {
		return checkDistName;
	}
	public void setCheckDistName(String checkDistName) {
		this.checkDistName = checkDistName;
	}
	public boolean isCheckFlag() {
		return checkFlag;
	}
	public void setCheckFlag(boolean checkFlag) {
		this.checkFlag = checkFlag;
	}
	public boolean isCheckStatus() {
		return checkStatus;
	}
	public void setCheckStatus(boolean checkStatus) {
		this.checkStatus = checkStatus;
	}
	public String getCheckLat() {
		return checkLat;
	}
	public void setCheckLat(String checkLat) {
		this.checkLat = checkLat;
	}
	public String getCheckLng() {
		return checkLng;
	}
	public void setCheckLng(String checkLng) {
		this.checkLng = checkLng;
	}
	public String getCheckDistKey() {
		return checkDistKey;
	}
	public void setCheckDistKey(String checkDistKey) {
		this.checkDistKey = checkDistKey;
	}
	

}
