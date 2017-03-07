package com.symphony.register;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.symphony.E_Sampark;
import com.symphony.R;
import com.symphony.SymphonyGCMHome;
import com.symphony.database.CheckData;
import com.symphony.database.DB;
import com.symphony.database.OTPData;
import com.symphony.distributer.DistributerActivity;
import com.symphony.distributer.DistributerList;
import com.symphony.http.HttpManager;
import com.symphony.http.HttpStatusListener;
import com.symphony.http.OTPListener;
import com.symphony.settings.SymphonySettings;
import com.symphony.sms.SMSService;
import com.symphony.utils.Const;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyFragment extends Fragment {


    private Button verifyBtn;
    private Button resendBtn;
    private EditText otpField;
    private String userMobileNumber;
    private String userName;
    private String registrationId;
    private String timeStamp;
    private String otpNumber;


    private TextView verifyStatus;
    private ImageView verifyStatusIcon;
    private LinearLayout verifyStatusLayout;
    private LinearLayout otpLayout;

    private TextView verifyRegistrationText;
    private Button nextBtn;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private ProgressDialog mProgressBar;
    private E_Sampark e_sampark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.verify_fragment, container, false);
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        verifyBtn = (Button) v.findViewById(R.id.verifyBtn);
        resendBtn = (Button) v.findViewById(R.id.resendBtn);
        otpField = (EditText) v.findViewById(R.id.otpField);

        verifyStatus = (TextView) v.findViewById(R.id.verifyStatus);
        verifyStatusIcon = (ImageView) v.findViewById(R.id.verifyStatusIcon);
        verifyStatusLayout = (LinearLayout) v.findViewById(R.id.verifyStatusLayout);
        otpLayout = (LinearLayout) v.findViewById(R.id.otpLayout);

        verifyRegistrationText = (TextView) v.findViewById(R.id.verifyRegistrationText);
        nextBtn = (Button) v.findViewById(R.id.nextBtn);


        prefs = e_sampark.getSharedPreferences();

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mProgressBar = new ProgressDialog(getActivity());

        mProgressBar.setTitle("Verifying OTP");

        mProgressBar.setMessage("Please wait  ...");
        mProgressBar.setProgressStyle(mProgressBar.STYLE_SPINNER);
        mProgressBar.hide();
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.setCancelable(false);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            userMobileNumber = bundle.getString("usernumber");
            userName = bundle.getString("username");
            registrationId = bundle.getString("registrationId");
            timeStamp = bundle.getString("timestamp");
            otpNumber = bundle.getString("otpdata");

            otpField.setText(otpNumber);
        }


        verifyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                closeKeyBoard();


                if (TextUtils.isEmpty(otpField.getText().toString()))
                    Toast.makeText(getActivity(), "Please enter OTP code", Toast.LENGTH_LONG).show();
                else {

                    // otp number
                    Log.d("VERFY  SCREEN", otpField.getText().toString() + " " + userMobileNumber);


                    mProgressBar.show();
                    HttpManager httpManager = new HttpManager(getActivity());
                    httpManager.verifyOTP(otpField.getText().toString(), userMobileNumber, registrationId, new HttpStatusListener() {

                        @Override
                        public void onVerifyStatus(Boolean status) {
                            // TODO Auto-generated method stub


                            //Intent intent = new Intent(getActivity(),DistributerActivity.class);
                            //getActivity().startActivity(intent);


                            Log.e("INSIDE LISTENER", status + "");

                            editor = prefs.edit();
                            editor.putBoolean("isregister", status);


                            if (status == true) {

                                mProgressBar.dismiss();
                                editor.putString("usermobilenumber", userMobileNumber);


                                editor.putBoolean("isFirstTime", true);


                                ContentValues values = new ContentValues();

                                values.put(DB.USER_MOBILE, userMobileNumber);
                                values.put(DB.USER_NAME, userName);
                                values.put(DB.USER_TIMESTAMP, timeStamp);


                                getActivity().getContentResolver().insert(

                                        Uri.parse("content://com.symphony.database.DBProvider/addNewUser"),

                                        values);


                            } else {

                                mProgressBar.dismiss();
                                Toast.makeText(getActivity(), e_sampark.getSharedPreferences().getString(Const.MESSAGE, ""), Toast.LENGTH_LONG).show();


                            }
                            setVerifyOTPMessage(status);
                            editor.commit();

                        }

                        private ContentValues ContentValues() {
                            // TODO Auto-generated method stub
                            return null;
                        }

                        @Override
                        public void onDistributerListLoad(Boolean status) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onVerifyMobileStatus(Boolean status) {
                            // TODO Auto-generated method stub

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
                            // TODO Auto-generated method stub

                        }

                    });


                }


            }


        });

        resendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Intent intent = new Intent(getActivity(),DistributerActivity.class);
                //getActivity().startActivity(intent);

                closeKeyBoard();


                //	Toast.makeText(getActivity(), "Registration ID\n"+regId , Toast.LENGTH_LONG).show();
                HttpManager httpManger = new HttpManager(getActivity());
                httpManger.registerDeviceId(

                        userName,
                        userMobileNumber,
                        userMobileNumber + "@gmail.com",
                        registrationId,
                        new OTPListener() {

                            @Override
                            public void onOtpReceived(OTPData otpData) {
                                // TODO Auto-generated method stub


                                Log.e("RegisterFragment ", "OTP RECEIVED " +


                                        otpData.getOtp() + " " +
                                        otpData.isStatus());
                                mProgressBar.dismiss();

                                if (otpData.isStatus()) {


                                    otpField.setText(otpData.getOtp());


                                } else {


                                    Toast.makeText(getActivity(), "Not able to get the OTP", Toast.LENGTH_LONG).show();


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
                    /*Intent intentRegisterSMSService = new Intent(getActivity(),SMSService.class);
					intentRegisterSMSService.putExtra("username",userName);
					intentRegisterSMSService.putExtra("usernumber",userMobileNumber);					
					intentRegisterSMSService.setAction(SMSService.SEND_REGISTER_USER_INTENT);					
					getActivity().startService(intentRegisterSMSService); 
					*/
            }


        });


        nextBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                closeKeyBoard();

                boolean isRegister = prefs.getBoolean("isregister", false);

                if (isRegister) {
                    Intent intent = new Intent(getActivity(), DistributerActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();


                }


                //	Intent intent = new Intent(getActivity(),DistributerActivity.class);
                //	getActivity().startActivity(intent);


            }


        });

    }


    private void setVerifyOTPMessage(boolean status) {


        //otpLayout.setVisibility(LinearLayout.GONE);

        if (status) {

            verifyStatusIcon.setBackgroundResource(R.drawable.success);
            verifyStatus.setText(R.string.verify_success_text);
            otpField.setVisibility(TextView.GONE);
            resendBtn.setVisibility(Button.GONE);
            nextBtn.setVisibility(Button.VISIBLE);
            verifyBtn.setVisibility(Button.GONE);
            verifyRegistrationText.setVisibility(TextView.GONE);


        } else {

            verifyStatusIcon.setBackgroundResource(R.drawable.failed);
            verifyStatus.setText(R.string.verify_failed_text);
            //verifyRegistrationText.setText(R.string.verify_failed_text);
            //verifyRegistrationText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.failed,0,0,0);
            verifyRegistrationText.setVisibility(TextView.VISIBLE);

            nextBtn.setVisibility(Button.GONE);


        }

        verifyStatusLayout.setVisibility(LinearLayout.VISIBLE);


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
