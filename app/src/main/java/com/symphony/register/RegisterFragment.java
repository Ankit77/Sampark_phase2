package com.symphony.register;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.symphony.http.WSLogout;
import com.symphony.utils.Const;
import com.symphony.utils.SymphonyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.symphony.utils.Const.HTTP_ENDPOINT;

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
    private Button btnForcrLogout;
    private AsyncLogout asyncLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        e_sampark = (E_Sampark) getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.register_fragment, container, false);
        registerBtn = (Button) v.findViewById(R.id.registerBtn);
        userNameText = (EditText) v.findViewById(R.id.userNameField);
        mobileNumberText = (EditText) v.findViewById(R.id.mobileField);
        btnForcrLogout = (Button) v.findViewById(R.id.forcrLogoutBtn);
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

        btnForcrLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userNameText.getText().toString())) {
                    SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "Please Enter Username");
                } else if (TextUtils.isEmpty(userNameText.getText().toString())) {
                    SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "Please Enter MobileNo.");
                } else {
                    displayWarningForceLogoutDialog(getActivity(), getString(R.string.app_name), "If you force logout,All your pending data from previous device are removed");
                }
            }
        });


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
                                SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "Not a valid number");
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

    /**
     * Asynctask for logout
     */
    private class AsyncLogout extends AsyncTask<String, Void, Boolean> {

        private WSLogout wsLogout;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = SymphonyUtils.displayProgressDialog(getActivity());
        }

        @Override
        protected Boolean doInBackground(String... params) {
            wsLogout = new WSLogout();
            return wsLogout.executeTown(params[0]);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            SymphonyUtils.dismissProgressDialog(progressDialog);
            if (aBoolean) {
                e_sampark.getSharedPreferences().edit().clear().commit();
                SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "You are successfully logout");
            } else {
                SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), wsLogout.getMessage());
            }
        }
    }


    public void displayWarningForceLogoutDialog(final Context context, final String title, final String message) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        if (title == null)
            alertDialog.setTitle(context.getString(R.string.app_name));
        else
            alertDialog.setTitle(title);
        alertDialog.setCancelable(false);

        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if (SymphonyUtils.isNetworkAvailable(context)) {
                    String url = HTTP_ENDPOINT + "/eSampark_Logout.asp?user=android&pass=xand123&MNO=" + mobileNumberText.getText().toString() + "&empid=" + e_sampark.getSharedPreferences().getString(Const.EMPID, "0") + "&forcelogout=1";
                    asyncLogout = new AsyncLogout();
                    asyncLogout.execute(url);
                } else {
                    SymphonyUtils.displayDialog(getActivity(), getString(R.string.app_name), "Please Check Internet Connection");
                }

            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();


            }
        });
        if (!((Activity) context).isFinishing()) {

            alertDialog.show();
        }
    }
}


