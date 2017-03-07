package com.symphony.http;

import com.symphony.database.OTPData;

public interface OTPListener extends HTTPConnectionListener {

	public void onOtpReceived(OTPData otpData);
}
