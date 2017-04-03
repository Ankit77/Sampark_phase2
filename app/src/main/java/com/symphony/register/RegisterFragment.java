package com.symphony.register;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.SymphonyGCMHome;
import com.symphony.database.CheckData;
import com.symphony.database.OTPData;
import com.symphony.http.HttpManager;
import com.symphony.http.HttpStatusListener;
import com.symphony.http.OTPListener;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegisterFragment extends Fragment {


    private Button registerBtn;
    private EditText userNameText;
    private EditText mobileNumberText;
    private ProgressDialog mProgressBar;
    private String MX_HTTP_SERVER = "180.150.249.57";
    private String MX_HTTP_PORT = "900";
    private String INT_HTTP_SERVER = "61.12.85.74";
    private String INT_HTTP_PORT = "900";
    private E_Sampark e_sampark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.register_fragment, container, false);
        registerBtn = (Button) v.findViewById(R.id.registerBtn);
        userNameText = (EditText) v.findViewById(R.id.userNameField);
        mobileNumberText = (EditText) v.findViewById(R.id.mobileField);
        return v;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (mProgressBar != null) {
            mProgressBar = null;
        }

        mProgressBar = new ProgressDialog(getActivity());

        mProgressBar.setTitle("Registering");

        mProgressBar.setMessage("Please wait  ...");
        mProgressBar.setProgressStyle(mProgressBar.STYLE_SPINNER);
        mProgressBar.hide();
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(false);


        registerBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                /** by pass**/

                //Intent byPass = new Intent(getActivity(),DistributerActivity.class);
                //startActivity(byPass);

                /** by pass**/


                closeKeyBoard();


                StringBuilder errMessage = new StringBuilder();


                if (TextUtils.isEmpty(userNameText.getText())) {


                    errMessage.append("Please enter User name");


                }


                if (TextUtils.isEmpty(mobileNumberText.getText())) {


                    if (errMessage.length() == 0)
                        errMessage.append("Please enter Mobile number");
                    else
                        errMessage.append(" & Mobile number ");

                }


                if (mobileNumberText.getText().toString().length() < 10) {


                    if (errMessage.length() == 0)
                        errMessage.append("Mobile number must be 10 digit");
                    else
                        errMessage.append(" & Mobile number must be 10 digit");

                }

                if (errMessage.length() > 0) {
                    Toast.makeText(getActivity(), errMessage, Toast.LENGTH_LONG).show();

                } else {


                    String userName = userNameText.getText().toString();

                    //INT
                    if (userName.contains("MX")) {

                        Log.e("Username check ", "Contains MX " + userName);
                        SymphonyUtils.setMasterIp(getActivity(), MX_HTTP_SERVER, MX_HTTP_PORT);

                    } else if (userName.contains("INT")) {
                        Log.e("Username check ", "Contains INT " + userName);
                        SymphonyUtils.setMasterIp(getActivity(), INT_HTTP_SERVER, INT_HTTP_PORT);
                    } else {
                        //	SymphonyUtils.setMasterIp(getActivity(),HTTP_SERVER, HTTP_PORT);
                        SymphonyUtils.setMasterIp(getActivity(), null, null);
                        Log.e("Username check ", "Does not Contain " + userName);
                    }
                    mProgressBar.show();
                    HttpManager httpManager = new HttpManager(getActivity());

                    httpManager.checkMobileNumber(mobileNumberText.getText().toString(), new HttpStatusListener() {

                        @Override
                        public void onVerifyStatus(Boolean status) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onDistributerListLoad(Boolean status) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onVerifyMobileStatus(Boolean status) {
                            // TODO Auto-generated method stub


                            if (status) {

                                final String regId = SymphonyGCMHome.getRegistrationId(getActivity());
                                HttpManager httpManger = new HttpManager(getActivity());
                                httpManger.registerDeviceId(
                                        userNameText.getText().toString(),
                                        mobileNumberText.getText().toString(),
                                        mobileNumberText.getText().toString() + "@gmail.com",
                                        regId,
                                        new OTPListener() {

                                            @Override
                                            public void onOtpReceived(OTPData otpData) {
                                                // TODO Auto-generated method stub
                                                if (otpData == null) {
                                                    mProgressBar.dismiss();
                                                    Toast.makeText(getActivity(), e_sampark.getSharedPreferences().getString(Const.MESSAGE, ""), Toast.LENGTH_LONG).show();
                                                    return;
                                                }
                                                Log.e("RegisterFragment ", "OTP RECEIVED " +
                                                        otpData.getOtp() + " " +
                                                        otpData.isStatus());
                                                mProgressBar.dismiss();

                                                if (otpData.isStatus()) {
                                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss a");
                                                    String currentDateandTime = sdf.format(new Date()).replace(" ", "");
                                                    currentDateandTime = currentDateandTime.replace(".", "");

                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("usernumber", mobileNumberText.getText().toString());
                                                    bundle.putString("username", userNameText.getText().toString());
                                                    bundle.putString("otpdata", otpData.getOtp());
                                                    bundle.putString("registrationId", regId);
                                                    bundle.putString("timestamp", currentDateandTime);

                                                    Log.e("Registration Id ", regId + "");

                                                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                                    VerifyFragment verifyFragment = new VerifyFragment();
                                                    verifyFragment.setArguments(bundle);

                                                    ft.replace(R.id.homeFragment, verifyFragment).addToBackStack("verify").commit();


                                                } else {


                                                    Toast.makeText(getActivity(), e_sampark.getSharedPreferences().getString(Const.MESSAGE, ""), Toast.LENGTH_LONG).show();

                                                }


                                            }

                                            @Override
                                            public void onTimeOut() {
                                                // TODO Auto-generated method stub

                                            }

                                            @Override
                                            public void onNetworkDisconnect() {
                                                // TODO Auto-generated method stub

                                            }


                                        });


                            } else {

                                mProgressBar.dismiss();
                                SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), getString(R.string.alert_not_valid_number));
                            }
                        }

                        @Override
                        public void onTimeOut() {
                            // TODO Auto-generated method stub
                            mProgressBar.dismiss();
                            Toast.makeText(getActivity(), "Request Timeout occurs , please try again", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onNetworkDisconnect() {
                            // TODO Auto-generated method stub
                            mProgressBar.dismiss();
                            Toast.makeText(getActivity(), "Network not available at this moment", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCheckStatus(CheckData checkData) {

                        }


                    });


                }

			
				
              /* FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                
                ft.replace(R.id.homeFragment, new VerifyFragment()).addToBackStack("verify").commit();
				
				*/

				/*Intent intent = new Intent(getActivity(),DistributerActivity.class);
                getActivity().startActivity(intent);
				
				Intent intentService = new Intent(getActivity(),SMSService.class);
				intentService.setAction(SMSService.FETCH_LOCATION_INTENT);
				getActivity().startService(intentService);*/ 

				/*HttpManager httpManager = new HttpManager(getActivity());
				httpManager.getDistributers("9375494877"); */


            }
        });


    }


    private void closeKeyBoard() {


        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        if (inputManager != null) {
            if (getActivity().getCurrentFocus() != null)
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }


    }


}
