package com.symphony.http;

public interface HTTPConnectionListener {

	public void onTimeOut();
	
	public void onNetworkDisconnect();
}
