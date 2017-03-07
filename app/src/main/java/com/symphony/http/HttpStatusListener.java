package com.symphony.http;

import com.symphony.database.CheckData;

public interface HttpStatusListener extends HTTPConnectionListener {

	public void onVerifyStatus(Boolean status);
	public void onDistributerListLoad(Boolean status);
	public void onVerifyMobileStatus(Boolean status);
	public void onCheckStatus(CheckData checkData);
	

	
}
