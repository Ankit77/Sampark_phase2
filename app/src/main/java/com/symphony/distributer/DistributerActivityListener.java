package com.symphony.distributer;

import android.os.Bundle;

public interface DistributerActivityListener {

	void onDistributerListSelect();
	void onDistributerListItemSelect(Bundle bundle);
	void onSettingsSelect();
	void onGPSDialogOpen(String msgText);
	void onOKPressed();
	void onCanclePressed();
	void onCameraImage(String distId,String distKey,String distName);
}
